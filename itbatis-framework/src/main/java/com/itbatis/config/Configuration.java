package com.itbatis.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zgc
 * @since 2020/7/1
 */
public class Configuration {

    private String driver;
    private String url;
    private String userName;
    private String passWord;

    private Map<String, MappedStatement> statementMap = new HashMap<>();

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public Map<String, MappedStatement> getStatementMap() {
        return statementMap;
    }

    public void setStatementMap(Map<String, MappedStatement> statementMap) {
        this.statementMap = statementMap;
    }
}
