package com.king2.webkcache.cache.pojo;

import java.util.concurrent.ConcurrentHashMap;

/*=======================================================
	说明:  这是一个通用的缓存数据中心

	作者		时间					            注释
  	俞烨		19-10-14                         创建
=======================================================*/
public class WebKCacheTypeIsObjDataCenter {

    private WebKCacheTypeIsObjDataCenter() {
    }

    private static WebKCacheTypeIsObjDataCenter webKCacheTypeIsMapDataCenter = new WebKCacheTypeIsObjDataCenter();

    public static WebKCacheTypeIsObjDataCenter getInstance() {
        return webKCacheTypeIsMapDataCenter;
    }

    /**
     * 这是一个缓存数据结构 使用者可以通过set(key , value)方法将值设置进缓存中
     * 也可以通过方法get(key)从缓存中取出数据
     */
    // TODO 类的类型为'WebKCacheTypeIsMapDataCenter'的添加方法名为'set()' 取出的方法名为get()
    private ConcurrentHashMap<String, Object> dataSMap = new ConcurrentHashMap<String, Object>();

    // 过期时间 毫秒 默认为2小时
    public static Integer timeout = 1000 * 60 * 60 * 2;

    public ConcurrentHashMap<String, Object> getDatasMap() {
        return dataSMap;
    }

    public void setDatasMap(ConcurrentHashMap<String, Object> datasMap) {
        this.dataSMap = datasMap;
    }
}
