package com.itbatis.base;

import com.itbatis.utils.MappedStatement;

/**
 * @author zgc
 * @since 2020/7/7
 */
public interface BaseMappedStatementParser {
    /**
     * 动态生成baseMapper中的sql
     * @param mappedStatement
     * @param param
     */
    void parser(MappedStatement mappedStatement, Object param);
}
