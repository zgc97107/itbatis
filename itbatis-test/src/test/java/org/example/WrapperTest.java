package org.example;

import com.itbatis.wrapper.QueryWrapper;
import com.itbatis.wrapper.UpdateWrapper;
import org.example.bean.Student;
import org.example.bean.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zgc
 * @since 2020/7/7
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WrapperTest {

    @Test
    public void queryWrapper() throws NoSuchMethodException, ClassNotFoundException {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        //查询user的id为1所有字段，返回结果为单条
        User user = wrapper.select(User.class).eq(User::getId, "6").one();
        System.out.println(user);
        wrapper = new QueryWrapper<>();
        //查询user的id小于3的username字段，返回结果为多条
        user = wrapper.select(User.class).eq(User::getId, "6").one();
        System.out.println(user);
//        List<User> list = wrapper.select(User.class,User::getUsername).lt(User::getId, "6").list();
//        System.out.println(list);
    }

    @Test
    public void updateWrapper(){
        UpdateWrapper<Student> updateWrapper = new UpdateWrapper<>();
        //更新cardNum,name字段，查询条件为id=1
        int update = updateWrapper
                .update(Student.class)
                .set(Student::getCardNum, "12312321")
                .set(Student::getName, "update")
                .eq(Student::getId, "1")
                .execute();
        System.out.println(update);
    }
}
