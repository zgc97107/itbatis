package com.itbatis.mapped;

import com.itbatis.annotation.Select;
import com.itbatis.annotation.Update;
import com.itbatis.base.BaseMapperStatementHandler;
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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
     * 创建mapper代理对象的BeanDefinition并加入到Spring
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
     * 扫描mapper包，为其中的方法创建MappedStatement对象
     */
    private void loadMapperInfo() {
        mapperInterfaces = LoadClass.getClasses(mapperLocation);
        //放入Mapper的父类
        Set<Class<?>> superClasses = mapperInterfaces.stream().map(Class::getInterfaces)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .collect(Collectors.toSet());
        mapperInterfaces.addAll(superClasses);
        //获取类中的所有方法并为其创建MappedStatement对象
        mapperInterfaces.stream()
                .map(Class::getDeclaredMethods)
                .flatMap(Arrays::stream)
                .map(this::createMappedStatement)
                .filter(Objects::nonNull)
                .forEach(Configuration::putToStatementMap);
        //将bean创建方法封装，并放入map中
        mapperInterfaces.forEach(MappedProxyFactory::putMappedCreator);
    }

    /**
     * 创建MappedStatement对象
     * @param method
     * @return
     */
    private MappedStatement createMappedStatement(Method method){
        String id = method.getName();
        String typeName = method.getGenericReturnType().getTypeName();

        //如果返回值为List<?>形式，则取?作为返回类型
        if (typeName.contains("<")) {
            typeName = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">"));
        }

        //如果为Map类型则替换为HashMap
        typeName = "java.util.Map".equals(typeName) ? "java.util.HashMap" : typeName;

        Class<?> mapperClass = method.getDeclaringClass();
        String namespace = mapperClass.getName();
        String sourceId = namespace + "." + id;
        //baseMapper中的sql需要动态生成
        if (BaseMapperStatementHandler.baseStatementHandlerMapping.containsKey(sourceId)) {
            return new MappedStatement(namespace, sourceId, null, typeName, sourceId);
        }
        //select方法
        Select select = method.getAnnotation(Select.class);
        if (select != null) {
            String sql = select.value();
            return new MappedStatement(namespace, sourceId, SqlKeyWord.SELECT, typeName, sql);
        }
        //update方法
        Update update = method.getAnnotation(Update.class);
        if (update != null) {
            String sql = update.value();
            return new MappedStatement(namespace, sourceId, SqlKeyWord.UPDATE, typeName, sql);
        }
        return null;
    }
}
