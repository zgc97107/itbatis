package com.itbatis.base;

import com.itbatis.annotation.TableId;
import com.itbatis.enums.SqlKeyWord;
import com.itbatis.utils.MappedStatement;
import com.itbatis.utils.ParameterUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zgc
 * @since 2020/7/7
 * 动态生成BaseMapper中的update语句
 */
@Component
public class UpdateStatementParset implements BaseMappedStatementParser {
    @Override
    public void parser(MappedStatement mappedStatement, Object param) {
        Class<?> entityClass = param.getClass();
        String tableName = ParameterUtil.getTableName(entityClass);

        //获取主键field
        String tableId = null;
        Field tableIdField = null;
        for (Field field : entityClass.getDeclaredFields()) {
            TableId annotation = field.getAnnotation(TableId.class);
            if (annotation != null) {
                tableIdField = field;
                tableId = ParameterUtil.humpToLine(field.getName());
                if (!StringUtils.isEmpty(annotation.value())) {
                    tableId = annotation.value();
                }
            }
        }
        if (tableIdField == null) {
            throw new RuntimeException("TableId is Null");
        }
        tableIdField.setAccessible(true);

        try {
            List<String> updateConditions = new LinkedList<>();
            for (Field field : entityClass.getDeclaredFields()) {
                if (field.getAnnotation(TableId.class) == null) {
                    field.setAccessible(true);
                    Object fieldValue = field.get(param);
                    if (fieldValue != null) {
                        //将不为null的字段创建为set条件，格式为fileName='fileValue'
                        updateConditions.add(ParameterUtil.humpToLine(field.getName())
                                + SqlKeyWord.EQ.value()
                                + "'" +
                                fieldValue.toString()
                                + "'");
                    }
                }
            }
            String sql = SqlKeyWord.UPDATE.value()
                    + tableName
                    + SqlKeyWord.SET.value()
                    + String.join(" ,", updateConditions)
                    + SqlKeyWord.WHERE.value()
                    + tableId
                    + SqlKeyWord.EQ.value()
                    + "'"
                    + tableIdField.get(param).toString()
                    + "'";
            mappedStatement.setSql(sql);
            mappedStatement.setSelectType(SqlKeyWord.UPDATE);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
