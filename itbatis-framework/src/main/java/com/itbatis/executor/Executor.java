package com.itbatis.executor;

import com.itbatis.config.MappedStatement;

import java.util.List;

/**
 * @author zgc
 * @since 2020/7/1
 */
public interface Executor {
    <T> List<T> query(MappedStatement statement,Object[] params);
}
