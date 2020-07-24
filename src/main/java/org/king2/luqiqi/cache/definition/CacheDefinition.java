package org.king2.luqiqi.cache.definition;

import lombok.Getter;
import org.king2.luqiqi.cache.data.DefaultCacheData;

/**
 * 项目名称：CACHE-KING2-V1.0
 * 类 名 称：CacheDefinition
 * 类 描 述：缓存的类定义
 * 创建时间：2020/7/22 10:57 上午
 * 创 建 人：俞烨-company-mac
 */
@Getter
public class CacheDefinition {

    public CacheDefinition(String key, Object cacheValue, Long timeout) {

        // 判断是否打开了过期
        if (timeout != null && timeout > 0) {
            this.isOpenExpired = true;
            // 添加一个需要监听的过期Key
            synchronized (DefaultCacheData.getInstance().expiredKeys) {
                DefaultCacheData.getInstance().expiredKeys.add(key);
            }
        }
        this.cacheValue = cacheValue;
        this.timeout = timeout;
    }

    /***
     * 缓存的值
     */
    private Object cacheValue;
    /**
     * 缓存的过期时间
     */
    private Long timeout;
    /**
     * 该缓存是否需要监听过期
     */
    private boolean isOpenExpired = false;

    /***
     * 当前存入的时间戳，用来计算过期key的一个时间
     */
    private Long timestamp = System.currentTimeMillis();
}
