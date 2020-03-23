package org.king2.test;

import org.junit.Test;
import org.king2.webkcache.cache.interfaces.impl.DefaultWebKingCache;
import org.king2.webkcache.cache.pojo.ServerProperties;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestPr {

    /**
     * 测试持久化
     * @throws Exception
     */
    @Test
    public void pr() throws Exception {
        DefaultWebKingCache defaultWebKingCache = new DefaultWebKingCache(100000
                , new ServerProperties(true, "/Users/sam/luqiqi/codes/cacheData"));
        for (int i = 0; i < 11; i++) {
            defaultWebKingCache.set("U" + i, "de", true);
        }

        System.in.read();
    }


    @Test
    public void dd() {
        test("213123");
    }

    public void test(String tt) {
        ExecutorService executorService = new ThreadPoolExecutor(
                100, 500, 1,
                TimeUnit.HOURS, new LinkedBlockingDeque<>()
        );
        executorService.execute(() -> {
            System.out.println(tt.hashCode());
        });
    }
}
