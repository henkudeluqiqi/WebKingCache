package org.king2.webkcache.cache.definition;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * =======================================================
 * 说明:  缓存的数据定义
 * <p>
 * 作者		时间					            注释
 *
 * @author 俞烨        19-10-15                         创建
 * =======================================================
 */
public class CacheDefinition {

    // ----------------------------------------------------------------------------------------------------------------
    // 这是一个缓存的定义类
    // 面会存在一些缓存的数据 比如这个缓存是否永久存在  这个缓存是否支持自动消失 是否交给监听器
    // 这个缓存类的类型属于什么 ....
    // ----------------------------------------------------------------------------------------------------------------
    public CacheDefinition(Object o, boolean f, int timeout) {
        this.object = o;
        this.isHandTimer.set(f);
        this.timeout = timeout;
    }

    public CacheDefinition() {
    }

    /**
     * 缓存原本的数据信息
     */
    public Object object;

    /**
     * 该对象是否交给监视器监听 true 代表交给 false不交
     * 交给监视器监听的话 根据用户配置的多久未使用就自动清理的时间 来动态的回收和存储缓存数据
     */
    public AtomicBoolean isHandTimer = new AtomicBoolean();

    /**
     * 改值的过期时间
     */
    public int timeout;
}
