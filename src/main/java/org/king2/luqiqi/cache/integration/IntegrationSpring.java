package org.king2.luqiqi.cache.integration;

import org.king2.luqiqi.cache.annotations.EnableCache;
import org.king2.luqiqi.cache.config.CacheCommonConfig;
import org.king2.luqiqi.cache.realize.DefaultCache;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * 项目名称：CACHE-KING2-V1.0
 * 类 名 称：IntegrationSpring
 * 类 描 述：集成Cache到Spring中
 * 创建时间：2020/7/23 10:07 上午
 * 创 建 人：俞烨-company-mac
 */
public class IntegrationSpring implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        if (beanDefinitionRegistry instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanDefinitionRegistry;
            // 获取到注解的内容
            MultiValueMap<String, Object> allAnnotationAttributes = annotationMetadata.getAllAnnotationAttributes(EnableCache.class.getName());
            if (!CollectionUtils.isEmpty(allAnnotationAttributes)) {
                // 获取到配置的时间
                List<Object> defaultTimeOut = allAnnotationAttributes.get("persistence");
                if (!CollectionUtils.isEmpty(defaultTimeOut)) {
                    Object o = defaultTimeOut.get(0);
                    if (o instanceof Boolean) {
                        CacheCommonConfig.PERSISTENCE = (Boolean) o;
                    }
                }
                List<Object> maxCacheMemory = allAnnotationAttributes.get("maxCacheMemory");
                if (!CollectionUtils.isEmpty(maxCacheMemory)) {
                    Object o = maxCacheMemory.get(0);
                    if (o instanceof Long) {
                        CacheCommonConfig.MAX_CACHE_MEMORY = (Long) o;
                    }
                }

                // 创建数据，然后添加到Spring容器中
                DefaultCache instance = DefaultCache.getInstance();
                defaultListableBeanFactory.registerSingleton("defaultCache", instance);
            }
        }
    }
}
