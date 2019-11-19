package org.king2.webkcache.cache.interfaces.impl;

import org.king2.webkcache.cache.definition.CacheDefinition;
import org.king2.webkcache.cache.interfaces.HashWebKingCache;
import org.king2.webkcache.cache.pojo.WebKingCacheTypeIsObjDataCenter;

import java.util.concurrent.ConcurrentHashMap;


public class HashWebCache implements HashWebKingCache {

    // 提供超时的构造
    public HashWebCache() {
        this(1000 * 60 * 60 * 2);
    }

    public HashWebCache(Integer timeout) {
        if (timeout != null) {
            WebKingCacheTypeIsObjDataCenter.timeout = timeout;
        }

    }

    @Override
    public void set(String allKey, String oneKey, String value) {

    }

    @Override
    public Object set(String key, Object value, boolean saveFlag) throws Exception {
        this.set(key, key, value + "");
        return null;
    }

    @Override
    public Object set(String key, Object value) throws Exception {
        return null;
    }

    @Override
    public void cr() {

    }

    @Override
    public Object get(String key) throws Exception {
        return null;
    }
}
