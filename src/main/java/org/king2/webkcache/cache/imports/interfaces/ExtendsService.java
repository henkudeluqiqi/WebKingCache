package org.king2.webkcache.cache.imports.interfaces;

import org.king2.webkcache.cache.pojo.Instantiation;

import java.util.List;

/**
 * 扩展的WebKingCache的Service
 */
public interface ExtendsService {

    /**
     * 返回自己自定义的实例信息
     *
     * @return
     */
    public List<Instantiation> addExtends();
}
