package org.king2.webkcache.cache.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实例化的Pojo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Instantiation {

    private String key;
    private String clazz;
}
