package com.itbatis.mapped;

import com.itbatis.base.BaseMapperStatementHandler;
import com.itbatis.enums.SqlKeyWord;
import com.itbatis.sqlsession.SqlSession;
import com.itbatis.sqlsession.SqlSessionFactory;
import com.itbatis.utils.MappedStatement;
import com.itbatis.utils.ParameterUtil;
import com.itbatis.utils.SpringApplicationHolder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zgc
 * @since 2020/7/1
 * mapped接口代理对象
 */
public class MappedProxy implements InvocationHandler {

    private SqlSessionFactory sqlSessionFactory;
    private BaseMapperStatementHandler baseMapperStatementParser;

    private ConcurrentHashMap<String, Object> resultCache = new ConcurrentHashMap<>();

    public MappedProxy() {
        this.baseMapperStatementParser = SpringApplicationHolder.applicationContext.getBean(BaseMapperStatementHandler.class);
        this.sqlSessionFactory = SpringApplicationHolder.applicationContext.getBean(SqlSessionFactory.class);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> returnType = method.getReturnType();
        String statement = method.getDeclaringClass().getName() + "." + method.getName();
        MappedStatement mappedStatement = MappedProxyRegistry.getStatementMap().get(statement);
        // 处理baseMapper方法
        String sql = mappedStatement.getSql();
        if (BaseMapperStatementHandler.baseStatementHandlerMapping.containsKey(sql)) {
            baseMapperStatementParser.parse(mappedStatement, args[0]);
        }
        SqlSession sqlSession = sqlSessionFactory.sqlSession();
        // update语句
        if (mappedStatement.getSelectType().equals(SqlKeyWord.UPDATE)) {
            // 清空缓存
            resultCache.clear();
            return sqlSession.update(mappedStatement, args);
            // select语句，返回多条查询结果
        } else {
            String key = ParameterUtil.generatorCacheKey(mappedStatement, args);
            // 从缓存中获取
            if (resultCache.containsKey(key)) {
                return resultCache.get(key);
            }
            Object result = Collection.class.isAssignableFrom(returnType) ?
                    sqlSession.selectList(mappedStatement, args)
                    : sqlSession.selectOne(mappedStatement, args);
            // 放入缓存
            if (result != null) {
                resultCache.put(key, result);
            }
            return result;
        }
    }
}
