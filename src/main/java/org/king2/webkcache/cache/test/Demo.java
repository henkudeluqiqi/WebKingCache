package org.king2.webkcache.cache.test;

import org.king2.webkcache.cache.interfaces.impl.DefaultWebKingCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/*=======================================================
	说明:

	作者		时间					            注释
  	俞烨		                         创建
=======================================================*/
public class Demo {

    /*public static void main(String[] args) throws InterruptedException {
        ConcurrentHashMap c = new ConcurrentHashMap();
        AtomicInteger integer = new AtomicInteger();
        List<Thread> threads = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            threads.add(new Thread(() -> {
                for (int i1 = 0; i1 < 1000000; i1++) {
                    try {
                        c.put(integer.addAndGet(1) + "鹿七", i1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }));
        }

        for (Thread thread : threads) {
            thread.start();
        }
        long l = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.join();
        }
        long l1 = System.currentTimeMillis();
        System.out.println(l1 - l);

    }*/

    public static void main(String[] args) throws Exception {
        AtomicInteger integer = new AtomicInteger();
        DefaultWebKingCache cache = new DefaultWebKingCache(10000);
        List<Thread> threads = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            threads.add(new Thread(() -> {
                for (int i1 = 0; i1 < 1000000; i1++) {
                    try {
                        cache.set(integer.addAndGet(1) + "鹿七", i1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }));
        }

        for (Thread thread : threads) {
            thread.start();
        }
        long l = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.join();
        }
        long l1 = System.currentTimeMillis();
        System.out.println(l1 - l);
        System.out.println(cache.size());

        l = System.currentTimeMillis();
        System.out.println("get结果" + cache.get("10000鹿七"));
        l1 = System.currentTimeMillis();
        System.out.println(l1 - l);

        l = System.currentTimeMillis();
        System.out.println("remove结果" + cache.remove("10000鹿七"));
        l1 = System.currentTimeMillis();
        System.out.println(l1 - l);
        System.out.println(cache.size());
    }

   /* public static void main(String[] args) throws Exception {

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
    }*/
}
