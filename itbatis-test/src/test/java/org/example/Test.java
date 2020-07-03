package org.example;

import com.itbatis.wrapper.QueryWrapper;
import org.example.bean.User;
import org.example.mapper.UserMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zgc
 * @since 2020/7/1
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {

    @Autowired
    private UserMapper mapper;

    @org.junit.Test
    public void testMapper() throws NoSuchMethodException, ClassNotFoundException {
        System.out.println(mapper.selectUserById(1L));
        System.out.println(mapper.selectUserByIdAndUsername(1L,"test"));
        System.out.println(mapper.selectUserByPassword("123456"));
        System.out.println(mapper.selectMapById(1L));
    }

    @org.junit.Test
    public void testWrapper() throws NoSuchMethodException, ClassNotFoundException {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        System.out.println(wrapper.select(User.class,User::getId,User::getUsername).gt(User::getId, "1").list());
    }
}
