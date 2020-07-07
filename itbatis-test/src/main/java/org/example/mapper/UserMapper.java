package org.example.mapper;

import com.itbatis.annotation.Select;
import com.itbatis.annotation.Update;
import com.itbatis.base.BaseMapper;
import org.example.bean.User;

import java.util.List;
import java.util.Map;


/**
 * @author zgc
 * @since 2020/7/1
 */
public interface UserMapper extends BaseMapper<User> {
    /**
     * 通过id查询
     * @param id
     * @return
     */
    @Select("select * from user where id = ?")
    User selectUserById(Long id);

    /**
     * 多个参数查询，顺序与占位符位置必须保持一致
     * @param id
     * @param username
     * @return
     */
    @Select("select * from user where id = ? and username = ?")
    User selectUserByIdAndUsername(Long id, String username);

    /**
     * 多条返回结果
     * @param password
     * @return
     */
    @Select("select * from user where password = ?")
    List<User> selectUserByPassword(String password);

    /**
     * 将返回字段放入Map
     * @param id
     * @return
     */
    @Select("select * from user where id = ?")
    Map selectMapById(Long id);

    /**
     * insert操作
     * @param username
     * @param password
     * @param phoneNum
     * @return
     */
    @Update("insert into user(username,password,phone_number) values(?,?,?)")
    int save(String username, String password, String phoneNum);

    /**
     * update操作
     * @param username
     * @param id
     * @return
     */
    @Update("update user set username=? where id = ?")
    int update(String username, Long id);

    /**
     * delete操作
     * @param id
     * @return
     */
    @Update("delete from user where id = ?")
    int delete(Long id);
}
