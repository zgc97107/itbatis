package com.itbatis.config;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author zgc
 * @since 2020/7/1
 */
public class Connections {

    public static Connection getConnection(Configuration configuration) {
        Connection connection = null;
        try {
            //加载驱动
            Class.forName(configuration.getDriver());
            connection = DriverManager.getConnection(configuration.getUrl(), configuration.getUsername(), configuration.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;

    }
}
