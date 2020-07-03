package com.itbatis.config;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author zgc
 * @since 2020/7/1
 * 连接类，后续接入连接器
 */
public class Connections {

    public static Connection getConnection(Configuration configuration) {
        //TODO 接入连接池
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
