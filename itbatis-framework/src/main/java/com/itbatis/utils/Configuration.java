package com.itbatis.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zgc
 * @since 2020/7/1
 */
@Component
public class Configuration {

    @Value("${datasource.driver}")
    private String driver;
    @Value("${datasource.url}")
    private String url;
    @Value("${datasource.username}")
    private String username;
    @Value("${datasource.password}")
    private String password;

    private static Map<String, MappedStatement> statementMap = new HashMap<>();

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static Map<String, MappedStatement> getStatementMap() {
        return statementMap;
    }

    public static void putToStatementMap(MappedStatement statement){
        statementMap.put(statement.getSourceId(), statement);
    }

    public static void setStatementMap(Map<String, MappedStatement> statementMap) {
        Configuration.statementMap = statementMap;
    }
}
