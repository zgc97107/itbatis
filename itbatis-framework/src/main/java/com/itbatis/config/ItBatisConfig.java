package com.itbatis.config;

import com.itbatis.sqlsession.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zgc
 * @since 2020/7/1
 */
@Configuration
public class ItBatisConfig {
    @Value("${datasource.driver}")
    private String driver;
    @Value("${datasource.url}")
    private String url;
    @Value("${datasource.username}")
    private String username;
    @Value("${datasource.password}")
    private String password;
    @Value("${it-batis.mapper-location}")
    private String mapperLocation;
    @Bean
    public SqlSessionFactory sqlSessionFactory(){
        return new SqlSessionFactory(driver,url,username,password,mapperLocation);
    }
}
