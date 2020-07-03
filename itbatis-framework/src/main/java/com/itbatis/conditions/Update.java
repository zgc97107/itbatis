package com.itbatis.conditions;

import com.itbatis.SFunction;

/**
 * @author zgc
 * @since 2020/7/3
 */
public interface Update<Children,R> {
    /**
     * 类中所有参数
     * @param entity 要操作的实体类
     * @return 返回的wrapper子类
     */
    Children update(R entity);

    /**
     * 类中指定参数
     * @param entity 要操作的实体类
     * @param function 赋值方法
     * @return 返回的wrapper子类
     */
    Children update(R entity,SFunction... function);

    /**
     * 执行更新
     * @return
     */
    int update();
}
