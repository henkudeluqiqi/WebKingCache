package org.king2.webkcache.cache.annotation;

import org.king2.webkcache.cache.imports.DynamicOpenWebKingCache;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * =======================================================
 * 说明:  是否开启WebKingCache的缓存功能
 * <p>
 * 作者		时间					            注释
 *
 * @author 俞烨        2019-11-18                         创建
 * =======================================================
 */
@Retention(RetentionPolicy.RUNTIME)
@Import(DynamicOpenWebKingCache.class)
public @interface EnableWebKingCache {

    /**
     * 默认超时的时间
     *
     * @return
     */
    int defaultTimeOut() default 10000;
}
