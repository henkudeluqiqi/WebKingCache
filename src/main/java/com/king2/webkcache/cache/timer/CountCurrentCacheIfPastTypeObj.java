package com.king2.webkcache.cache.timer;

import com.king2.webkcache.cache.pojo.WebKCacheTypeIsObjDataCenter;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/*=======================================================
	说明:  计算当前缓存是否过期

	作者		时间					            注释
  	俞烨		19-10-14                         创建
=======================================================*/
public class CountCurrentCacheIfPastTypeObj {

    private CountCurrentCacheIfPastTypeObj() {
    }

    private static CountCurrentCacheIfPastTypeObj countCurrentCacheIfPastTypeObj = new CountCurrentCacheIfPastTypeObj();

    public static CountCurrentCacheIfPastTypeObj getInstance() {
        return countCurrentCacheIfPastTypeObj;
    }

    /**
     * 类型为Obj的缓存计时器是否启动
     */
    public static AtomicBoolean ifActive = new AtomicBoolean();

    /**
     * 计时器
     */
    private static ConcurrentHashMap<String, Date> timers = new ConcurrentHashMap<String, Date>();


    public ConcurrentHashMap<String, Date> getTimers() {
        return timers;
    }

    public void setTimers(ConcurrentHashMap<String, Date> timers) {
        this.timers = timers;
    }


    /**
     * 打开计时器
     * 打开计时器后 会根据配置好的时间 每隔多久就会去监听一次数据是否需要呗删除
     * 如果当前数据不需要被删除那么就不会删除当前的这个数据
     * 反之 如果这个数据的缓存时间已经失效 那么就需要进行删除 否则数据留着就是浪费空间
     * TODO 后期会将一级缓存升级为二级缓存
     * 升级为二级缓存后 那么一级缓存过期后的数据 就会进入二级缓存 如果二级缓存中时间又失效了 那么就真的将该数据移除
     */
    public static void openTimers() {

        /*
            打开计时器 我们需要用到和添加数据的同一把锁 因为这样才能控制住安全问题 不会引起多线程的一些毛病
         */
        WebKCacheTypeIsObjDataCenter instance = WebKCacheTypeIsObjDataCenter.getInstance();

        // 获取到当前时间
        Date currentDate = new Date();
        // 开启锁以后开始判断当前计时器是否已经启动 如果当前计时器的状态为true  那么就不行对这个计时器进行操作
        if (!ifActive.get()) {
            // 开启一条新的线程以免他会干扰到主线程
            new Thread(() -> {
                synchronized (instance) {
                    // 未开启  我们进来以后需要将状态设置为启动
                    ifActive.set(true);
                    while (true) {
                        // 设置完成后 我们开始写我们自己的逻辑
                        // 遍历timers计时器 如果为空就不进行以下操作 就进入休眠状态
                        if (!timers.isEmpty()) {
                            // 不为空 遍历数据
                            timers.forEach((k, v) -> {
                                // 判断时间是否为空 如果时间为空 那么就不需要进行删除等一些操作
                                if (v != null) {
                                    // 判断时间是否超过现在的时间
                                    if (new Date(v.getTime() + WebKCacheTypeIsObjDataCenter.timeout).compareTo(currentDate) == -1) {
                                        instance.getDatasMap().remove(k);
                                        timers.remove(k);
                                    }
                                }
                            });
                        }
                        // 等于空 进入休眠状态
                        // TODO 这个时间我们需要配置到缓存中去 因为失效的时间和检索的时间 都要交给用户去配置 而不是我们写死掉
                        try {
                            instance.wait(WebKCacheTypeIsObjDataCenter.timeout);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }).start();

        }


    }
}
