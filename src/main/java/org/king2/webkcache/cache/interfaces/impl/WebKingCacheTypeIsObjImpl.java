package org.king2.webkcache.cache.interfaces.impl;

import org.king2.webkcache.cache.appoint.WebCacheTypeIsObjAppoint;
import org.king2.webkcache.cache.interfaces.WebKingCacheHandleDataInterface;
import org.king2.webkcache.cache.pojo.WebKingCacheTypeIsObjDataCenter;
import org.king2.webkcache.cache.timer.CountCurrentCacheIfPastTypeObj;
import org.king2.webkcache.cache.definition.CacheDefinition;
import org.king2.webkcache.cache.lock.WebKingCacheTypeIsObjectLock;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * =======================================================
 * 说明:  可以WebKCache缓存接口的实现类 类型为Obj
 * <p>
 * 作者		时间					            注释
 *
 * @author 俞烨        19-10-14                         创建
 * =======================================================
 */
public class WebKingCacheTypeIsObjImpl implements WebKingCacheHandleDataInterface {

    // 提供超时的构造
    public WebKingCacheTypeIsObjImpl() {
        this(1000 * 60 * 60 * 2);
    }

    public WebKingCacheTypeIsObjImpl(Integer timeout) {
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
    public Object set(String key, Object value, boolean saveFlag) throws Exception {

        /*
        这是一个set数据的方法 具有同步的效果 所以使用者无需担心多线程的情况下数据安不安全的问题
        使用者只管一个使用 中间的操作都由WebKCache帮你实现
         */


        // 创建读写分离锁
        ReentrantReadWriteLock lock = WebKingCacheTypeIsObjectLock.getLock();
        lock.writeLock().lock();
        try {
            // 获取类型为Obj的缓存数据
            WebKingCacheTypeIsObjDataCenter objInstance = WebKingCacheTypeIsObjDataCenter.getInstance();
            // 开启锁 防止多线程的环境下数据不安全的问题

            // 创建返回的数据
            CacheDefinition returnObj = null;

            // 校验值是否正确 是否为空
            WebCacheTypeIsObjAppoint.checkKeyIsEmpty(key);

            // 校验完成后讲数据存入缓存中去
            ConcurrentHashMap<String, CacheDefinition> cacheData = objInstance.getDatasMap();
            returnObj = cacheData.get(key);

            // 将数据存入缓存中
            CacheDefinition definition = new CacheDefinition();
            definition.isPereExist.set(saveFlag);
            definition.object = value;
            definition.isHandTimer.set(!saveFlag);
            cacheData.put(key, definition);
            // ------------------------------------------------------------------------------------------------------------
            // 存入成功记住将缓存的本次缓存的时间存入一级缓存计时器中 防止该缓存为无用数据 需要将无用的数据进行清理
            // 如果说这个用户是永久存入缓存中的话 那么就不需要将该数据交给监视器
            // saveFlag true: 永久存在 false: 需要交给监视器监听
            // ------------------------------------------------------------------------------------------------------------
            if (!saveFlag) {
                CountCurrentCacheIfPastTypeObj.getInstance().getTimers().put(key, new Date());
                // 开启计时器
                CountCurrentCacheIfPastTypeObj.openTimers();
                // 唤醒
                WebKingCacheTypeIsObjectLock.writeCondition().signalAll();
            }
            return returnObj == null ? null : returnObj.object;
        } catch (Exception e) {
            throw e;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 参照 set(String key, Object value, boolean saveFlag) throws Exception;的注释
     * 默认这个缓存对象是交给缓存监视器管理的
     */
    @Override
    public Object set(String key, Object value) throws Exception {
        return set(key, value, false);
    }


    /**
     * 调用CacheRecycle缓存回收器
     */
    public void cr() {
        // 获取类型为Obj的缓存数据
        WebKingCacheTypeIsObjDataCenter objInstance = WebKingCacheTypeIsObjDataCenter.getInstance();
        objInstance.notifyAll();
    }


    /**
     * 通过key获取值
     *
     * @param key
     * @return
     */
    @Override
    public Object get(String key) throws Exception {


        // ----------------------------------------------------------------------
        // 这是Get数据的方法  使用者只管提供对应的Key 我会将Value返回
        // ----------------------------------------------------------------------

        // 获取类型为Obj的缓存数据
        WebKingCacheTypeIsObjDataCenter objInstance = WebKingCacheTypeIsObjDataCenter.getInstance();
        // 由于Get()和Set()方法操作的是同一个缓存数据 所以他们加的锁类型也应该是一致的 否则就会出现安全隐患


        // 创建读写分离锁
        ReentrantReadWriteLock lock = WebKingCacheTypeIsObjectLock.getInstance().getLock();
        lock.readLock().lock();
        try {
            // 开启锁成功 查询Key是否属于规范
            WebCacheTypeIsObjAppoint.checkKeyIsEmpty(key);
            // 取出缓存中的数据信息
            ConcurrentHashMap<String, CacheDefinition> dataSMap = objInstance.getDatasMap();
            CacheDefinition returnObj = dataSMap.get(key);

            // 获取到了数据以后如果returnObj不为null 我们需要将这个缓存数据的最后操作的时间设置成现在的时间
            // 这样子就达到了数据持久化的结果
            if (returnObj != null) {
                // 查询该数据是否需要交给监视器
                if (returnObj.isHandTimer.get()) {
                    CountCurrentCacheIfPastTypeObj.getInstance().getTimers().put(key, new Date());
                }
            }
            return returnObj == null ? null : returnObj.object;
        } catch (Exception e) {
            throw e;
        } finally {
            lock.readLock().unlock();
        }

    }
}
