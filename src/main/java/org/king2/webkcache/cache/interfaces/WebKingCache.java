package org.king2.webkcache.cache.interfaces;

/**
 * =======================================================
 * 说明:  WebKCache操作数据的接口
 * <p>
 * 作者		时间					            注释
 *
 * @author 俞烨        19-10-14                         创建
 * =======================================================
 */
public interface WebKingCache {

    /**
     * 将数据放入缓存中  这是一个万能的Set方法
     * 但是使用者要注意一定 那就是存进去的类型和取出来的类型要一致 否则使用者在强转的中会抛出异常
     * 这个方法不提供自己失效的时间  但是系统提供了一个缓存监视器'CountCurrentCacheIfPastTypeObj'
     * CountCurrentCacheIfPastTypeObj监视器会根据用户配置的毫秒数来监听这个数据是否需要删除  是否需要回收,以此达到释放内存的效果。
     * 如果这个缓存数据要一直存储在内存中可以将参数@Param saveFlag 设置成true这样就会永远保存在内存中,
     * 也可以调用set(String key, Object value) 方法 这个方法也是将缓存数据永久存在内存中。
     *
     * @param key   缓存中的key
     * @param value 缓存中的值
     * @return
     * @Param saveFlag 数据是否永久存在
     */
    Object set(String key, Object value, boolean saveFlag, Integer timeout) throws Exception;

    /**
     * 参照 set(String key, Object value, boolean saveFlag , int timeout) throws Exception;的注释
     *
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    Object set(String key, Object value, boolean saveFlag) throws Exception;


    /**
     * 参照 set(String key, Object value, boolean saveFlag , int timeout) throws Exception;的注释
     *
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    Object set(String key, Object value) throws Exception;

    /**
     * 调用CacheRecycle缓存回收期
     */
    void cr();

    /**
     * 通过key获取值
     *
     * @param key
     * @return
     */
    Object get(String key) throws Exception;

    /**
     * 根据key删除值
     *
     * @param key
     * @return
     */
    Object remove(String key);

    /**
     * 获取长度
     *
     * @return
     */
    int size();
}
