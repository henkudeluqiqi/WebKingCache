package org.king2.webkcache.cache.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务器配置
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerProperties {

    // 是否启动了持久化机制
    private boolean isActivePr;
    // 持久化的数据存入的地址
    private String prPath;
}
