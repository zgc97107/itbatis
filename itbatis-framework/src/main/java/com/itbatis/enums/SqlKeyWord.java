package com.itbatis.enums;

/**
 * @author zgc
 * @since 2020/7/3
 */
public enum SqlKeyWord {

    SELECT("SELECT "),
    UPDATE("UPDATE "),
    INSERT("INSERT INTO "),
    FROM(" FROM "),
    SET(" SET "),
    AND(" AND "),
    WHERE(" WHERE "),
    VALUES(" VALUES"),
    OR(" OR "),
    IN(" IN "),
    NOT(" NOT "),
    LIKE(" LIKE "),
    EQ(" = "),
    NE(" <> "),
    GT(" > "),
    GE(" >= "),
    LT(" < "),
    LE(" <= ");

    private String value;

    SqlKeyWord(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
