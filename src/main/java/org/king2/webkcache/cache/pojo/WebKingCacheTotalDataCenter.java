package org.king2.webkcache.cache.pojo;

import org.king2.webkcache.cache.definition.CacheDefinition;

import java.util.concurrent.ConcurrentHashMap;

/**
 * =======================================================
 * 说明:  WebKCache总的数据中心
 * <p>
 * 作者		时间					            注释
 *
 * @author 俞烨        19-10-14                         创建
 * =======================================================
 */
public class WebKingCacheTotalDataCenter {

    // 为了提供一个长时间存储的Web缓存 我们需要将使用单例模式
    // 单例模式是什么具体请百度


    /**
     * 获取缓存类型是Object的缓存数据中心
     * 这个方法主要是提供给使用者 可以自己操作这个缓存数据 可以拿出去 但是不提供放进来的功能
     * 调用getTypeIsObjCacheData()方法 返回ConcurrentHashMap<String, Object>
     * ConcurrentHashMap<String, Object> 是类型为Object的总数据集合
     * String是Key Object是存进来的值 用户进行转换的时候一定要注意 如果存进来的类型和转换的类型不一致是会报类型转换异常的
     * 这是一个万能的数据结构中心  因为值为Object 在Java中所有的对象都继承了Object 所以 所有对象都可以存入这个缓存结构中
     *
     * @return ConcurrentHashMap<String, Object>
     */
    public static ConcurrentHashMap<String, CacheDefinition> getTypeIsObjCacheData() {
        return WebKingCacheTypeIsObjDataCenter.getInstance().getDatasMap();
    }
}
