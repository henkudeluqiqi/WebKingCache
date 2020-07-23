package org.king2.luqiqi.cache.exceptions;

import org.king2.luqiqi.cache.interfaces.Cache;

/**
 * 项目名称：CACHE-KING2-V1.0
 * 类 名 称：CacheSizeUpperLimitException
 * 类 描 述：数据已满的异常类
 * 创建时间：2020/7/22 4:13 下午
 * 创 建 人：俞烨-company-mac
 */
public class CacheSizeUpperLimitException extends Exception {


    public CacheSizeUpperLimitException() {
        super("缓存池拒绝添加，原因：缓存池已满。");
    }
}
