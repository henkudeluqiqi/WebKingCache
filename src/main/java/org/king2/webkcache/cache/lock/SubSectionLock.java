package org.king2.webkcache.cache.lock;

import org.king2.webkcache.cache.pojo.ReadWritePojo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 分段锁
 */
public class SubSectionLock {

    /**
     * 分段锁的数据信息，我们根据key的hash值来取得当前key使用哪一把锁，这样就提高了程序的性能
     * keyHash => ReadWritePojo
     */
    public static final ConcurrentHashMap<Integer, ReadWritePojo> SUB_SECTION_LOCK =
            new ConcurrentHashMap<Integer, ReadWritePojo>();

    /**
     * 分段锁的最高长度
     */
    public static final Integer SUB_SECTION_LOCK_SIZE = 11;

    /**
     * 当前容器是否已经初始化
     */
    private static final AtomicBoolean IF_INIT = new AtomicBoolean(false);

    /**
     * 通过Key获取当前Key所需要的锁信息
     *
     * @param key
     * @return 锁信息
     */
    public static ReadWritePojo getLock(Object key) {

        if (!IF_INIT.get()) {
            // 需要进行初始化
            for (Integer i = 0; i <= SUB_SECTION_LOCK_SIZE; i++) {
                SUB_SECTION_LOCK.put(i, new ReadWritePojo());
            }
            IF_INIT.set(true);
        }

        return SUB_SECTION_LOCK.get(key.hashCode() & SUB_SECTION_LOCK_SIZE);
    }

}
