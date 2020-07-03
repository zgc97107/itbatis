package org.example.mapper;

import com.itbatis.annotation.Select;
import org.example.bean.User;

import java.util.List;


/**
 * @author zgc
 * @since 2020/7/1
 */
public interface UserMapper {
    @Select("select * from user where id = ?")
    User selectUserById(Long id);

    @Select("select * from user where id = ? and username = ?")
    User selectUserByIdAndUsername(Long id,String username);

    @Select("select * from user where password = ?")
    List<User> selectUserByPassword(String password);
}
