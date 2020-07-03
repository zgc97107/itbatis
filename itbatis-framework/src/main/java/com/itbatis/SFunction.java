package com.itbatis;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author zgc
 * @since 2020/7/2
 */
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}
