package org.king2.webkcache.cache.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 删除Key的Data
 */
@Data
@AllArgsConstructor
public class RemoveKeyData {

    private String key;
    private String path;
}
