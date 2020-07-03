package com.itbatis.wrapper;

import com.itbatis.SFunction;
import com.itbatis.config.MappedStatement;
import com.itbatis.executor.Executor;
import com.itbatis.utils.ParameterUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zgc
 * @since 2020/7/2
 */
public class QueryWrapper<R> extends AbstractWrapper<R> {

    final List<String> select = new ArrayList<>();
    final List<String> where = new ArrayList<>();
    final List<String> having = new ArrayList<>();
    final List<String> groupBy = new ArrayList<>();
    final List<String> orderBy = new ArrayList<>();
    final List<String> andOr = new ArrayList<>();

    final String WHERE = " WHERE ";
    final String AND = " AND ";
    final String SELECT = "SELECT ";
    final String FROM = " FROM ";

    private String tableName = null;

    private String resultType = null;

    private Executor executor;

    public QueryWrapper(Executor executor) {
        this.executor = executor;
    }

    @Override
    public QueryWrapper<R> eq(SFunction<R, ?> function, Object value) {
        //设置返回值类型
        resultType = ParameterUtil.getClassName(function);
        tableName = resultType.substring(resultType.lastIndexOf(".") + 1).toLowerCase();
        tableName = ParameterUtil.humpToLine(tableName);
        String key = ParameterUtil.getName(function);
        where.add(key + " = '" + value+"'");
        return this;
    }

    public R one() {
        return list().get(0);
    }

    public List<R> list() {
        MappedStatement mappedStatement = new MappedStatement();
        mappedStatement.setSql(createSql());
        mappedStatement.setResultType(resultType);
        return executor.query(mappedStatement, null);
    }

    private String createSql() {
        StringBuilder sqlBuilder = new StringBuilder(SELECT);
        String selectConditions = "*";
        if (select != null && !select.isEmpty()) {
            selectConditions = String.join(" ,", select);
        }
        sqlBuilder.append(selectConditions).append(FROM).append(tableName).append(WHERE);
        if (where.size()!=0){
            String conditions = String.join(AND, where);
            sqlBuilder.append(conditions);
        }
        return sqlBuilder.toString();
    }
}
