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
 * 动态生成BaseMapper中的delete语句
 */
@Component
public class DeleteStatementParser implements BaseMappedStatementParser {
    @Override
    public void parser(MappedStatement mappedStatement, Object param) {
        try {
            Class<?> entityClass = param.getClass();
            String tableName = ParameterUtil.getTableName(entityClass);
            List<String> deleteConditions = new LinkedList<>();
            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true);
                //将不为null的field创建为条件，格式为fileName = 'fileValue'
                if (field.get(param) != null) {
                    deleteConditions.add(ParameterUtil.humpToLine(field.getName())
                            + SqlKeyWord.EQ.value()
                            + "'"
                            + field.get(param).toString()
                            + "'");
                }
            }
            //拼接sql
            String sql = SqlKeyWord.DELETE.value()
                    + SqlKeyWord.FROM.value()
                    + tableName
                    + SqlKeyWord.WHERE.value()
                    + String.join(" ,", deleteConditions);
            mappedStatement.setSql(sql);
            mappedStatement.setSelectType(SqlKeyWord.UPDATE);
            mappedStatement.setResultType(param.getClass().getName());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
