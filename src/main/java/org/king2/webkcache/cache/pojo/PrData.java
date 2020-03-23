package org.king2.webkcache.cache.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.king2.webkcache.cache.definition.CacheDefinition;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class PrData implements Serializable {
    private String key;
    private CacheDefinition cacheDefinition;
    private String path;
}
