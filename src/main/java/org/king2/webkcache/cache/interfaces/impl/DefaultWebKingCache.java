package org.king2.webkcache.cache.interfaces.impl;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.king2.webkcache.cache.appoint.WebCacheTypeIsObjAppoint;
import org.king2.webkcache.cache.consumer.PrConsumer;
import org.king2.webkcache.cache.definition.CacheDefinition;
import org.king2.webkcache.cache.interfaces.WebKingCache;
import org.king2.webkcache.cache.lock.SubSectionLock;
import org.king2.webkcache.cache.pojo.*;
import org.king2.webkcache.cache.task.TaskThreadPool;
import org.king2.webkcache.cache.timer.DefaultWebKingCacheTimer;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * =======================================================
 * 说明:
 * 这是一个线程安全的WebKingCache实现类 用户在高并发的时候不用担心数据的安全
 * 因为这个类，已经完成了同步的处理，使用者只需要关心自己的业务逻辑即可。
 * <p>
 * 作者		时间					            注释
 *
 * @author 俞烨        19-10-14                         创建
 * =======================================================
 */
@Log4j
public class DefaultWebKingCache implements WebKingCache {

    // 提供超时的构造
    public DefaultWebKingCache() {
        this(1000 * 60 * 60 * 2);
    }

    public final static AtomicInteger SIZE = new AtomicInteger(0);

    /**
     * 用户记录当前数据存在文本中的哪一行
     */
    @Getter
    private static Map<Integer, Map<String, Integer>> RECORD_VALUE_LINE = null;

    /**
     * 用来记录当前文件总共有多少行
     */
    @Getter
    private static Map<Integer, Integer> RECORD_FILE_TOTLE_SIZE = null;

    // 服务器的数据
    public volatile ServerProperties serverProperties;

