package org.king2.webkcache.cache.imports;

import org.king2.webkcache.cache.annotation.EnableWebKingCache;
import org.king2.webkcache.cache.exceptions.BeanFactoryCaseError;
import org.king2.webkcache.cache.interfaces.impl.ConcurrentWebCache;
import org.king2.webkcache.cache.interfaces.impl.DefaultWebKingCache;
import org.king2.webkcache.cache.pojo.ServerProperties;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * =======================================================
 * 说明:  动态开启web缓存的Import配置类
 * <p>
 * 作者		时间					            注释
 *
 * @author 俞烨        2019-11-18                         创建
 * =======================================================
 */
public class DynamicOpenWebKingCache implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        // 判断是否是DefaultListableBeanFactory 其实一定是DefaultListableBeanFactory 但是为了保守起见
        if (registry instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) registry;

            // 定义用户定义的超时时间
            Integer timeout = 10000;

            // 获取用户配置的超时时间
            MultiValueMap<String, Object> allAnnotationAttributes = importingClassMetadata.getAllAnnotationAttributes(EnableWebKingCache.class.getName());
            if (!CollectionUtils.isEmpty(allAnnotationAttributes)) {
                // 获取到配置的时间
                List<Object> defaultTimeOut = allAnnotationAttributes.get("defaultTimeOut");
                if (!CollectionUtils.isEmpty(defaultTimeOut)) {
                    Object o = defaultTimeOut.get(0);
                    if (o instanceof Integer) {
                        timeout = (Integer) o;
                    }
                }
            }
            // 创建默认的数据
            ConcurrentWebCache webKingCacheTypeIsObj = new ConcurrentWebCache(timeout);
            DefaultWebKingCache defaultWebKingCache = new DefaultWebKingCache(timeout);
            // 注册到BeanFactory中的BeanDefinition中。
            beanFactory.registerSingleton("concurrentCache", webKingCacheTypeIsObj);
            beanFactory.registerSingleton("defaultWebKingCache", defaultWebKingCache);
            defaultWebKingCache.serverProperties = ((DefaultListableBeanFactory) registry).getBean(ServerProperties.class);
        } else {
            try {
                throw new BeanFactoryCaseError("BeanFactory转换异常");
            } catch (BeanFactoryCaseError beanFactoryCaseError) {
                beanFactoryCaseError.printStackTrace();
            }
        }

    }
}
