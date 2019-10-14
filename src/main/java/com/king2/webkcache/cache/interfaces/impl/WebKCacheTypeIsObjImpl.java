package com.king2.webkcache.cache.interfaces.impl;

import com.king2.webkcache.cache.appoint.WebCacheTypeIsObjAppoint;
import com.king2.webkcache.cache.interfaces.WebKCacheHandleDataInterface;
import com.king2.webkcache.cache.pojo.WebKCacheTypeIsObjDataCenter;
import com.king2.webkcache.cache.timer.CountCurrentCacheIfPastTypeObj;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/*=======================================================
	说明:  可以WebKCache缓存接口的实现类 类型为Obj

	作者		时间					            注释
  	俞烨		19-10-14                         创建
=======================================================*/
public class WebKCacheTypeIsObjImpl implements WebKCacheHandleDataInterface {

    // 提供超时的构造
    public WebKCacheTypeIsObjImpl() {
        this(1000 * 60 * 60 * 2);
    }

    public WebKCacheTypeIsObjImpl(Integer timeout) {
        if (timeout != null) {
            WebKCacheTypeIsObjDataCenter.timeout = timeout;
        }
    }

    /**
     * 将数据放入缓存中
     *
     * @param key   缓存中的key
     * @param value 缓存中的值
     * @return
     */
    public Object set(String key, Object value) throws Exception {

        /*
        这是一个set数据的方法 具有同步的效果 所以使用者无需担心多线程的情况下数据安不安全的问题
        使用者只管一个使用 中间的操作都由WebKCache帮你实现
         */

        // 获取类型为Obj的缓存数据
        WebKCacheTypeIsObjDataCenter objInstance = WebKCacheTypeIsObjDataCenter.getInstance();
        // 开启锁 防止多线程的环境下数据不安全的问题
        synchronized (objInstance) {

            // 创建返回的数据
            Object returnObj = null;

            // 校验值是否正确 是否为空
            WebCacheTypeIsObjAppoint.checkKeyIsEmpty(key);

            // 校验完成后讲数据存入缓存中去
            ConcurrentHashMap<String, Object> cacheData = objInstance.getDatasMap();
            returnObj = cacheData.get(key);

            // 将数据存入缓存中
            cacheData.put(key, value);
            // TODO 存入成功记住讲缓存的本次缓存的时间存入一级缓存计时器中 防止该缓存为无用数据 需要将无用的数据进行清理
            CountCurrentCacheIfPastTypeObj.getInstance().getTimers().put(key, new Date());
            // 开启计时器
            CountCurrentCacheIfPastTypeObj.openTimers();
            // 唤醒
            objInstance.notifyAll();
            return returnObj;
        }

    }

    /**
     * 调用CacheRecycle缓存回收期
     */
    @Override
    public void cr() {
        // 获取类型为Obj的缓存数据
        WebKCacheTypeIsObjDataCenter objInstance = WebKCacheTypeIsObjDataCenter.getInstance();
        objInstance.notifyAll();
    }
}
