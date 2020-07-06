package com.itbatis.mapped;

import com.itbatis.annotation.Select;
import com.itbatis.annotation.Update;
import com.itbatis.enums.SqlKeyWord;
import com.itbatis.utils.Configuration;
import com.itbatis.utils.LoadClass;
import com.itbatis.utils.MappedStatement;
import com.itbatis.utils.ParameterUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zgc
 * @since 2020/7/3
 * 向spring容器中注册mapper接口代理
 */
@Component
public class MappedProxyRegistry implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private String mapperLocation;

    private List<Class<?>> mapperInterfaces;

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
     *
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
     * 创建MappedStatement对象
     */
    private void loadMapperInfo() {
        mapperInterfaces = LoadClass.getClasses(mapperLocation);
        Set<Class<?>> superClasses = mapperInterfaces.stream().map(Class::getInterfaces)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .collect(Collectors.toSet());
        mapperInterfaces.addAll(superClasses);
        //将方法转为MappedStatement
        mapperInterfaces.stream()
                //获取所有方法
                .map(Class::getDeclaredMethods)
                .flatMap(Arrays::stream)
                .map(method -> {
                    String id = method.getName();
                    String typeName = method.getGenericReturnType().getTypeName();
                    if (typeName.contains("<")) {
                        typeName = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">"));
                        typeName = "java.util.Map".equals(typeName) ? "java.util.HashMap" : typeName;
                    }
                    Class<?> mapperClass = method.getDeclaringClass();
                    String namespace = mapperClass.getName();
                    String sourceId = namespace + "." + id;
                    //处理baseMapper方法
                    if (MappedProxy.BASE_METHODS.contains(method.getName())) {
                        String sql = method.getName();
                        return new MappedStatement(namespace, sourceId, null, typeName, sql);
                    }

                    Select select = method.getAnnotation(Select.class);
                    if (select != null) {
                        String sql = select.value();
                        return new MappedStatement(namespace, sourceId, SqlKeyWord.SELECT, typeName, sql);
                    }
                    Update update = method.getAnnotation(Update.class);
                    if (update != null) {
                        String sql = update.value();
                        return new MappedStatement(namespace, sourceId, SqlKeyWord.UPDATE, typeName, sql);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .forEach(statement -> Configuration.getStatementMap().put(statement.getSourceId(), statement));
        //将bean创建方法封装，并放入map中
        mapperInterfaces.forEach(key -> MappedProxyFactory.mappedCache.put(key, () -> {
            MappedProxy proxy = new MappedProxy();
            return Proxy.newProxyInstance(this.getClass().getClassLoader(),
                    new Class[]{key}, proxy);
        }));
    }
}
