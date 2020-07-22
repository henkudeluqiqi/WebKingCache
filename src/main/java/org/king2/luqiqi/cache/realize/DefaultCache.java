package org.king2.luqiqi.cache.realize;

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

    public void set(String key, Long timeout, Object value) {

        // 判断Key是否为空
        Assert.notNull(key, "Key不能为空");

        // 加锁
        ReentrantReadWriteLock lock = DefaultCacheData.getInstance().getLock(key);
        lock.writeLock().lock();
        try {

            // 判断内存是否够用
            if (DefaultCacheData.getInstance().getCacheSize() < CacheCommonConfig.MAX_CACHE_MEMORY) {

                add(key, timeout, value);
            } else {
                // 需要进行数据的重构，将过期的数据进行删除，如果没有过期的数据就进行拒绝策略
                if (DefaultCacheData.getInstance().expiredKeys.size() <= 0) {
                    throw new CacheSizeUpperLimitException();
                }

                // 清理数据
                clear();

                // 判断数据是否还是不能存入
                if (DefaultCacheData.getInstance().getCacheSize() >= CacheCommonConfig.MAX_CACHE_MEMORY) {
                    throw new CacheSizeUpperLimitException();
                }

                add(key, timeout, value);
            }
        } catch (CacheSizeUpperLimitException e) {
            e.printStackTrace();
        } finally {
            // 释放锁
            lock.writeLock().unlock();
        }
    }

    private void add(String key, Long timeout, Object value) {
        int index = key.hashCode() & (DefaultCacheData.getInstance().subsectionLength - 1);
        Map<String, Object> cacheMap = DefaultCacheData.getInstance().defaultCache.get(index);
        if (cacheMap == null) {
            cacheMap = new HashMap<>();
        }
        if (cacheMap.put(key, new CacheDefinition(key, value, timeout)) == null) {
            DefaultCacheData.getInstance().setCacheSize();
        }
        DefaultCacheData.getInstance().defaultCache.put(index, cacheMap);
    }

    @Override
    public void set(String key, Object value) {
        set(key, null, value);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        return null;
    }

    /***
     * 清理数据
     */
    public synchronized void clear() {
        if (!DefaultCacheData.getInstance().expiredKeys.isEmpty()) {
            Map<Integer, Map<String, Object>> defaultCache = DefaultCacheData.getInstance().defaultCache;
            // 定义已经删除掉的数据
            List<String> delKeys = new ArrayList<>();
            for (String expiredKey : DefaultCacheData.getInstance().expiredKeys) {
                int index = expiredKey.hashCode() & (DefaultCacheData.getInstance().subsectionLength - 1);
                CacheDefinition cacheDefinition = (CacheDefinition) defaultCache.get(index).get(expiredKey);
                // 判断是否过期了
                if (cacheDefinition.isOpenExpired()) {
                    if (cacheDefinition.getTimestamp() + cacheDefinition.getTimeout() <= System.currentTimeMillis()) {
                        // 开启锁
                        ReentrantReadWriteLock lock = DefaultCacheData.getInstance().getLock(expiredKey);
                        lock.writeLock().lock();
                        try {
                            // 说明过期了
                            defaultCache.get(index).remove(expiredKey);
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
