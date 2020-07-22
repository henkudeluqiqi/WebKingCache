package org.king2.luqiqi.cache.data;

import lombok.Data;
import org.king2.luqiqi.cache.realize.DefaultCache;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 项目名称：CACHE-KING2-V1.0
 * 类 名 称：DefaultCacheData
 * 类 描 述：默认缓存的数据中心
 * 创建时间：2020/7/22 3:19 下午
 * 创 建 人：俞烨-company-mac
 */
public class DefaultCacheData {

    private DefaultCacheData() {
        // 初始化锁的信息
        initLock();
    }

    private static DefaultCacheData DEFAULT_CACHE_DATA = null;

    public synchronized static DefaultCacheData getInstance() {
        if (DEFAULT_CACHE_DATA == null) {
            synchronized (DefaultCacheData.class) {
                if (DEFAULT_CACHE_DATA == null) {
                    DEFAULT_CACHE_DATA = new DefaultCacheData();
                }
                return DEFAULT_CACHE_DATA;
            }
        } else {
            return DEFAULT_CACHE_DATA;
        }
    }


    /****
     * 数据存放的容器
     */
    public final Map<Integer, Map<String, Object>> defaultCache =
            new HashMap<>();

    /***
     * 需要监听的过期Key
     */
    public final Set<String> expiredKeys = new HashSet<>();

    /***
     * 缓存数据对象的大小
     */
    private Long cacheSize = 0L;

    /***
     * 数据的分段锁
     */
    private final Map<Integer, ReentrantReadWriteLock> locks =
            new HashMap<>();

    /***
     * 分段锁初始化的长度
     */
    public final Integer subsectionLength = 64;

    /***
     * 是否初始化了锁的信息
     */
    private volatile boolean isInitLockFlag = false;

    /***
     * 初始化锁的信息
     */
    private synchronized void initLock() {
        if (isInitLockFlag) return;
        isInitLockFlag = true;
        // 初始化锁的信息
        for (Integer i = 0; i < subsectionLength; i++) {
            locks.put(i, new ReentrantReadWriteLock());
        }
    }

    /***
     * 通过key获取到锁
     * @param key key
     * @return
     */
    public ReentrantReadWriteLock getLock(String key) {
        Assert.notNull(key, "key不能为空");
        int lockIndex = key.hashCode() & (subsectionLength - 1);
        return locks.get(lockIndex);
    }

    public void setCacheSize() {
        this.cacheSize = cacheSize + 1;
    }

    public void minusCacheSize() {
        this.cacheSize = cacheSize == 0 ? 0 : cacheSize - 1;
    }

    public Long getCacheSize() {
        return cacheSize;
    }
}
