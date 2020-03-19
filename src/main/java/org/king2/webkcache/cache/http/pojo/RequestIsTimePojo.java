package org.king2.webkcache.cache.http.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class RequestIsTimePojo {
    private CacheRequest cacheRequest;
    private Date time;
}
