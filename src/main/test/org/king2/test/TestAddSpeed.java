package org.king2.test;

import org.junit.Test;
import org.king2.webkcache.cache.http.aspect.HttpWebKingCache;

public class TestAddSpeed {

    static HttpWebKingCache cache = null;

    static {
        cache = HttpWebKingCache.getInstance();
        try {
            cache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 测试单线程添加数据的速度
     * 第一次10w: 1987ms
     * 第二次10w: 2655ms
     * 第三次10w: 2075ms
     * 第四次10w: 2003ms
     * 第五次10w: 1980ms
     * 第六次10w: 2061ms、2132ms、1872ms、2044ms、1981ms、2294ms
     * 平均為:2102.3ms
     * 网络良好时，丢失率 0%
     * */
    @Test
    public void testOneThreadAddSpeed() throws Exception {


        // 添加1000w条数据
        long l = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            cache.set("testOneThreadAddSpeed1" + i, "i", true);
        }

        long l1 = System.currentTimeMillis();
        System.out.println(l1 - l);

        System.in.read();
    }


    /***
     * 30w数据
     * 207ms
     */
    @Test
    public void size() {
        long l = System.currentTimeMillis();
        System.out.println(cache.size());
        long l1 = System.currentTimeMillis();
        System.out.println(l1 - l);

    }

    @Test
    public void get() throws Exception {
        long l = System.currentTimeMillis();
        System.out.println(cache.get("testOneThreadAddSpeed1" + 8883));
        long l1 = System.currentTimeMillis();
        System.out.println(l1 - l);
    }
}
