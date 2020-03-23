package org.king2.webkcache.cache.http.pojo;

/**
 * 缓存返回值
 */
public class CacheResponse {

    private String token;
    private Object value;


    public CacheResponse(String token, Object value) {
        this.token = token;
        this.value = value;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
