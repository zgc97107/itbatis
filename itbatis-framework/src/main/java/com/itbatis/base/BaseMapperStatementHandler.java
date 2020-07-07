package com.itbatis.base;

import com.itbatis.utils.MappedStatement;
import com.itbatis.utils.SpringApplicationHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;


/**
 * @author zgc
 * @since 2020/7/7
 * 动态处理BaseMapper中的语句
 * 适配器模式
 */
@Component
public class BaseMapperStatementHandler {

    /**
     * key:sourceId
     * value:对应的解析类名称
     */
    public static Map<String, String> baseStatementHandlerMapping;

    static {
        try {
            InputStream in = ClassLoader.getSystemResourceAsStream("config/handler.properties");
            Properties properties = new Properties();
            properties.load(in);
            baseStatementHandlerMapping = properties.keySet().stream().collect(
                    Collectors.toMap(Object::toString, key -> properties.getProperty(key.toString()))
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parse(MappedStatement mappedStatement, Object param) {
        String sourceId = mappedStatement.getSourceId();
        String handlerPath = baseStatementHandlerMapping.get(sourceId);
        BaseMappedStatementParser handler = (BaseMappedStatementParser) SpringApplicationHolder.applicationContext.getBean(handlerPath);
        handler.parser(mappedStatement, param);
    }
}
