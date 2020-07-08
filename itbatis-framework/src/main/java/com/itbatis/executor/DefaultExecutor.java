package com.itbatis.executor;

import com.itbatis.datasource.DataSourcesProxy;
import com.itbatis.utils.MappedStatement;
import com.itbatis.utils.ParameterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zgc
 * @since 2020/7/1
 * sql执行器
 */
@Component
public class DefaultExecutor implements Executor {

    private final Logger LOG = LoggerFactory.getLogger(DefaultExecutor.class);

    private ConcurrentHashMap<String, List> resultCache = new ConcurrentHashMap<>();

    @Autowired
    public DefaultExecutor(DataSourcesProxy dataSourcesProxy) {
        this.dataSourcesProxy = dataSourcesProxy;
    }

    private DataSourcesProxy dataSourcesProxy;

    @Override
    public int update(MappedStatement statement, Object[] params) {
        // 清空缓存
        resultCache.clear();

        Connection connection = dataSourcesProxy.getConnection();
        PreparedStatement preparedStatement = null;
        int result = -1;
        try {
            preparedStatement = connection.prepareStatement(statement.getSql());
            handleParameter(preparedStatement, params);
            LOG.info(preparedStatement.toString());
            result = preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public <T> List<T> query(MappedStatement statement, Object[] params) {
        //从缓存中获取
        String cacheKey = ParameterUtil.generatorCacheKey(statement, params);
        if (resultCache.containsKey(cacheKey)) {
            return resultCache.get(cacheKey);
        }

        Connection connection = dataSourcesProxy.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<T> resultObjs = new LinkedList<>();
        try {
            preparedStatement = connection.prepareStatement(statement.getSql());
            handleParameter(preparedStatement, params);
            LOG.info(preparedStatement.toString());
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
        // 放入缓存
        resultCache.put(cacheKey, resultObjs);
        return resultObjs;
    }

    /**
     * 处理查询参数
     *
     * @param statement
     * @param params
     * @throws SQLException
     */
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

    /**
     * 处理结果查询结果
     *
     * @param resultSet
     * @param resultObjs
     * @param resultType 返回对象类型
     * @param <T>        返回对象类型
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private <T> void handleResult(ResultSet resultSet, List<T> resultObjs, String resultType) throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {
        Class<?> aClass = Class.forName(resultType);
        while (resultSet.next()) {
            T result = (T) aClass.newInstance();
            if (result instanceof Map) {
                putField(resultSet, (Map) result);
            } else {
                mappingField(resultSet, aClass, result);
            }
            resultObjs.add(result);
        }
    }

    /**
     * 将结果解析为Map
     *
     * @param resultSet
     * @param result
     * @param <T>
     * @throws SQLException
     */
    private <T> void putField(ResultSet resultSet, Map result) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnName(i + 1);
            Object value = resultSet.getObject(columnName);
            columnName = ParameterUtil.lineToHump(columnName);
            result.put(columnName, value);
        }
    }

    /**
     * 将结果映射到T
     *
     * @param resultSet
     * @param aClass
     * @param result
     * @param <T>
     * @throws IllegalAccessException
     */
    private <T> void mappingField(ResultSet resultSet, Class<?> aClass, T result) throws IllegalAccessException {
        for (Field field : aClass.getDeclaredFields()) {
            String name = field.getName();
            //驼峰命名转为下划线
            name = ParameterUtil.humpToLine(name);
            String type = field.getType().getSimpleName();
            field.setAccessible(true);
            Object value = null;
            try {
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
            } catch (SQLException e) {
                continue;
            }
            field.set(result, value);
        }
    }


}
