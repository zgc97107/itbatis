package com.itbatis.sqlsession;

import com.itbatis.config.Configuration;
import com.itbatis.config.MappedStatement;
import com.itbatis.executor.Executor;
import com.itbatis.wrapper.QueryWrapper;

import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author zgc
 * @since 2020/7/1
 */
public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;
    private final Executor executor;

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    @Override
    public <T> T selectOne(String statement, Object... params) {
        List<T> results = selectList(statement, params);
        if (results == null || results.size() == 0) {
            return null;
        } else if (results.size() == 1) {
            return results.get(0);
        } else {
            throw new RuntimeException("too many result");
        }

    }

    @Override
    public <U> List<U> selectList(String statement, Object... params) {
        MappedStatement mappedStatement = this.configuration.getStatementMap().get(statement);
        return executor.query(mappedStatement, params);
    }

    @Override
    public <T> T getMapper(Class<T> mapperType) {
        MappedProxy proxy = new MappedProxy(this);
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{mapperType}, proxy);
    }
}
