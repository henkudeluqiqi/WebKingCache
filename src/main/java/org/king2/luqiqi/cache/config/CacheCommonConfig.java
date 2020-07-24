package org.king2.luqiqi.cache.config;

/**
 * 项目名称：CACHE-KING2-V1.0
 * 类 名 称：CacheCommonConfig
 * 类 描 述：缓存公共的配置中心
 * 创建时间：2020/7/22 3:39 下午
 * 创 建 人：俞烨-company-mac
 */
public class CacheCommonConfig {

    /***
     * 缓存类最大的一个占用内存(对象数量)
     */
    public static Long MAX_CACHE_MEMORY = 10000000L;

    /***
     * 是否开启持久化
     */
    public static boolean PERSISTENCE = false;
}
