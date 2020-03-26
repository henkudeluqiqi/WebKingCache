package org.king2.webkcache.cache.imports;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.king2.webkcache.cache.annotation.EnableWebKingCache;
import org.king2.webkcache.cache.exceptions.BeanFactoryCaseError;
import org.king2.webkcache.cache.imports.interfaces.ExtendsService;
import org.king2.webkcache.cache.imports.interfaces.service.DefaultExtendsServiceImpl;
import org.king2.webkcache.cache.interfaces.impl.DefaultWebKingCache;
import org.king2.webkcache.cache.pojo.Instantiation;
import org.king2.webkcache.cache.pojo.ServerProperties;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * =======================================================
 * 说明:  动态开启web缓存的Import配置类
 * <p>
 * 作者		时间					            注释
 *
 * @author 俞烨        2019-11-18                         创建
 * =======================================================
 */
@Slf4j
public class DynamicOpenWebKingCache implements ImportBeanDefinitionRegistrar {

    public static final String CACHE_POJO_PATH = "META-INF/cache-pojo.properties";

    @SneakyThrows
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
            // 创建WebKingCache的实例信息
            parse(registry, timeout);

            // 注册用户自己定义的实现类
            registryUserExtendsService(registry);
        } else {
            try {
                throw new BeanFactoryCaseError("BeanFactory转换异常");
            } catch (BeanFactoryCaseError beanFactoryCaseError) {
                beanFactoryCaseError.printStackTrace();
            }
        }

    }

    private void registryUserExtendsService(BeanDefinitionRegistry registry) throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        if (registry instanceof ConfigurableListableBeanFactory) {
            String[] beanNamesForType = ((ConfigurableListableBeanFactory) registry).getBeanNamesForType(ExtendsService.class);
            for (String s : beanNamesForType) {
                ExtendsService bean = (ExtendsService) ((ConfigurableListableBeanFactory) registry).getBean(s);
                for (Instantiation addExtend : bean.addExtends()) {
                    registry.registerBeanDefinition(addExtend.getKey(),
                            new AnnotatedGenericBeanDefinition(Class.forName(addExtend.getClazz())));
                }
            }
        }
    }

    public void parse(BeanDefinitionRegistry registry, Integer timeout) throws Exception {


        for (Instantiation addExtend : new DefaultExtendsServiceImpl().addExtends()) {

            try {
                Class<?> aClass = Class.forName(addExtend.getClazz());
                Object o = aClass.getConstructor(Integer.class).newInstance(timeout);
                if (o instanceof DefaultWebKingCache) {
                    ServerProperties bean = null;
                    try {
                        // 获取实例
                        bean = ((DefaultListableBeanFactory) registry).getBean(ServerProperties.class);
                    } catch (Exception e) {
                        log.warn("当前WebKingCache缓存没有打开持久化功能");
                        bean = new ServerProperties(false, "");
                    }
                    ((DefaultWebKingCache) o).serverProperties = bean == null ?
                            new ServerProperties() : bean;
                    if (bean.isActivePr()) {
                        ((DefaultWebKingCache) o).init();
                    }

                }
                ((DefaultListableBeanFactory) registry).registerSingleton(addExtend.getKey(), o);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
