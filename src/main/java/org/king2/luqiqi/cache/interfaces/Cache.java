package org.king2.luqiqi.cache.interfaces;

import com.alibaba.fastjson.JSON;

import java.io.File;

/**
 * 项目名称：CACHE-KING2-V1.0
 * 类 名 称：Cache
 * 类 描 述：WebKingCache的统一接口
 * 创建时间：2020/7/22 10:43 上午
 * 创 建 人：俞烨-company-mac
 */
public interface Cache {

    /**
     * 将数据存入缓存中的接口
     *
     * @param key   key
     * @param value 值
     */
    void set(String key, Object value);


    <T> T get(String key, Class<T> clazz);

    default void remove(String key) {

    }

    default boolean exits(String key) {
        return false;
    }

    default boolean expired(String key) {
        return false;
    }

    default void setEx(String key, long timeout, Object value) {

    }
}
