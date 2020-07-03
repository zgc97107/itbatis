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
public class QueryWrapper<R> extends AbstractWrapper<QueryWrapper<R>,R> implements Query<QueryWrapper<R>, R> {

    private String select;

    final String WHERE = " WHERE ";
    final String AND = " AND ";
    final String SELECT = "SELECT ";
    final String FROM = " FROM ";

    public QueryWrapper() {
        executor = SpringApplicationHolder.applicationContext.getBean(Executor.class);
    }

    @Override
    public QueryWrapper<R> select(Class<R> rClass) {
        super.init(rClass);
        select = "*";
        return this;
    }

    @Override
    public QueryWrapper<R> select(Class<R> rClass, SFunction<R,?>... functions) {
        super.init(rClass);
        select = Arrays.stream(functions)
                .map(ParameterUtil::getFieldName)
                .collect(Collectors.joining(" ,"));
        return this;
    }

    @Override
    public QueryWrapper<R> eq(SFunction<R,?> function, String value) {
        return addCondition(function, value, SqlKeyWord.EQ);
    }

    @Override
    public QueryWrapper<R> ne(SFunction<R,?> function, String value) {
        return addCondition(function, value, SqlKeyWord.NE);
    }

    @Override
    public QueryWrapper<R> gt(SFunction<R,?> function, String value) {
        return addCondition(function, value, SqlKeyWord.GT);
    }

    @Override
    public QueryWrapper<R> ge(SFunction<R,?> function, String value) {
        return addCondition(function, value, SqlKeyWord.GE);
    }

    @Override
    public QueryWrapper<R> lt(SFunction<R,?> function, String value) {
        return addCondition(function, value, SqlKeyWord.LT);
    }

    @Override
    public QueryWrapper<R> le(SFunction<R,?> function, String value) {
        return addCondition(function, value, SqlKeyWord.LE);
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
        StringBuilder sqlBuilder = new StringBuilder(SELECT);
        String selectConditions = "*";
        if (select != null && !select.isEmpty()) {
            selectConditions = String.join(" ,", select);
        }
        sqlBuilder.append(selectConditions).append(FROM).append(tableName).append(WHERE);
        if (where.size() != 0) {
            String conditions = String.join(AND, where);
            sqlBuilder.append(conditions);
        }
        return sqlBuilder.toString();
    }
}
