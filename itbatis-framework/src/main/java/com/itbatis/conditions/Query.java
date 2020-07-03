package com.itbatis.conditions;

import com.itbatis.SFunction;

import java.util.List;

/**
 * @author zgc
 * @since 2020/7/3
 * 查询参数方法封装
 */
public interface Query<Children,R> {
     /**
      * 类中所有参数
      * @param rClass 要操作的实体类
      * @return 返回的wrapper子类
      */
     Children select(Class<R> rClass);

     /**
      * 指定参数
      * @param functions 赋值方法
      * @return 返回的wrapper子类
      */
     Children select(Class<R> rClass,SFunction<R,?>... functions);

     /**
      * 执行结果为一条
      * @return 返回的实体对象
      */
     R one();

     /**
      * 多条结果
      * @return 返回的实体对象
      */
     List<R> list();
}
