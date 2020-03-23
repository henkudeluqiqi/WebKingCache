package org.king2.webkcache.cache.http;

import org.king2.webkcache.cache.http.aspect.HttpWebKingCache;

public class Demo {


    public static void main(String[] args) throws Exception {
        HttpWebKingCache cache = HttpWebKingCache.getInstance();
        cache.start();
        cache.set("key" , "value");
        System.out.println(cache.get("key"));
        System.out.println(cache.size());
    }
}
