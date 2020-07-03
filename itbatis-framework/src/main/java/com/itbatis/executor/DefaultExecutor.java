package com.itbatis.executor;

import com.itbatis.config.Configuration;
import com.itbatis.config.Connections;
import com.itbatis.config.MappedStatement;
import com.itbatis.utils.ParameterUtil;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zgc
 * @since 2020/7/1
 */
public class DefaultExecutor implements Executor {

    private Configuration configuration;

    public DefaultExecutor(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> List<T> query(MappedStatement statement, Object[] params) {
        Connection connection = Connections.getConnection(configuration);
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<T> resultObjs = new LinkedList<>();
        try {
            preparedStatement = connection.prepareStatement(statement.getSql());
            handleParameter(preparedStatement, params);
            resultSet = preparedStatement.executeQuery();
            handleResult(resultSet, resultObjs, statement.getResultType());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return resultObjs;
    }

    private <T> void handleResult(ResultSet resultSet, List<T> resultObjs, String resultType) throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {
        Class<?> aClass = Class.forName(resultType);
        while (resultSet.next()) {
            T result = (T) aClass.newInstance();
            for (Field field : aClass.getDeclaredFields()) {
                String name = field.getName();
                //驼峰命名转为下划线
                name = ParameterUtil.humpToLine(name);
                String type = field.getType().getSimpleName();
                field.setAccessible(true);
                Object value = null;
                switch (type) {
                    case "String":
                        value = resultSet.getString(name);
                        break;
                    case "Integer":
                        value = resultSet.getInt(name);
                        break;
                    case "Long":
                        value = resultSet.getLong(name);
                        break;
                    default:
                        break;
                }
                field.set(result, value);
            }
            resultObjs.add(result);
        }
    }

    private void handleParameter(PreparedStatement statement, Object[] params) throws SQLException {
        if (params == null) {
            return;
        }
        for (int i = 1; i <= params.length; i++) {
            Object param = params[i - 1];
            if (param instanceof Integer) {
                statement.setInt(i, (Integer) param);
            } else if (param instanceof Long) {
                statement.setLong(i, (Long) param);
            } else if (param instanceof String) {
                statement.setString(i, (String) param);
            }
        }
    }
}
