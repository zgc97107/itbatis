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
 * 动态生成BaseMapper中的insert语句
 */
@Component
public class InsertStatementParser implements BaseMappedStatementParser {
    @Override
    public void parser(MappedStatement mappedStatement, Object param) {
        try {
            Class<?> entityClass = param.getClass();
            String tableName = ParameterUtil.getTableName(entityClass);
            //存储需要插入的字段
            List<String> fieldConditions = new LinkedList<>();
            //字段的值
            List<String> valuesConditions = new LinkedList<>();
            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(param);
                if (value != null) {
                    fieldConditions.add(ParameterUtil.humpToLine(field.getName()));
                    valuesConditions.add("'" + value.toString() + "'");
                }
            }
            String fieldCondition = String.join(" ,", fieldConditions);
            String valueCondition = String.join(" ,", valuesConditions);
            String sql = SqlKeyWord.INSERT.value()
                    + tableName
                    + "(" + fieldCondition + ")"
                    + SqlKeyWord.VALUES.value()
                    + "(" + valueCondition + ")";
            mappedStatement.setSql(sql);
            mappedStatement.setSelectType(SqlKeyWord.UPDATE);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
