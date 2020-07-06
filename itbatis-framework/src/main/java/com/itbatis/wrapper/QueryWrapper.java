package com.itbatis.wrapper;

import com.itbatis.SFunction;
import com.itbatis.conditions.Query;
import com.itbatis.enums.SqlKeyWord;
import com.itbatis.executor.Executor;
import com.itbatis.utils.ParameterUtil;
import com.itbatis.utils.SpringApplicationHolder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zgc
 * @since 2020/7/2
 */
public class QueryWrapper<R> extends AbstractWrapper<QueryWrapper<R>, R> implements Query<QueryWrapper<R>, R> {

    private String select;

    public QueryWrapper() {
        executor = SpringApplicationHolder.applicationContext.getBean(Executor.class);
    }

    @Override
    protected void init(Class<R> rClass) {
        super.tableName = ParameterUtil.getTableName(rClass);
        super.resultType = rClass.getName();
    }

    @Override
    public QueryWrapper<R> select(Class<R> rClass) {
        init(rClass);
        select = "*";
        return this;
    }

    @Override
    public QueryWrapper<R> select(Class<R> rClass, SFunction<R, ?>... functions) {
        init(rClass);
        select = Arrays.stream(functions)
                .map(ParameterUtil::getFieldName)
                .collect(Collectors.joining(" ,"));
        return this;
    }

    @Override
    public R one() {
        return list().get(0);
    }

    @Override
    public List<R> list() {
        return executor.query(getMappedStatement(), null);
    }

    @Override
    protected String createSql() {
        StringBuilder sqlBuilder = new StringBuilder(SqlKeyWord.SELECT.value());
        String selectConditions = "*";
        if (select != null && !select.isEmpty()) {
            selectConditions = String.join(" ,", select);
        }
        sqlBuilder.append(selectConditions)
                .append(SqlKeyWord.FROM.value())
                .append(tableName)
                .append(super.createSql());
        return sqlBuilder.toString();
    }
}
