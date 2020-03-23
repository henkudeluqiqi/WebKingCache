package org.king2.webkcache.cache.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * =======================================================
 * 说明:  WebKCache类型为Object的锁
 * <p>
 * 作者		时间					            注释
 *
 * @author 俞烨        19-10-23                         创建
 * =======================================================
 */
public class WebKingCacheTypeIsObjectLock {
    private static WebKingCacheTypeIsObjectLock ourInstance = new WebKingCacheTypeIsObjectLock();

    public static WebKingCacheTypeIsObjectLock getInstance() {
        return ourInstance;
    }

    private WebKingCacheTypeIsObjectLock() {
    }


    private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public static ReentrantReadWriteLock getLock() {
        return lock;
    }

    public static Condition writeCondition() {
        return getLock().writeLock().newCondition();
    }

    public static Condition readCondition() {
        return getLock().readLock().newCondition();
    }
}
