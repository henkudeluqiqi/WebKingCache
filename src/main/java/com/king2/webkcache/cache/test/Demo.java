package com.king2.webkcache.cache.test;

import com.king2.webkcache.cache.interfaces.impl.WebKCacheTypeIsObjImpl;

/*=======================================================
	说明:

	作者		时间					            注释
  	俞烨		                         创建
=======================================================*/
public class Demo {

    public static void main(String[] args) throws Exception {

        WebKCacheTypeIsObjImpl webKCacheTypeIsObj = new WebKCacheTypeIsObjImpl(2000);
        webKCacheTypeIsObj.set("测试", "测试");
        webKCacheTypeIsObj.set("测试1", "测试");
        webKCacheTypeIsObj.set("测试2", "测试");
    }
}
