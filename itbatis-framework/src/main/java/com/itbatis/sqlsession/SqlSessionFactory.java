package com.itbatis.sqlsession;

import com.itbatis.annotation.Select;
import com.itbatis.config.Configuration;
import com.itbatis.config.MappedStatement;
import com.itbatis.executor.Executor;
import com.itbatis.utils.LoadClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author zgc
 * @since 2020/7/1
 */
@Component
public class SqlSessionFactory {

    private Configuration configuration;

    @Value("${it-batis.mapper-location}")
    private String mapperLocation;

    private Executor executor;

    @Autowired
    public SqlSessionFactory(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    /**
     * 根据@select注解创建MappedStatement对象
     */
    @PostConstruct
    private void loadMapperInfo() {
        List<Class<?>> classes = LoadClass.getClasses(mapperLocation);
        //将方法转为MappedStatement
        classes.stream()
                //获取所有方法
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

    @Bean
    public SqlSession sqlSession() {
        return new DefaultSqlSession(configuration, executor);
    }
}
