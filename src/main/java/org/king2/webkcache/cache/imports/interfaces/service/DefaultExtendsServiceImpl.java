package org.king2.webkcache.cache.imports.interfaces.service;

import org.king2.webkcache.cache.imports.interfaces.ExtendsService;
import org.king2.webkcache.cache.pojo.Instantiation;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认的实现
 */
public class DefaultExtendsServiceImpl implements ExtendsService {

    @Override
    public List<Instantiation> addExtends() {

        List<Instantiation> list = new ArrayList<>();
        list.add(new Instantiation("concurrentCache", "org.king2.webkcache.cache.interfaces.impl.ConcurrentWebCache"));
        list.add(new Instantiation("defaultWebKingCache", "org.king2.webkcache.cache.interfaces.impl.DefaultWebKingCache"));
        return list;
    }
}
