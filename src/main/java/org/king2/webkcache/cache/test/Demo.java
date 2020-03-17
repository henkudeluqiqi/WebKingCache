package org.king2.webkcache.cache.test;

import org.king2.webkcache.cache.interfaces.impl.ConcurrentWebCache;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/*=======================================================
	说明:

	作者		时间					            注释
  	俞烨		                         创建
=======================================================*/
public class Demo {

    public static void main(String[] args) throws Exception {

        ConcurrentWebCache webKCacheTypeIsObj = new ConcurrentWebCache(10000);


        webKCacheTypeIsObj.set("鹿七七", 23, false, 5000);
        webKCacheTypeIsObj.set("鹿七七2", 23);
        webKCacheTypeIsObj.set("鹿七七3", 23, false, 400);
        webKCacheTypeIsObj.set("鹿七七4", 23, false, 600);
        Thread.sleep(500);
        System.out.println(webKCacheTypeIsObj.get("鹿七七"));
        System.out.println(webKCacheTypeIsObj.get("鹿七七2"));
        System.out.println(webKCacheTypeIsObj.get("鹿七七3"));
        System.out.println(webKCacheTypeIsObj.get("鹿七七4"));
        System.out.println(webKCacheTypeIsObj.size());
        webKCacheTypeIsObj.cr();
        Thread.sleep(1000);
        System.out.println(webKCacheTypeIsObj.get("鹿七七4"));
        System.out.println(webKCacheTypeIsObj.size());
        System.out.println(webKCacheTypeIsObj.remove("鹿七七4"));
        System.out.println(webKCacheTypeIsObj.size());
        System.out.println(webKCacheTypeIsObj.remove("鹿七七2"));
        System.out.println(webKCacheTypeIsObj.size());
        Thread.sleep(10000);
        System.out.println("---------------------------------------------");
        System.out.println(webKCacheTypeIsObj.size());
        System.out.println(webKCacheTypeIsObj.get("鹿七七"));
    }
}
