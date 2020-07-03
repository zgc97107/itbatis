package com.itbatis.sqlsession;

import java.util.List;

/**
 * @author zgc
 * @since 2020/7/1
 */
public interface SqlSession {
    <T> T selectOne(String statement, Object... params);

    <T> List<T> selectList(String statement, Object... params);

    <T> T getMapper(Class<T> type);
}
