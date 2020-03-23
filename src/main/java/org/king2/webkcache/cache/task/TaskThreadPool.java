package org.king2.webkcache.cache.task;

import lombok.Data;
import org.king2.webkcache.cache.lock.SubSectionLock;
import org.king2.webkcache.cache.pojo.PrData;
import org.king2.webkcache.cache.pojo.RemoveKeyData;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 任务线程池
 */
@Data
public class TaskThreadPool {

    private TaskThreadPool() {
    }

    private static final TaskThreadPool TASK_THREAD_POOL = new TaskThreadPool();

    public static TaskThreadPool getInstance() {
        return TASK_THREAD_POOL;
    }


    // 创建线程池
    private final ExecutorService POOL = new ThreadPoolExecutor(
            100, 500, 1,
            TimeUnit.HOURS, new LinkedBlockingQueue<>()
    );
    // 创建存放数据的队列信息
    private Map<Integer, ConcurrentLinkedQueue<PrData>> prData;

    // 创建存放删除数据的队列信息
    private ConcurrentLinkedQueue<RemoveKeyData> removeKeys;

    private AtomicBoolean isInit = new AtomicBoolean(false);


    public void init() {
        if (!isInit.get()) {
            isInit.set(true);
            prData = new ConcurrentHashMap<>();
            removeKeys = new ConcurrentLinkedQueue<>();
            for (Integer i = 0; i <= SubSectionLock.SUB_SECTION_LOCK_SIZE; i++) {
                prData.put(i, new ConcurrentLinkedQueue<>());
            }
        }
    }

    public ConcurrentLinkedQueue get(String key) {
        return prData.get(key.hashCode() & SubSectionLock.SUB_SECTION_LOCK_SIZE);
    }

}
