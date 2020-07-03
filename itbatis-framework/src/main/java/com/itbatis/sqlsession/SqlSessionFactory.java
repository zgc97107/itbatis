package com.itbatis.sqlsession;

import com.itbatis.config.Configuration;
import com.itbatis.executor.Executor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author zgc
 * @since 2020/7/1
 */
@Component
public class SqlSessionFactory{

    private Configuration configuration;

    private Executor executor;

    @Autowired
    public SqlSessionFactory(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    @Bean
    public SqlSession sqlSession() {
        return new DefaultSqlSession(configuration, executor);
    }
}
