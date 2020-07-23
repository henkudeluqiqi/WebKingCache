package org.king2.luqiqi.cache.realize;

import com.alibaba.fastjson.JSON;
import org.king2.luqiqi.cache.config.CacheCommonConfig;
import org.king2.luqiqi.cache.data.DefaultCacheData;
import org.king2.luqiqi.cache.definition.CacheDefinition;
import org.king2.luqiqi.cache.exceptions.CacheSizeUpperLimitException;
import org.king2.luqiqi.cache.interfaces.Cache;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 项目名称：CACHE-KING2-V1.0
 * 类 名 称：DefaultCache
 * 类 描 述：默认的缓存实现类
 * 创建时间：2020/7/22 11:06 上午
 * 创 建 人：俞烨-company-mac
 */
public class DefaultCache implements Cache {


    /***
     * 默认的set实现是一个线程安全的方法，通过key.hashcode() & (subsectionLock.length - 1)获取到对应的锁，
     * 使用者不需要担心在缓存中的数据安全问题。
     * 然后存入对应的缓存数据结构中
     * 缓存数据结构采用的是  Map<String ,Object>
     *     Map<String ,Object> 才是对应的 key - value(CacheDefinition) 所属数据结构
     * @param key   key
     * @param value 值
     */
    @Override
    public void set(String key, Object value) {
        set(key, null, value);
    }

    /***
     * set(String key, Object value)方法的重载
     * @param key       key
     * @param timeout   超时时间
     * @param value     值
     */
    public void set(String key, Long timeout, Object value) {

        // 判断Key是否为空
        Assert.notNull(key, "Key不能为空");

        // 加锁
        DefaultCacheData instance = DefaultCacheData.getInstance();
        ReentrantReadWriteLock lock = instance.getLock(key);
        lock.writeLock().lock();
        try {

            // 判断内存是否够用
            if (instance.getCacheSize() >= CacheCommonConfig.MAX_CACHE_MEMORY) {
                // 需要进行数据的重构，将过期的数据进行删除，如果没有过期的数据就进行拒绝策略
                if (instance.expiredKeys.size() <= 0) {
                    throw new CacheSizeUpperLimitException();
                }

                // 清理数据
                clear();

                // 判断数据是否还是不能存入
                if (instance.getCacheSize() >= CacheCommonConfig.MAX_CACHE_MEMORY) {
                    throw new CacheSizeUpperLimitException();
                }
            }
            add(key, timeout, value);
        } catch (CacheSizeUpperLimitException e) {
            e.printStackTrace();
        } finally {
            // 释放锁
            lock.writeLock().unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DefaultCache defaultCache = new DefaultCache();
        defaultCache.set("123", new Object());
        Thread.sleep(2000);
        Object o = defaultCache.get("123", Object.class);
        o = defaultCache.get("123", Object.class);
    }

    /***
     * 添加到缓存中
     * @param key
     * @param timeout
     * @param value
     */
    private void add(String key, Long timeout, Object value) {
        DefaultCacheData instance = DefaultCacheData.getInstance();
        if (instance.defaultCache.put(key, new CacheDefinition(key, value, timeout)) == null) {
            instance.setCacheSize();
        }
       /* DefaultCacheData instance = DefaultCacheData.getInstance();
        int index = key.hashCode() & (instance.subsectionLength - 1);
        Map<String, Object> cacheMap = instance.defaultCache.get(index);
        if (cacheMap == null) {
            cacheMap = new HashMap<>();
        }
        if (cacheMap.put(key, new CacheDefinition(key, value, timeout)) == null) {
            instance.setCacheSize();
        }
        instance.defaultCache.put(index, cacheMap);*/
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {

        // 判断Key是否为空
        Assert.notNull(key, "Key不能为空");

        // 加锁
        DefaultCacheData instance = DefaultCacheData.getInstance();
        ReentrantReadWriteLock lock = instance.getLock(key);
        lock.readLock().lock();
        boolean isUnReadLock = false;
        try {
            CacheDefinition cacheDefinition = (CacheDefinition) instance.defaultCache.get(key);
            if (cacheDefinition == null) {
                return null;
            }

            // 判断是否开启了监听过期机制或者需要判断这个缓存是否已经过期了，如果过期了需要清理
            if (!cacheDefinition.isOpenExpired() ||
                    cacheDefinition.getTimestamp() + cacheDefinition.getTimeout() > System.currentTimeMillis()) {
                return (T) cacheDefinition.getCacheValue();
            }

            // 说明过期了 需要进行清理
            lock.readLock().unlock();
            isUnReadLock = true;
            lock.writeLock().lock();
            instance.defaultCache.remove(key);
            DefaultCacheData.getInstance().minusCacheSize();
            // 删除监听的set集合
            synchronized (DefaultCacheData.getInstance().expiredKeys) {
                DefaultCacheData.getInstance().expiredKeys.remove(key);
            }
            return null;
        } finally {
            if (!isUnReadLock) lock.readLock().unlock();
            if (isUnReadLock) lock.writeLock().unlock();
        }

    }


    /***
     * 清理数据
     */
    public synchronized void clear() {
        if (!DefaultCacheData.getInstance().expiredKeys.isEmpty()) {
            Map<String, Object> defaultCache = DefaultCacheData.getInstance().defaultCache;
            // 定义已经删除掉的数据
            List<String> delKeys = new ArrayList<>();
            for (String expiredKey : DefaultCacheData.getInstance().expiredKeys) {
                int index = expiredKey.hashCode() & (DefaultCacheData.getInstance().subsectionLength - 1);
                CacheDefinition cacheDefinition = (CacheDefinition) defaultCache.get(expiredKey);
                // 判断是否过期了
                if (cacheDefinition.isOpenExpired()) {
                    if (cacheDefinition.getTimestamp() + cacheDefinition.getTimeout() <= System.currentTimeMillis()) {
                        // 开启锁
                        ReentrantReadWriteLock lock = DefaultCacheData.getInstance().getLock(expiredKey);
                        lock.writeLock().lock();
                        try {
                            // 说明过期了
                            defaultCache.remove(expiredKey);
                            DefaultCacheData.getInstance().minusCacheSize();
                            delKeys.add(expiredKey);
                        } finally {
                            lock.writeLock().unlock();
                        }
                    }
                }
            }
            // 清空完毕后 重新整理set的数据
            if (!CollectionUtils.isEmpty(delKeys)) {
                synchronized (DefaultCacheData.getInstance().expiredKeys) {
                    for (String delKey : delKeys) {
                        DefaultCacheData.getInstance().expiredKeys.remove(delKey);
                    }
                }

            }
        }

    }
}