    public DefaultWebKingCache(Integer timeout, ServerProperties serverProperties) {
        if (timeout != null) {
            WebKingCacheTypeIsObjDataCenter.timeout = timeout;
        }

        // 解析配置文件是否为空
        if (serverProperties != null) {
            // 并检查是否需要开启配置项
            File file = new File(serverProperties.getPrPath());
            if (serverProperties.isActivePr() && file.exists()) {
                this.RECORD_VALUE_LINE = new ConcurrentHashMap<>();
                // 初始化RECORD_VALUE_LINE
                for (Integer i = 0; i <= SubSectionLock.SUB_SECTION_LOCK_SIZE; i++) {
                    this.RECORD_VALUE_LINE.put(i, new ConcurrentHashMap<>());
                }
                this.RECORD_FILE_TOTLE_SIZE = new ConcurrentHashMap<>();
                this.serverProperties = serverProperties;
                // 我们需要进行初始化快照数据
                serverProperties.setActivePr(true);
                // 开启同步持久化的数据到本地内存当中
                try {
                    openPrDataGotoMemory();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 初始化压缩队列
                TaskThreadPool.getInstance().init();
                // 开启压缩消费者
                PrConsumer.openConsumer();
            } else {
                serverProperties.setActivePr(false);
            }
        } else {
            serverProperties.setActivePr(false);
        }
    }

    /**
     * 同步持久化的数据到缓存
     */
    private void openPrDataGotoMemory() throws ExecutionException, InterruptedException {


        // 获取到该路径下的所有文件信息
        // 定义存放File的集合
        List<File> files = new ArrayList<>();
        // 首先获取到所有的数据先
        for (Integer i = 0; i <= SubSectionLock.SUB_SECTION_LOCK_SIZE; i++) {
            File file = new File(serverProperties.getPrPath() + "/" + PrConsumer.PR_PATH + i);
            if (file.exists()) {
                files.add(file);
            }
        }


        // 获取到了所有的数据后 我们需要开始同步数据到缓存中
        if (CollectionUtils.isEmpty(files)) {
            return;
        }

        // 定义任务的集合
        List<Future<String>> tasks = new ArrayList<>();
        for (File file : files) {
            // 首先取出所有的数据信息并同步
            tasks.add(TaskThreadPool.getInstance().getPOOL().submit(new Callable<String>() {
                @Override
                public String call() throws Exception {

                    // 开始读取数据
                    FileReader fileReader = null;
                    BufferedReader bufferedReader = null;
                    try {

                        fileReader = new FileReader(file);
                        bufferedReader = new BufferedReader(fileReader);

                        // 开始读取数据
                        String readStr = null;
                        while ((readStr = bufferedReader.readLine()) != null) {
                            // 我们需要将数据重新转成byte数组并解压缩
                            byte[] bytes = JSON.parseObject(readStr, byte[].class);

                            // 进行解压
                            byte[] unzip = PrConsumer.unzip(bytes);
                            PrData prData = JSON.parseObject(new String(unzip), PrData.class);
                            set(prData);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    } finally {
                        if (fileReader != null) fileReader.close();
                        if (bufferedReader != null) bufferedReader.close();
                    }
                    return file.getPath();
                }
            }));
        }

        for (Future task : tasks) {
            Object o = task.get();
            if (o != null) {
                log.info("路径" + o + "同步成功");
            } else {
                log.error("路径" + o + "同步失败");
            }
        }
    }

    public DefaultWebKingCache(Integer timeout) {
        this(timeout, null);
    }

    /**
     * 同步时用到的set集合
     */
    private void set(PrData prData) throws Exception {
        if (prData == null) return;
        CacheDefinition cacheDefinition = prData.getCacheDefinition();
        set(prData.getKey(),
                cacheDefinition.object,
                cacheDefinition.isHandTimer.get(),
                cacheDefinition.timeout, false);
    }

    private Object set(String key, Object value, boolean saveFlag, Integer timeout, boolean pr) throws Exception {
        // 校验值是否正确 是否为空
        WebCacheTypeIsObjAppoint.checkKeyIsEmpty(key);
        // 获取锁
        ReadWritePojo lock = SubSectionLock.getLock(key);
        lock.reentrantReadWriteLock.writeLock().lock();

        try {
            // 创建返回的数据
            CacheDefinition returnObj = null;

            // 校验通过首先获取到返回的值
            ConcurrentHashMap<String, CacheDefinition> data = lock.data;

            // 初始化对象的信息
            CacheDefinition cacheDefinition = new CacheDefinition(value, saveFlag, timeout);

            // 将信息存入数据中
            returnObj = data.put(key, cacheDefinition);
            if (returnObj == null) {
                // 说明没有相同的值 数量+1
                SIZE.getAndIncrement();
                // 判断是否需要持久化 如果需要持久化的话 那么就需要提交给任务队列并去处理这个信息
                if (pr && saveFlag) prDataGotoFile(key, cacheDefinition);
            }

            // ------------------------------------------------------------------------------------------------------------
            // 存入成功记住将缓存的本次缓存的时间存入一级缓存计时器中 防止该缓存为无用数据 需要将无用的数据进行清理
            // 如果说这个用户是永久存入缓存中的话 那么就不需要将该数据交给监视器
            // saveFlag true: 永久存在 false: 需要交给监视器监听
            // ------------------------------------------------------------------------------------------------------------
            if (!saveFlag) {
                DefaultWebKingCacheTimer.TIME.put(key, new Date());
                DefaultWebKingCacheTimer.open();
            }

            return returnObj == null ? null : returnObj.object;
        } catch (Exception e) {
            throw e;
        } finally {
            lock.reentrantReadWriteLock.writeLock().unlock();
        }
    }

    /**
     * 将数据放入缓存中  这是一个万能的Set方法
     * 但是使用者要注意一定 那就是存进去的类型和取出来的类型要一致 否则使用者在强转的中会抛出异常
     * 这个方法不提供自己失效的时间  但是系统提供了一个缓存监视器'CountCurrentCacheIfPastTypeObj'
     * CountCurrentCacheIfPastTypeObj监视器会根据用户配置的毫秒数来监听这个数据是否需要删除  是否需要回收,以此达到释放内存的效果。
     * 如果这个缓存数据要一直存储在内存中可以将参数@Param saveFlag 设置成true这样就会永远保存在内存中,
     * 也可以调用set(String key, Object value) 方法 这个方法也是将缓存数据永久存在内存中。
     *
     * @param key   缓存中的key
     * @param value 缓存中的值
     * @return
     * @Param saveFlag 数据是否永久存在
     */
    @Override
    public Object set(String key, Object value, boolean saveFlag, Integer timeout) throws Exception {
        return set(key, value, saveFlag, timeout, true);
    }

    /**
     * 将数据持久化的信息提交给队列
     *
     * @param key
     * @param cacheDefinition
     */
    private void prDataGotoFile(final String key, final CacheDefinition cacheDefinition) {

        if (serverProperties.isActivePr()) {
            TaskThreadPool.getInstance().getPOOL().execute(() -> {
                Thread.currentThread().setName("生产：同步数据线程" + System.currentTimeMillis());
                ConcurrentLinkedQueue<PrData> prData = TaskThreadPool
                        .getInstance()
                        .get(key);
                synchronized (prData) {
                    Integer subSectionLockSize = SubSectionLock.SUB_SECTION_LOCK_SIZE;
                    Integer value = RECORD_FILE_TOTLE_SIZE.get(subSectionLockSize);
                    RECORD_VALUE_LINE.get(key.hashCode() & subSectionLockSize).put(key, value == null ? 0 : value);
                    RECORD_FILE_TOTLE_SIZE.put(subSectionLockSize, value == null ? 0 : value + 1);
                    prData.add(new PrData(key, cacheDefinition, serverProperties.getPrPath()));
                    prData.notifyAll();
                }
            });
        }
    }

    /**
     * 参照 set(String key, Object value, boolean saveFlag) throws Exception;的注释
     * 默认这个缓存对象是交给缓存监视器管理的
     */
    @Override
    public Object set(String key, Object value, boolean saveFlag) throws Exception {
        return set(key, value, true, WebKingCacheTypeIsObjDataCenter.timeout);
    }

    /**
     * 参照 set(String key, Object value, boolean saveFlag) throws Exception;的注释
     * 默认这个缓存对象是交给缓存监视器管理的
     */
    @Override
    public Object set(String key, Object value) throws Exception {
        return set(key, value, true);
    }


    /**
     * 调用CacheRecycle缓存回收器
     */
    public void cr() {
        DefaultWebKingCacheTimer.DEFAULT_THREAD.interrupt();
    }


    /**
     * 通过key获取值
     *
     * @param key
     * @return
     */
    @Override
    public Object get(String key) throws Exception {


        // 定义返回的数据类型
        Object value = null;
        // 校验值是否正确 是否为空
        WebCacheTypeIsObjAppoint.checkKeyIsEmpty(key);
        // 是否已经释放了读锁
        boolean flag = false;

        // 开启读锁
        ReadWritePojo lock = SubSectionLock.getLock(key);
        lock.reentrantReadWriteLock.readLock().lock();

        try {
            // 判断是否过期
            if (lock.data.get(key) != null && DefaultWebKingCacheTimer.isPast(key, lock.data.get(key).timeout, new Date())) {
                // 过期了需要删除对应文件中持久化的数据
                removePrData(key);
                // 释放读锁，升级写锁
                flag = true;
                lock.reentrantReadWriteLock.readLock().unlock();
                lock.reentrantReadWriteLock.writeLock().lock();
                try {
                    lock.data.remove(key);
                    DefaultWebKingCacheTimer.TIME.remove(key);
                    SIZE.addAndGet(-1);
                } catch (Exception e) {
                    throw e;
                } finally {
                    lock.reentrantReadWriteLock.writeLock().unlock();
                }
                return null;
            }
            // 取出数据的信息
            value = lock.data.get(key) == null ? null : lock.data.get(key).object;
            return value;
        } catch (Exception e) {
            throw e;
        } finally {
            if (!flag) lock.reentrantReadWriteLock.readLock().unlock();
        }
    }

    /**
     * 删除对应文件中持久化的数据
     *
     * @param key
     */
    public void removePrData(String key) {
        if (serverProperties.isActivePr()) {
            TaskThreadPool.getInstance().getPOOL().execute(() -> {
                ConcurrentLinkedQueue<RemoveKeyData> removeKeys = TaskThreadPool.getInstance().getRemoveKeys();
                synchronized (removeKeys) {
                    removeKeys.add(new RemoveKeyData(key, serverProperties.getPrPath()));
                    removeKeys.notifyAll();
                }
            });
        }
    }

    @Override
    public Object remove(String key) throws Exception {

        if (SIZE.get() <= 0) {
            return null;
        }
        // 获取锁信息
        ReadWritePojo lock = SubSectionLock.getLock(key);
        lock.reentrantReadWriteLock.writeLock().lock();
        Object returnObj = null;
        try {
            CacheDefinition remove = lock.data.remove(key);
            if (remove == null) return null;
            DefaultWebKingCacheTimer.TIME.remove(key);
            returnObj = remove.object;
            SIZE.addAndGet(-1);
            removePrData(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.reentrantReadWriteLock.writeLock().unlock();
        }
        return returnObj;
    }

    @Override
    public int size() {
        return SIZE.get();
    }


    @Override
    public boolean containsKey(String key) throws Exception {
        return get(key) != null;
    }
}


