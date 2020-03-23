package org.king2.webkcache.cache.http.aspect;

import io.netty.channel.Channel;
import org.king2.webkcache.cache.definition.CacheDefinition;
import org.king2.webkcache.cache.http.clietn.CacheClient;
import org.king2.webkcache.cache.http.pojo.CacheRequest;
import org.king2.webkcache.cache.http.pojo.MsgType;
import org.king2.webkcache.cache.http.pojo.RequestIsTimePojo;
import org.king2.webkcache.cache.interfaces.WebKingCache;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 如果开启了远程的缓存那么我们就需要使用AOP切入方法
 */
public class HttpWebKingCache implements WebKingCache {

    private HttpWebKingCache() {
    }

    private static final HttpWebKingCache HTTP_WEB_KING_CACHE = new HttpWebKingCache();

    public static HttpWebKingCache getInstance() {
        return HTTP_WEB_KING_CACHE;
    }

    /**
     * 当前正在休眠的所有线程信息
     */
    public static final Map<String, CountDownLatch> LATCH = new ConcurrentHashMap<>();
    /**
     * 异步返回的消息结果
     */
    public static final Map<String, Object> YB_RETURN_OBJ = new ConcurrentHashMap<>();
    /**
     * 存放发送前的数据，以防数据丢失
     */
    public static final Map<String, RequestIsTimePojo> REQUEST_MAP = new ConcurrentHashMap<>();

    private CacheClient defaultCacheClient = new CacheClient("172.20.10.8", 8888);


    public void start() throws Exception {
        if (defaultCacheClient != null) {
            defaultCacheClient.start();
        }
    }

    @Override
    public Object get(String key) throws Exception {

        // 唯一token
        String token = UUID.randomUUID().toString() + System.currentTimeMillis();
        CountDownLatch value = new CountDownLatch(1);
        LATCH.put(token, value);
        // 开始发送信息
        send(new CacheRequest(key, null, MsgType.GET, token));
        value.await(10, TimeUnit.SECONDS);
        // 等待唤醒，一但唤醒就将返回值取出并返回
        Object o = YB_RETURN_OBJ.remove(token);
        // 消费成功后也需要删除对应的数据
        return o;
    }

    @Override
    public int size() {

        // 唯一token
        String token = UUID.randomUUID().toString() + System.currentTimeMillis();
        CountDownLatch value = new CountDownLatch(1);
        LATCH.put(token, value);
        // 开始发送信息
        send(new CacheRequest(null, null, MsgType.SIZE, token));
        try {
            value.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 等待唤醒，一但唤醒就将返回值取出并返回
        Object o = YB_RETURN_OBJ.remove(token);
        if (o instanceof Integer) {
            return (int) o;
        }

        return 0;
    }


    @Override
    public Object set(String key, Object value, boolean saveFlag, Integer timeout) throws Exception {
        // 唯一token
        String token = UUID.randomUUID().toString() + System.currentTimeMillis();
        CacheRequest data = new CacheRequest(key, new CacheDefinition(value, saveFlag, timeout), MsgType.SET, token);
        REQUEST_MAP.put(token, new RequestIsTimePojo(data, new Date()));
        send(data);
        return "该方法为异步调用,为了提升效率暂时无法获取到返回值";
    }

    @Override
    public Object set(String key, Object value, boolean saveFlag) throws Exception {
        return set(key, value, saveFlag, 100000);
    }

    @Override
    public Object set(String key, Object value) throws Exception {
        return set(key, value, true);
    }

    @Override
    public void cr() {
        // 唯一token
        String token = UUID.randomUUID().toString() + System.currentTimeMillis();
        CacheRequest data = new CacheRequest(null, null, MsgType.CR);
        REQUEST_MAP.put(token, new RequestIsTimePojo(data, new Date()));
        send(data);
    }


    @Override
    public Object remove(String key) {
        // 唯一token
        String token = UUID.randomUUID().toString() + System.currentTimeMillis();
        CacheRequest data = new CacheRequest(null, null, MsgType.REMOVE);
        REQUEST_MAP.put(token, new RequestIsTimePojo(data, new Date()));
        send(data);
        return "异步调用为了提升效率暂时无法获取到返回值";
    }

    public void send(CacheRequest data) {
        Channel channel = null;
        try {
            channel = defaultCacheClient.getChannel();
            channel.writeAndFlush(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        defaultCacheClient.getChannel().close();
    }

    public boolean isActive() {
        return defaultCacheClient.getIsActive().get();
    }

    public CacheClient getDefaultCacheClient() {
        return defaultCacheClient;
    }

    public void setDefaultCacheClient(CacheClient defaultCacheClient) {
        this.defaultCacheClient = defaultCacheClient;
    }

    public boolean containsKey(String key) throws Exception {
        return get(key) != null;
    }
}
