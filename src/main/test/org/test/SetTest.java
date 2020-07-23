package org.test;

import org.junit.Test;
import org.king2.luqiqi.cache.interfaces.Cache;
import org.king2.luqiqi.cache.realize.DefaultCache;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 项目名称：CACHE-KING2-V1.0
 * 类 名 称：SetTest
 * 类 描 述：TODO
 * 创建时间：2020/7/22 3:56 下午
 * 创 建 人：俞烨-company-mac
 */
public class SetTest {


    @Test
    public void set() {
        Cache cache = new DefaultCache();
        ConcurrentHashMap<Object, Object> objectObjectHashMap = new ConcurrentHashMap<>();
        long l = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            // objectObjectHashMap.put("余" + i, "紫" + i);
            cache.set("余" + i, "紫" + i);
        }
        long s = System.currentTimeMillis();
        System.out.println("set100w:" + (s - l));
        l = System.currentTimeMillis();
        System.out.println(cache.get("余7777777", String.class));
        s = System.currentTimeMillis();
        System.out.println("get100w:" + (s - l));
    }
}
