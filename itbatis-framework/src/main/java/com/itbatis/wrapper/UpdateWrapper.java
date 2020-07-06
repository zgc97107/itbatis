package com.itbatis.wrapper;

import com.itbatis.SFunction;
import com.itbatis.conditions.Update;
import com.itbatis.enums.SqlKeyWord;
import com.itbatis.executor.Executor;
import com.itbatis.utils.ParameterUtil;
import com.itbatis.utils.SpringApplicationHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zgc
 * @since 2020/7/6
 */
public class UpdateWrapper<R> extends AbstractWrapper<UpdateWrapper<R>, R> implements Update<UpdateWrapper<R>, R> {

    final List<String> set = new ArrayList<>();

    public UpdateWrapper() {
        executor = SpringApplicationHolder.applicationContext.getBean(Executor.class);
    }


    @Override
    protected void init(Class<R> rClass) {
        super.tableName = ParameterUtil.getTableName(rClass);
    }

    @Override
    protected String createSql() {
        StringBuilder sqlBuilder = new StringBuilder(SqlKeyWord.UPDATE.value());
        sqlBuilder.append(tableName)
                .append(SqlKeyWord.SET.value());
        String setCondition = String.join(" ,", set);
        sqlBuilder.append(setCondition)
                .append(super.createSql());
        return sqlBuilder.toString();
    }

    @Override
    public UpdateWrapper<R> update(Class<R> rClass) {
        init(rClass);
        return this;
    }

    @Override
    public UpdateWrapper<R> set(SFunction<R,?> function, String value) {
        set.add(ParameterUtil.getFieldName(function)
                + SqlKeyWord.EQ.value()
                + "'" + value + "'");
        return this;
    }

    @Override
    public int execute() {
        return executor.update(getMappedStatement(), null);
    }
}
