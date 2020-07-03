package com.itbatis.mapped;

import com.itbatis.sqlsession.SqlSession;
import com.itbatis.utils.SpringApplicationHolder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author zgc
 * @since 2020/7/1
 * mapped代理对象
 */
public class MappedProxy implements InvocationHandler {

    private SqlSession sqlSession;

    public MappedProxy() {
        this.sqlSession = SpringApplicationHolder.applicationContext.getBean(SqlSession.class);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> returnType = method.getReturnType();
        String statement = method.getDeclaringClass().getName() + "." + method.getName();
        if (Collection.class.isAssignableFrom(returnType)) {
            return sqlSession.selectList(statement, args);
        } else {
            return sqlSession.selectOne(statement, args);
        }
    }
}
