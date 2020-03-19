package org.king2.webkcache.cache.timer;

import org.king2.webkcache.cache.interfaces.impl.DefaultWebKingCache;
import org.king2.webkcache.cache.lock.SubSectionLock;
import org.king2.webkcache.cache.pojo.ReadWritePojo;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 默认的缓存数据过期处理类
 */
public class DefaultWebKingCacheTimer {

    /**
     * 该缓存类是否被启动
     */
    public static AtomicBoolean IF_ACTIVE = new AtomicBoolean();

    /**
     * 检测时间（分钟）
     */
    public static final Integer PAST_TIME = 10;

    /**
     * 默认的线程
     */
    public static Thread DEFAULT_THREAD = null;

    /**
     * 当前缓存中所有数据的过期时间
     */
    public static ConcurrentHashMap<String, Date> TIME = new ConcurrentHashMap<>();

    /**
     * 开启定时器后会根据系统配置的默认几分钟进行检索数据是否失效（10分钟）
     * 如果进行失效则将系统的缓存数据进行删除
     */
    public static void open() {

        // 判断容器是否已经启动
        if (IF_ACTIVE.get()) {
            return;
        }
        IF_ACTIVE.set(true);

        /**
         * 创建一条新的线程避免主线程遭遇干扰
         */
        DEFAULT_THREAD = new Thread(() -> {
            while (true) {
                // 开始读取数据判断数据信息是否已经失效
                Date currentData = new Date();
                if (!CollectionUtils.isEmpty(TIME)) {
                    TIME.forEach((k, v) -> {
                        // 获取锁
                        ReadWritePojo currentKeyLock = SubSectionLock.getLock(k);
                        currentKeyLock.reentrantReadWriteLock.writeLock().lock();
                        try {
                            if (isPast(k, currentKeyLock.data.get(k).timeout, currentData)) {
                                // 删除对应的数据
                                currentKeyLock.data.remove(k);
                                TIME.remove(k);
                                DefaultWebKingCache.SIZE.addAndGet(-1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            currentKeyLock.reentrantReadWriteLock.writeLock().unlock();
                        }
                    });
                }

                // 进入休眠状态
                try {
                    Thread.sleep(PAST_TIME * 1000);
                } catch (InterruptedException e) {
                }
            }
        });
        DEFAULT_THREAD.start();
    }

    /**
     * 判断Key是否已经过期
     *
     * @param key
     * @param timeout
     * @param currentDate
     * @return true 已经过期 false没有过期
     */
    public static boolean isPast(String key, int timeout, Date currentDate) {
        Date date = TIME.get(key);
        if (date == null) {
            return false;
        }
        return (date.getTime() + timeout) < currentDate.getTime();
    }
}
