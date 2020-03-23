package org.king2.webkcache.cache.consumer;

import org.king2.webkcache.cache.interfaces.impl.DefaultWebKingCache;
import org.king2.webkcache.cache.lock.SubSectionLock;
import org.king2.webkcache.cache.pojo.PrData;
import org.king2.webkcache.cache.pojo.RemoveKeyData;
import org.king2.webkcache.cache.task.TaskThreadPool;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 需要删除的key消费者
 */
public class RemoveKeyConsumer {

    public static void openRemoveKeyConsumer() {
        new Thread(() -> {

            Thread.currentThread().setName("消费删除key的线程");
            while (true) {
                // 定义是否已经把FileRead释放
                boolean isFileRead = false;
                ConcurrentLinkedQueue<RemoveKeyData> removeKeys = TaskThreadPool.getInstance().getRemoveKeys();
                synchronized (removeKeys) {
                    if (!removeKeys.isEmpty()) {
                        // 开始消费
                        RemoveKeyData poll = removeKeys.poll();
                        Map<Integer, ConcurrentLinkedQueue<PrData>> prData = TaskThreadPool.getInstance().getPrData();
                        Integer index = SubSectionLock.SUB_SECTION_LOCK_SIZE;
                        ConcurrentLinkedQueue<PrData> prData1 = prData.get(poll.hashCode() & index);
                        synchronized (prData1) {
                            // 准备删除信息
                            // 获取到文本信息
                            File file = new File(poll.getPath() + "/" + PrConsumer.PR_PATH + index);
                            if (file.exists()) {
                                // 重新写入
                                FileWriter outputStream = null;
                                try {
                                    // 获取文件的新信息
                                    List<String> newWriteData = getNewData(index, file, poll);

                                    // 删除这个文件
                                    String name = file.getName();
                                    file.delete();
                                    // 重新创建
                                    file = new File(name);
                                    file.createNewFile();

                                    outputStream = new FileWriter(file, true);
                                    for (String newWriteDatum : newWriteData) {
                                        outputStream.write(newWriteDatum);
                                        outputStream.write("\r\n");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    // 释放
                                    if (outputStream != null) {
                                        try {
                                            outputStream.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        try {
                            removeKeys.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 获取这个文件的新数据信息
     *
     * @param index 索引
     * @param file  文件信息
     * @param poll  删除的信息
     * @return
     * @throws IOException
     */
    public static List<String> getNewData(int index, File file, RemoveKeyData poll) throws IOException {
        // 获取到当前数据存在文件中的行数
        Map<String, Integer> stringIntegerMap = DefaultWebKingCache.getRECORD_VALUE_LINE().get(index);
        if (!CollectionUtils.isEmpty(stringIntegerMap)) {
            // 当前数据在文件中的行数
            Integer line = stringIntegerMap.get(poll.getKey());
            // 开始读取文件
            FileReader fileReader = null;
            BufferedReader bufferedReader = null;
            try {
                fileReader = new FileReader(file);
                bufferedReader = new BufferedReader(fileReader);

                // 读取数据并删除不需要的数据
                List<String> newWriteData = new ArrayList<>();
                String lineData = null;
                int lineSize = 0;
                while ((lineData = bufferedReader.readLine()) != null) {
                    if (line == lineSize++) {
                        continue;
                    }
                    newWriteData.add(lineData);
                }

                return newWriteData;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileReader != null)  // 释放掉之前的读信息
                    fileReader.close();
                if (bufferedReader != null)
                    // 释放掉之前的读信息
                    bufferedReader.close();
            }

        }
        return null;
    }
}