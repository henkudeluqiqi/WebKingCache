package org.king2.webkcache.cache.imports;

import org.king2.webkcache.cache.http.aspect.HttpWebKingCache;
import org.king2.webkcache.cache.http.clietn.CacheClient;
import org.king2.webkcache.cache.http.pojo.CacheServer;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class DynamicOpenHttpCache implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        if (registry instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) registry;
            CacheServer bean = beanFactory.getBean(CacheServer.class);
            if (bean == null) {
                throw new RuntimeException("请配置WebKingCache服务器的信息，如不知怎么配置请参照官网。");
            }

            // 这时候可以进行连接了
            CacheClient cacheClient = new CacheClient(bean.getHost(), bean.getPort());
            try {
                cacheClient.start();
            } catch (Exception e) {
                throw new RuntimeException("连接服务器失败：" + e);
            }

            try {
                HttpWebKingCache cache = HttpWebKingCache.getInstance();
                cache.setDefaultCacheClient(cacheClient);
                registry.registerBeanDefinition("defaultHttpWebKingCache",
                        new AnnotatedGenericBeanDefinition(cache.getClass()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
