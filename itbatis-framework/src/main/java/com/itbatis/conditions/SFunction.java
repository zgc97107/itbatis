package com.itbatis.conditions;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author zgc
 * @since 2020/7/2
 * 用于序列化Function方法，使之后可以获取filedName
 */
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}
