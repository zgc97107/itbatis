package org.example.bean;

import com.itbatis.annotation.TableId;

/**
 * @author zgc
 * @since 2020/7/6
 */
public class Student {
    @TableId
    private Integer id;
    private String cardNum;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", cardNum='" + cardNum + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
