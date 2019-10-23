package com.king2.webkcache.cache.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*=======================================================
	说明:  WebKCache类型为Object的锁

	作者		时间					            注释
  	俞烨		19-10-23                         创建
=======================================================*/
public class WebKCacheTypeIsObjectLock {
    private static WebKCacheTypeIsObjectLock ourInstance = new WebKCacheTypeIsObjectLock();

    public static WebKCacheTypeIsObjectLock getInstance() {
        return ourInstance;
    }

    private WebKCacheTypeIsObjectLock() {
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
