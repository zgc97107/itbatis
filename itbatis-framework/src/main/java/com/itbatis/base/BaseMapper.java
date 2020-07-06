package com.itbatis.base;

import java.util.List;

/**
 * @author zgc
 * @since 2020/7/2
 */
public interface BaseMapper<T> {

    T selectOne(T entity);

    List<T> selectList(T entity);

    int updateById(T entity);

    int save(T entity);

    int delete(T entity);
}
