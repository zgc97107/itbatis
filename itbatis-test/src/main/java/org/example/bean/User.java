package org.example.bean;

import com.itbatis.annotation.TableId;
import org.example.mapper.UserMapper;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author zgc
 * @since 2020/7/1
 */
public class User {
    @TableId
    private Long id;
    private String username;
    private String password;
    private String phoneNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<UserMapper> userMapperClass = UserMapper.class;
        for (Method method : userMapperClass.getDeclaredMethods()) {
            Type returnType = method.getGenericReturnType();
            System.out.println(returnType.getTypeName());
            System.out.println(returnType);
        }
//        Class<?> aClass = Class.forName("java.util.List<org.example.bean.User>");
//        Object o = aClass.newInstance();
    }
}
