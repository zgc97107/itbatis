package com.itbatis.mapped;

/**
 * @author zgc
 * @since 2020/7/3
 * 用于封装mapper创建方法
 */
public interface MappedProxyCreator<R> {
    R create();
}
