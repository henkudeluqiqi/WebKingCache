package org.king2.webkcache.cache.pojo;

import org.king2.webkcache.cache.definition.CacheDefinition;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 锁的POJO
 */
public class ReadWritePojo {

    public ReentrantReadWriteLock reentrantReadWriteLock;
    public Condition writeCondition;
    public ConcurrentHashMap<String, CacheDefinition> data;

    public ReadWritePojo() {
        this.reentrantReadWriteLock = new ReentrantReadWriteLock();
        this.writeCondition = this.reentrantReadWriteLock.writeLock().newCondition();
        this.data = new ConcurrentHashMap<>();
    }


}
