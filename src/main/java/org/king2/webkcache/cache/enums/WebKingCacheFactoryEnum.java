package org.king2.webkcache.cache.enums;

/**
 * =======================================================
 * 说明:
 * 由于WebKingCache是一个接口，而且存在着多个实现，所以用户可以通过枚举类，获取到不同类型的数据
 * <p>
 * 作者		时间					            注释
 *
 * @author 俞烨        19-11-19                         创建
 * =======================================================
 */
public enum WebKingCacheFactoryEnum {

    CURRENT_TYPEIS_OBJ("org.king2.webkcache.cache.interfaces.ConcurrentWebCache"),      // 线程安全且数据类型为Object
    NO_CURRENT_TYPEIS_HASH("");  // 线程不安全且数据类型为HashMap

    private String clazz;

    private WebKingCacheFactoryEnum(String clazz) {
        this.clazz = clazz;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }
}

