package org.king2.webkcache.cache.http.pojo;

import org.king2.webkcache.cache.definition.CacheDefinition;

/**
 * 发送消息的Data
 */
public class CacheRequest {

    // 消息的key
    private String key;
    // 消息的主体
    private CacheDefinition cacheDefinition;
    // 消息的类型
    private MsgType msgType;
    // 当前线程的唯一token
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public CacheRequest() {
    }

    public CacheRequest(String key, CacheDefinition cacheDefinition, MsgType msgType, String token) {
        this.key = key;
        this.cacheDefinition = cacheDefinition;
        this.msgType = msgType;
        this.token = token;
    }

    public CacheRequest(String key, CacheDefinition cacheDefinition, MsgType msgType) {
        this.key = key;
        this.cacheDefinition = cacheDefinition;
        this.msgType = msgType;
    }

    public CacheDefinition getCacheDefinition() {
        return cacheDefinition;
    }

    public void setCacheDefinition(CacheDefinition cacheDefinition) {
        this.cacheDefinition = cacheDefinition;
    }

    public MsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
