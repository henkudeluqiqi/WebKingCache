package org.king2.webkcache.cache.http.pojo;

/**
 * 消息类型
 */
public enum MsgType {

    // 添加一个数据
    SET,
    // 获取一个数据
    GET,
    // 获取当前缓存的全部数据量
    SIZE,
    // 删除一个数据
    REMOVE,
    // 再次唤醒数据回收器
    CR,
    // PING
    PING,
    // 异步方法
    A_SYN
}
