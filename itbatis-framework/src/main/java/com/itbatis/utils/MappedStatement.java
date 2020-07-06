package com.itbatis.utils;

import com.itbatis.enums.SqlKeyWord;

/**
 * @author zgc
 * @since 2020/7/1
 */
public class MappedStatement {

    private String namespace;
    private String sourceId;
    private SqlKeyWord selectType;
    private String resultType;
    private String sql;

    public MappedStatement(){

    }

    public MappedStatement(String namespace, String sourceId, SqlKeyWord selectType, String resultType, String sql){
        this.namespace = namespace;
        this.sourceId = sourceId;
        this.selectType = selectType;
        this.resultType = resultType;
        this.sql = sql;
    }

    public SqlKeyWord getSelectType() {
        return selectType;
    }

    public void setSelectType(SqlKeyWord selectType) {
        this.selectType = selectType;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }
}
