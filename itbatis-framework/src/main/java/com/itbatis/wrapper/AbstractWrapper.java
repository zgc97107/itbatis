package com.itbatis.wrapper;

import com.itbatis.SFunction;
import com.itbatis.conditions.Condition;
import com.itbatis.config.MappedStatement;
import com.itbatis.enums.SqlKeyWord;
import com.itbatis.executor.Executor;
import com.itbatis.utils.ParameterUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zgc
 * @since 2020/7/2
 */
public abstract class AbstractWrapper<Children,R> implements Condition<Children,R> {

    protected final List<String> where = new ArrayList<>();
    protected final List<String> having = new ArrayList<>();
    protected final List<String> groupBy = new ArrayList<>();
    protected final List<String> orderBy = new ArrayList<>();
    protected final List<String> andOr = new ArrayList<>();

    protected Children typeThis = (Children) this;

    protected String tableName;

    protected String resultType;

    protected Executor executor;

    protected void init(Class<R> rClass) {
        tableName = ParameterUtil.getTableName(rClass);
        resultType = rClass.getName();
    }

    protected Children addCondition(SFunction<?, ?> sFunction, String value, SqlKeyWord keyWord) {
        String field = ParameterUtil.getFieldName(sFunction);
        String condition = keyWord.value();
        where.add(field + condition + "'" + value + "'");
        return typeThis;
    }


    protected MappedStatement getMappedStatement() {
        MappedStatement mappedStatement = new MappedStatement();
        mappedStatement.setSql(createSql());
        mappedStatement.setResultType(resultType);
        return mappedStatement;
    }

    protected abstract String createSql();

}
