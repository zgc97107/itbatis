package com.itbatis.config;

/**
 * @author zgc
 * @since 2020/7/1
 */
public class MappedStatement {

    private String namespace;
    private String sourceId;
    private String resultType;
    private String sql;

    public MappedStatement(){

    }

    public MappedStatement(String namespace,String sourceId,String resultType,String sql){
        this.namespace = namespace;
        this.sourceId = sourceId;
        this.resultType = resultType;
        this.sql = sql;
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
