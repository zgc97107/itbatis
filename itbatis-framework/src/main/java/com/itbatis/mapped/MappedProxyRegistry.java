package com.itbatis.mapped;

import com.itbatis.annotation.Select;
import com.itbatis.config.Configuration;
import com.itbatis.config.MappedStatement;
import com.itbatis.utils.LoadClass;
import com.itbatis.utils.ParameterUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.*;

/**
 * @author zgc
 * @since 2020/7/3
 * 向spring容器中注册mapper接口代理
 */
@Component
public class MappedProxyRegistry implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private String mapperLocation;

    private Set<Class<?>> mapperInterfaces = new HashSet<>();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        mapperLocation = applicationContext.getEnvironment().getProperty("it-batis.mapper-location");
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        loadMapperInfo();
        registryMapperProxy(beanDefinitionRegistry);
    }

    /**
     * 注册mapper代理对象
     * @param beanDefinitionRegistry
     */
    private void registryMapperProxy(BeanDefinitionRegistry beanDefinitionRegistry) {
        mapperInterfaces.forEach(mapperInterface -> {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(mapperInterface);
            AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
            beanDefinition.getPropertyValues().add("interfaceClass", mapperInterface.getName());
            beanDefinition.setBeanClass(MappedProxyFactory.class);
            beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            String beanName = mapperInterface.getSimpleName();
            beanName = ParameterUtil.lowerFirst(beanName);
            beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
        });
    }

    /**
     * 根据@select注解创建MappedStatement对象
     */
    private void loadMapperInfo() {
        List<Class<?>> classes = LoadClass.getClasses(mapperLocation);
        //将方法转为MappedStatement
        classes.stream()
                //获取所有方法
                .map(aClass -> aClass.getDeclaredMethods())
                .flatMap(methods -> Arrays.stream(methods))
                .map(method -> {
                    String id = method.getName();
                    String typeName = method.getGenericReturnType().getTypeName();

                    if (typeName.indexOf("<") != -1) {
                        typeName = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">"));
                        typeName = typeName.equals("java.util.Map") ? "java.util.HashMap" : typeName;
                    }
                    Select select = method.getAnnotation(Select.class);
                    if (select == null) {
                        return null;
                    }

                    String sql = select.value();
                    Class<?> mapperClass = method.getDeclaringClass();
                    mapperInterfaces.add(mapperClass);
                    String namespace = mapperClass.getName();
                    String sourceId = namespace + "." + id;
                    return new MappedStatement(namespace, sourceId, typeName, sql);
                })
                .filter(Objects::nonNull)
                .forEach(statement -> Configuration.getStatementMap().put(statement.getSourceId(), statement));
        //将bean创建方法封装
        mapperInterfaces.forEach(key -> MappedProxyFactory.mappedCache.put(key, () -> {
            MappedProxy proxy = new MappedProxy();
            return Proxy.newProxyInstance(this.getClass().getClassLoader(),
                    new Class[]{key}, proxy);
        }));
    }
}
