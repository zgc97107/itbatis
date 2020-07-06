package com.itbatis.sqlsession;

import com.itbatis.utils.Configuration;
import com.itbatis.utils.MappedStatement;
import com.itbatis.executor.Executor;
import com.itbatis.mapped.MappedProxy;

import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author zgc
 * @since 2020/7/1
 * 初始化MappedStatement
 * 创建mapper接口代理
 */
public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;
    private final Executor executor;

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    @Override
    public <T> T selectOne(MappedStatement statement, Object... params) {
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
    public <U> List<U> selectList(MappedStatement statement, Object... params) {
        return executor.query(statement, params);
    }

    @Override
    public int update(MappedStatement statement, Object... params) {
        return executor.update(statement, params);
    }

    /**
     * 创建mapper接口代理
     *
     * @param mapperType
     * @param <T>
     * @return
     */
    @Override
    public <T> T getMapper(Class<T> mapperType) {
        MappedProxy proxy = new MappedProxy();
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[]{mapperType}, proxy);
    }
}
