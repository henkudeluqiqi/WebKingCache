package org.king2.webkcache.cache.http.retry;

import org.king2.webkcache.cache.http.aspect.HttpWebKingCache;
import org.king2.webkcache.cache.http.pojo.RequestIsTimePojo;

import java.util.Date;

/**
 * 数据重发的类
 */
public class RequestRetry {

    public static void monitor() {

        // 监听是否需要重新发送数据
        new Thread(() -> {
            while (true) {
                // 判断数据是否为空
                if (!HttpWebKingCache.REQUEST_MAP.isEmpty()) {
                    Date date = new Date();
                    HttpWebKingCache.REQUEST_MAP.forEach((k, v) -> {

                        // 判断消息是否过期
                        if ((v.getTime().getTime() + (1000 * 5)) <= date.getTime()) {
                            // 过期需要重新发送数据
                            RequestIsTimePojo remove = HttpWebKingCache.REQUEST_MAP.get(k);
                            if (remove != null) {
                                // 重发前需要判断连接是否正常
                                while (HttpWebKingCache.getInstance().isActive()) {
                                    HttpWebKingCache.getInstance().send(remove.getCacheRequest());
                                }
                            }

                        }
                    });
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
