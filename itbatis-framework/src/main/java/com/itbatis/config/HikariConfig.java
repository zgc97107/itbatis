package com.itbatis.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author zgc
 * @since 2020/7/8
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.datasource",name = "type",havingValue = "com.zaxxer.hikari.HikariDataSource")
public class HikariConfig {
    @Value("${spring.datasource.url}")
    private String url;

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druid(){
        HikariDataSource hikariDataSource = new HikariDataSource();
        //Hikari连接池的url属性名为jdbcUrl，配置中为url，故此处手动映射
        hikariDataSource.setJdbcUrl(url);
        return hikariDataSource;
    }

}
