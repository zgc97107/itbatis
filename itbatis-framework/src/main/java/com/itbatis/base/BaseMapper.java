package com.itbatis.base;

import java.util.List;

/**
 * @author zgc
 * @since 2020/7/2
 */
public interface BaseMapper<T> {

    T selectOne();

    List<T> selectList();
}
