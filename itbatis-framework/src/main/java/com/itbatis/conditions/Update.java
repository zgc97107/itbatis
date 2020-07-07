package com.itbatis.conditions;

/**
 * @author zgc
 * @since 2020/7/3
 */
public interface Update<Children,R> {
    /**
     * 类中所有参数
     * @param rClass 要操作的实体类
     * @return 返回的wrapper子类
     */
    Children update(Class<R> rClass);

    /**
     * 更新指定参数
     * @param function 赋值方法
     * @param value 值
     * @return 返回的wrapper子类
     */
    Children set(SFunction<R,?> function,String value);

    /**
     * 执行更新
     * @return
     */
    int execute();
}
