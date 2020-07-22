package org.test;

import org.junit.Test;
import org.king2.luqiqi.cache.interfaces.Cache;
import org.king2.luqiqi.cache.realize.DefaultCache;

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
        for (int i = 0; i < 11; i++) {
            cache.set("余" + i, "紫" + i);
        }

    }
}
