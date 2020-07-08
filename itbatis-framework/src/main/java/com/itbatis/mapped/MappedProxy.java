package com.itbatis.mapped;

import com.itbatis.base.BaseMapperStatementHandler;
import com.itbatis.enums.SqlKeyWord;
import com.itbatis.sqlsession.SqlSession;
import com.itbatis.utils.MappedStatement;
import com.itbatis.utils.SpringApplicationHolder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author zgc
 * @since 2020/7/1
 * mapped接口代理对象
 */
public class MappedProxy implements InvocationHandler {

    private SqlSession sqlSession;
    private BaseMapperStatementHandler baseMapperStatementParser;

    public MappedProxy() {
        this.baseMapperStatementParser = SpringApplicationHolder.applicationContext.getBean(BaseMapperStatementHandler.class);
        this.sqlSession = SpringApplicationHolder.applicationContext.getBean(SqlSession.class);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> returnType = method.getReturnType();
        String statement = method.getDeclaringClass().getName() + "." + method.getName();
        MappedStatement mappedStatement = MappedProxyRegistry.getStatementMap().get(statement);
        //处理baseMapper方法
        String sql = mappedStatement.getSql();
        if (BaseMapperStatementHandler.baseStatementHandlerMapping.containsKey(sql)) {
            baseMapperStatementParser.parse(mappedStatement,args[0]);
        }
        // update语句
        if (mappedStatement.getSelectType().equals(SqlKeyWord.UPDATE)) {
            return sqlSession.update(mappedStatement, args);
            // select语句，返回多条查询结果
        } else if (Collection.class.isAssignableFrom(returnType)) {
            return sqlSession.selectList(mappedStatement, args);
            // select语句，返回单条查询结果
        } else {
            return sqlSession.selectOne(mappedStatement, args);
        }
    }
}
