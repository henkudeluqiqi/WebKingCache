package com.king2.webkcache.cache.interfaces;

/*=======================================================
	说明:  WebKCache操作数据的接口

	作者		时间					            注释
  	俞烨		19-10-14                         创建
=======================================================*/
public interface WebKCacheHandleDataInterface {

    /**
     * 将数据放入缓存中
     *
     * @param key   缓存中的key
     * @param value 缓存中的值
     * @return
     */
    Object set(String key, Object value) throws Exception;

    /**
     * 调用CacheRecycle缓存回收期
     */
    void cr();
}
