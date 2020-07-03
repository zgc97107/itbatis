package com.itbatis.sqlsession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author zgc
 * @since 2020/7/1
 */
public class MappedProxy implements InvocationHandler {

    private SqlSession sqlSession;

    public MappedProxy(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> returnType = method.getReturnType();
        String statement = method.getDeclaringClass().getName() + "." + method.getName();
        if (Collection.class.isAssignableFrom(returnType)) {
            return sqlSession.selectList(statement, args);
        }else {
            return sqlSession.selectOne(statement, args);
        }
    }
}
