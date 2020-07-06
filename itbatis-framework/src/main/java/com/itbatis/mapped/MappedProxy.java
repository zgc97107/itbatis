package com.itbatis.mapped;

import com.itbatis.annotation.TableId;
import com.itbatis.enums.SqlKeyWord;
import com.itbatis.sqlsession.SqlSession;
import com.itbatis.utils.Configuration;
import com.itbatis.utils.MappedStatement;
import com.itbatis.utils.ParameterUtil;
import com.itbatis.utils.SpringApplicationHolder;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author zgc
 * @since 2020/7/1
 * mapped代理对象
 */
public class MappedProxy implements InvocationHandler {

    public static final Set<String> BASE_METHODS = new HashSet<>(3);

    static {
        BASE_METHODS.add("selectOne");
        BASE_METHODS.add("selectList");
        BASE_METHODS.add("updateById");
        BASE_METHODS.add("save");
    }

    private SqlSession sqlSession;

    public MappedProxy() {
        this.sqlSession = SpringApplicationHolder.applicationContext.getBean(SqlSession.class);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> returnType = method.getReturnType();
        String statement = method.getDeclaringClass().getName() + "." + method.getName();
        MappedStatement mappedStatement = Configuration.getStatementMap().get(statement);
        //处理baseMapper方法
        String sql = mappedStatement.getSql();
        if (BASE_METHODS.contains(sql)) {
            handlerSql(mappedStatement, args[0]);
        }
        // update语句
        if (mappedStatement.getSelectType().equals(SqlKeyWord.UPDATE)) {
            return sqlSession.update(mappedStatement, args);
            // select语句，返回多条查询结果
        } else if (Collection.class.isAssignableFrom(returnType)) {
            return sqlSession.selectList(mappedStatement, args);
            // select语句，返回单条查询结果
        } else {
            return sqlSession.selectOne(mappedStatement, args);
        }
    }

    public void handlerSql(MappedStatement mappedStatement, Object param) {
        try {
            String method = mappedStatement.getSql();
            String sql = null;
            Class<?> entityClass = param.getClass();
            String tableName = ParameterUtil.getTableName(entityClass);
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
            switch (method) {
                case "selectOne":
                    sql = SqlKeyWord.SELECT.value()
                            + "*"
                            + SqlKeyWord.FROM.value()
                            + tableName
                            + SqlKeyWord.WHERE.value()
                            + tableId
                            + SqlKeyWord.EQ.value()
                            + "'"
                            + tableIdField.get(param).toString()
                            + "'";
                    mappedStatement.setSql(sql);
                    mappedStatement.setSelectType(SqlKeyWord.SELECT);
                    mappedStatement.setResultType(param.getClass().getName());
                    break;
                case "selectList":
                    List<String> conditions = new LinkedList<>();
                    for (Field field : entityClass.getDeclaredFields()) {
                        field.setAccessible(true);
                        if (field.get(param) != null) {
                            conditions.add(ParameterUtil.humpToLine(field.getName())
                                    + SqlKeyWord.EQ.value()
                                    + "'"
                                    + field.get(param).toString()
                                    + "'");
                        }
                    }
                    sql = SqlKeyWord.SELECT.value() + "*"
                            + SqlKeyWord.FROM.value()
                            + tableName
                            + SqlKeyWord.WHERE.value()
                            + String.join(" ,", conditions);
                    mappedStatement.setSql(sql);
                    mappedStatement.setSelectType(SqlKeyWord.SELECT);
                    mappedStatement.setResultType(param.getClass().getName());
                    break;
                case "updateById":
                    List<String> updateConditions = new LinkedList<>();
                    for (Field field : entityClass.getDeclaredFields()) {
                        if (field.getAnnotation(TableId.class) == null) {
                            field.setAccessible(true);
                            Object fieldValue = field.get(param);
                            if (fieldValue != null) {
                                updateConditions.add(ParameterUtil.humpToLine(field.getName())
                                        + SqlKeyWord.EQ.value()
                                        + "'" +
                                        fieldValue.toString()
                                        + "'");
                            }
                        }
                    }
                    sql = SqlKeyWord.UPDATE.value()
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
                    break;
                case "save":
                    List<String> fieldConditions = new LinkedList<>();
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
                    sql = SqlKeyWord.INSERT.value()
                            + tableName
                            + "(" + fieldCondition + ")"
                            + SqlKeyWord.VALUES.value()
                            + "(" + valueCondition + ")";
                    mappedStatement.setSql(sql);
                    mappedStatement.setSelectType(SqlKeyWord.UPDATE);
                    break;
                default:
                    break;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
