package com.itbatis.datasource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author zgc
 * @since 2020/7/1
 */
@Component
public class DataSourcesProxy {

    private DataSource dataSource;

    @Autowired
    public DataSourcesProxy(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getConnection()  {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Get Connection Failed Exception");
        }
    }
}
