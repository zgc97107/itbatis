package com.itbatis.conditions;

import com.itbatis.SFunction;

/**
 * @author zgc
 * @since 2020/7/3
 * 查询条件方法封装
 */
public interface Condition<Children,R> {
    /**
     * 等于
     * @return
     */
    Children eq(SFunction<R,?> function, String value);

    /**
     * 不等于
     * @return
     */
    Children ne(SFunction<R,?> function, String value);

    /**
     * 大于
     * @return
     */
    Children gt(SFunction<R,?> function, String value);

    /**
     * 大于等于
     * @return
     */
    Children ge(SFunction<R,?> function, String value);

    /**
     * 小于
     * @return
     */
    Children lt(SFunction<R,?> function, String value);

    /**
     * 小于等于
     * @return
     */
    Children le(SFunction<R,?> function, String value);
}
