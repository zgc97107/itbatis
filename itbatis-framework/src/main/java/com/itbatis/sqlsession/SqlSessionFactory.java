package com.itbatis.sqlsession;

import com.itbatis.annotation.Select;
import com.itbatis.config.Configuration;
import com.itbatis.config.MappedStatement;
import com.itbatis.executor.DefaultExecutor;
import com.itbatis.utils.LoadClass;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author zgc
 * @since 2020/7/1
 */
public class SqlSessionFactory {

    private String mapperLocation;

    private Configuration configuration = new Configuration();

    public SqlSessionFactory(String driver, String url, String username, String password, String mapperLocation) {
        this.mapperLocation = mapperLocation;
        configuration.setDriver(driver);
        configuration.setUrl(url);
        configuration.setUserName(username);
        configuration.setPassWord(password);
        loadMapperInfo();
    }

    private void loadMapperInfo() {
        List<Class<?>> classes = LoadClass.getClasses(mapperLocation);
        //将方法转为MappedStatement
        classes.stream()
                .map(aClass -> aClass.getDeclaredMethods())
                .flatMap(methods -> Arrays.stream(methods))
                .map(method -> {
                    String id = method.getName();
                    String typeName = method.getGenericReturnType().getTypeName();
                    if (typeName.indexOf("<") != -1) {
                        typeName = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">"));
                    }
                    Select select = method.getAnnotation(Select.class);
                    if (select == null) {
                        return null;
                    }
                    String sql = select.value();
                    String namespace = method.getDeclaringClass().getName();
                    String sourceId = namespace + "." + id;
                    return new MappedStatement(namespace, sourceId, typeName, sql);
                })
                .filter(Objects::nonNull)
                .forEach(statement -> configuration.getStatementMap().put(statement.getSourceId(), statement));
    }

    public SqlSession openSqlSession() {
        return new DefaultSqlSession(configuration, new DefaultExecutor(configuration));
    }
}
