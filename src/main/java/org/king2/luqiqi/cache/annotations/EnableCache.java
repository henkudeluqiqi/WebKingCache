package org.king2.luqiqi.cache.annotations;

import org.king2.luqiqi.cache.integration.IntegrationSpring;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 项目名称：CACHE-KING2-V1.0
 * 类 名 称：EnableCache
 * 类 描 述：开启缓存的注解
 * 创建时间：2020/7/23 10:04 上午
 * 创 建 人：俞烨-company-mac
 */
@Retention(RetentionPolicy.RUNTIME)
@Import(IntegrationSpring.class)
public @interface EnableCache {

    /***
     * 默认的缓存中最大cache对象数量
     * @return
     */
    long maxCacheMemory() default 1000000L;

    /***
     * 是否开启持久化机制
     * @return
     */
    boolean persistence() default false;
}
