package com.itbatis.wrapper;

import com.itbatis.SFunction;

/**
 * @author zgc
 * @since 2020/7/2
 */
public abstract class AbstractWrapper<R> {

    public abstract AbstractWrapper<R> eq(SFunction<R,?> function, Object value);

}
