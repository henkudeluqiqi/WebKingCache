package org.king2.webkcache.cache.annotation;

import org.king2.webkcache.cache.http.aspect.HttpWebKingCache;
import org.king2.webkcache.cache.imports.DynamicOpenHttpCache;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 开启WebKingCache对Http的支持
 */
@Retention(RetentionPolicy.RUNTIME)
@Import({DynamicOpenHttpCache.class, HttpWebKingCache.class})
@EnableAspectJAutoProxy
public @interface EnableHttpWebKingCache {
}
