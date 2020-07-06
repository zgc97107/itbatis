package com.itbatis.mapped;

import org.springframework.beans.factory.FactoryBean;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zgc
 * @since 2020/7/3
 * 用于BeanDefinition创建对象
 */
public class MappedProxyFactory<T> implements FactoryBean<T> {

    private Class<T> interfaceClass;

    public static Map<Class<?>, MappedProxyCreator<?>> mappedCache = new HashMap<>();

    /**
     * spring将通过此方法创建对象
     * @return
     * @throws Exception
     */
    @Override
    public T getObject() throws Exception {
        return (T) mappedCache.get(interfaceClass).builder();
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }
}
