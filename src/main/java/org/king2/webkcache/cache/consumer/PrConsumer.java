package org.king2.webkcache.cache.consumer;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j;
import org.king2.webkcache.cache.pojo.PrData;
import org.king2.webkcache.cache.task.TaskThreadPool;
import org.xerial.snappy.Snappy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 数据压缩的消费者
 */
@Log4j
public class PrConsumer {


    public static final Integer WAIT_TIME = 1000 * 60 * 10;

    public static final String PR_PATH = "/cache-king-data-";

    /**
     * 打开消费者端
     */
    public static void openConsumer() {

        new Thread(() -> {
            while (true) {
                // 准备消费信息
                Map<Integer, ConcurrentLinkedQueue<PrData>> prData = TaskThreadPool.getInstance().getPrData();
                prData.forEach((k, v) -> {
                    TaskThreadPool.getInstance().getPOOL().execute(() -> {
                        Thread.currentThread().setName("消费：同步数据线程" + System.currentTimeMillis());
                        synchronized (v) {
                            // 需要写入数据到文件当中
                            if (!v.isEmpty()) {
                                PrData poll = v.poll();
                                log.info(poll.getKey() + "-" + poll.getCacheDefinition().object);
                                File file = new File(poll.getPath() + PR_PATH + k);
                                if (!file.exists()) {
                                    try {
                                        file.createNewFile();
                                    } catch (IOException e) {
                                        // 创建失败
                                        log.error("创建文件失败");
                                        e.printStackTrace();
                                    }
                                }

                                // 创建文件完成后 我们需要将数据压缩并持久化到本地磁盘上。
                                byte[] zip = zip(poll);
                                // 持久化到本地磁盘
                                prDisk(zip, file);
                            }

                            try {
                                v.wait(WAIT_TIME);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                });

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 持久化数据到本地磁盘
     *
     * @param zip
     */
    public static void prDisk(byte[] zip, File file) {
        if (zip == null || !file.exists()) {
            return;
        }
        // 持久化到本地磁盘
        FileWriter writer = null;
        try {
            writer = new FileWriter(file, true);
            writer.write(JSON.toJSONString(zip));
            writer.write("\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 压缩数据并返回byte数组
     *
     * @param prData 需要进行压缩的数据
     * @return
     */
    public static byte[] zip(PrData prData) {

        try {
            return Snappy.compress(JSON.toJSONString(prData));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解压
     *
     * @param bytes
     * @return
     */
    public static byte[] unzip(byte[] bytes) {
        try {
            return Snappy.uncompress(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
