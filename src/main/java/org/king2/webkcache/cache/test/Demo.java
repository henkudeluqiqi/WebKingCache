package org.king2.webkcache.cache.test;

import org.king2.webkcache.cache.interfaces.impl.WebKingCacheTypeIsObjImpl;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/*=======================================================
	说明:

	作者		时间					            注释
  	俞烨		                         创建
=======================================================*/
public class Demo {

    public static void main(String[] args) throws Exception {

        WebKingCacheTypeIsObjImpl webKCacheTypeIsObj = new WebKingCacheTypeIsObjImpl(10000);

        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    for (int i1 = 0; i1 < 100; i1++) {
                        webKCacheTypeIsObj.set(random.nextInt(10000000) + "", random.nextInt(10000000));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        ConcurrentHashMap<String, String> test = new ConcurrentHashMap<>();
        while (true) {
            if (test.size() > 10) {
                continue;
            }
            String s = random.nextInt(10000000) + "";
            Object o = webKCacheTypeIsObj.get(s);

            if (o != null) {
                System.out.println("key" + s);
                System.out.println(o);
                test.put(s, s);
            }

        }

    }
}
