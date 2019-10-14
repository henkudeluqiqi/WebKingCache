package com.king2.webkcache.cache.appoint;

/*=======================================================
	说明:  类型为Obj的缓存委派类

	作者		时间					            注释
  	俞烨		19-10-14                         创建
=======================================================*/
public class WebCacheTypeIsObjAppoint {

    /**
     * 校验Key是否为空
     *
     * @param key
     */
    public static void checkKeyIsEmpty(String key) throws Exception {
        if (null == key || "".equals(key)) {
            throw new Exception("The cached key is empty 存入缓存空的key不可以为空");
        }
    }
}
