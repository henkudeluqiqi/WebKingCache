package org.king2.webkcache.cache.interfaces.impl;

import org.king2.webkcache.cache.appoint.WebCacheTypeIsObjAppoint;
import org.king2.webkcache.cache.definition.CacheDefinition;
import org.king2.webkcache.cache.interfaces.WebKingCache;
import org.king2.webkcache.cache.lock.SubSectionLock;
import org.king2.webkcache.cache.pojo.ReadWritePojo;
import org.king2.webkcache.cache.pojo.WebKingCacheTypeIsObjDataCenter;
import org.king2.webkcache.cache.timer.DefaultWebKingCacheTimer;

import java.util.Date;
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
public class DefaultWebKingCache implements WebKingCache {

    // 提供超时的构造
    public DefaultWebKingCache() {
        this(1000 * 60 * 60 * 2);
    }

    public final static AtomicInteger SIZE = new AtomicInteger(0);

    public DefaultWebKingCache(Integer timeout) {
        if (timeout != null) {
            WebKingCacheTypeIsObjDataCenter.timeout = timeout;
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


        // 校验值是否正确 是否为空
        WebCacheTypeIsObjAppoint.checkKeyIsEmpty(key);

        // 获取锁
        ReadWritePojo lock = SubSectionLock.getLock(key);
        lock.reentrantReadWriteLock.writeLock().lock();

        try {
            // 创建返回的数据
            CacheDefinition returnObj = null;
            // 校验通过首先获取到返回的值
            returnObj = lock.data.get(key);

            // 初始化对象的信息
            CacheDefinition cacheDefinition = new CacheDefinition(value, saveFlag, timeout);

            // 将信息存入数据中
            CacheDefinition put = lock.data.put(key, cacheDefinition);
            if (put == null) {
                SIZE.getAndIncrement();
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

    @Override
    public Object remove(String key) {

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
}


