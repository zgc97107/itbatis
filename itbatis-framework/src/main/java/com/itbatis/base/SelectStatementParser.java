package com.itbatis.base;

import com.itbatis.enums.SqlKeyWord;
import com.itbatis.utils.MappedStatement;
import com.itbatis.utils.ParameterUtil;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zgc
 * @since 2020/7/7
 * 动态生成BaseMapper中的select语句
 */
@Component
public class SelectStatementParser implements BaseMappedStatementParser {
    @Override
    public void parser(MappedStatement mappedStatement, Object param) {
        try {
            Class<?> entityClass = param.getClass();
            String tableName = ParameterUtil.getTableName(entityClass);
            List<String> conditions = new LinkedList<>();
            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.get(param) != null) {
                    //将值不为空的filed创建为条件，格式为fileName='fileValue'
                    conditions.add(ParameterUtil.humpToLine(field.getName())
                            + SqlKeyWord.EQ.value()
                            + "'"
                            + field.get(param).toString()
                            + "'");
                }
            }
            String sql = SqlKeyWord.SELECT.value() + "*"
                    + SqlKeyWord.FROM.value()
                    + tableName
                    + SqlKeyWord.WHERE.value()
                    + String.join(SqlKeyWord.AND.value(), conditions);
            mappedStatement.setSql(sql);
            mappedStatement.setSelectType(SqlKeyWord.SELECT);
            mappedStatement.setResultType(param.getClass().getName());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
