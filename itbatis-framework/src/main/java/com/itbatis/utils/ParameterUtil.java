package com.itbatis.utils;

import com.itbatis.annotation.TableId;
import com.itbatis.annotation.TableName;
import com.itbatis.conditions.SFunction;
import org.springframework.util.StringUtils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author zgc
 * @since 2020/7/1
 */
public class ParameterUtil {
    /**
     * 生成缓存键，规则为sql+参数
     *
     * @param mappedStatement
     * @param args
     * @return
     */
    public static String generatorCacheKey(MappedStatement mappedStatement, Object[] args) {
        String argStr = args == null ? "" :
                Arrays.stream(args)
                        .map(Object::toString)
                        .collect(Collectors.joining(","));
        return mappedStatement.getSql() + ":" + argStr;
    }

    /**
     * 根据Class名获取tableName
     * 默认为类名首字母小写后转下划线
     * 也可以通过@TableName指定
     *
     * @param clazz
     */
    public static String getTableName(Class<?> clazz) {
        TableName annotation = clazz.getAnnotation(TableName.class);
        if (annotation != null) {
            return annotation.value();
        }
        return getTableName(clazz.getSimpleName());
    }

    public static String getTableName(String className) {
        return ParameterUtil.humpToLine(ParameterUtil.lowerFirst(className));
    }

    private static Pattern linePattern = Pattern.compile("_(\\w)");

    /**
     * 下划线转驼峰
     */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    /**
     * 驼峰转下划线
     *
     * @param str
     * @return
     */
    public static String humpToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 从function中取出filed名
     *
     * @param function
     * @param <T>
     * @return
     */
    public static <T> String getFieldName(SFunction<T, ?> function) {
        // 从function取出序列化方法
        Method writeReplaceMethod;
        try {
            writeReplaceMethod = function.getClass().getDeclaredMethod("writeReplace");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        // 从序列化方法取出序列化的lambda信息
        boolean isAccessible = writeReplaceMethod.isAccessible();
        writeReplaceMethod.setAccessible(true);
        SerializedLambda serializedLambda;
        try {
            serializedLambda = (SerializedLambda) writeReplaceMethod.invoke(function);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        writeReplaceMethod.setAccessible(isAccessible);

        // 从lambda信息取出method、field、class等
        String fieldName = serializedLambda.getImplMethodName().substring("get".length());
        fieldName = fieldName.replaceFirst(fieldName.charAt(0) + "", (fieldName.charAt(0) + "").toLowerCase());
        Field field;
        try {
            String className = serializedLambda.getImplClass().replace("/", ".");
            field = Class.forName(className).getDeclaredField(fieldName);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        // 从field取出字段名，可以根据实际情况调整
        TableId tableId = field.getAnnotation(TableId.class);
        if (tableId != null && !StringUtils.isEmpty(tableId.value())) {
            return tableId.value();
        } else {
            return fieldName.replaceAll("[A-Z]", "_$0");
        }
    }

    /**
     * 从function中取出Class名
     *
     * @param function
     * @param <T>
     * @return
     */
    public static <T> String getClassName(SFunction<T, ?> function) {
        // 从function取出序列化方法
        Method writeReplaceMethod;
        try {
            writeReplaceMethod = function.getClass().getDeclaredMethod("writeReplace");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        // 从序列化方法取出序列化的lambda信息
        boolean isAccessible = writeReplaceMethod.isAccessible();
        writeReplaceMethod.setAccessible(true);
        SerializedLambda serializedLambda;
        try {
            serializedLambda = (SerializedLambda) writeReplaceMethod.invoke(function);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        writeReplaceMethod.setAccessible(isAccessible);
        return serializedLambda.getImplClass().replace("/", ".");
    }

    public static String lowerFirst(String value) {
        return value.replaceFirst(value.charAt(0) + "",
                Character.toLowerCase(value.charAt(0)) + "");
    }

}
