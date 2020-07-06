package com.itbatis.sqlsession;

import com.itbatis.utils.MappedStatement;

import java.util.List;

/**
 * @author zgc
 * @since 2020/7/1
 */
public interface SqlSession {
    <T> T selectOne(MappedStatement statement, Object... params);

    <T> List<T> selectList(MappedStatement statement, Object... params);

    int update(MappedStatement statement,Object... params);

    <T> T getMapper(Class<T> type);
}
