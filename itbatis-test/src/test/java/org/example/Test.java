package org.example;

import com.itbatis.wrapper.UpdateWrapper;
import org.example.bean.Student;
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
//        User user = mapper.selectUserById(1L);
//        System.out.println(user);
//        user = mapper.selectUserByIdAndUsername(1L, "test");
//        List<User> userList = mapper.selectUserByPassword("123456");
//        System.out.println(userList);
//        List<Map> map = mapper.selectMapById(1L);
//        System.out.println(map);
//        int result = mapper.updateUserById("updateTest", "123", "123");
        User entity = new User();
        entity.setUsername("insert test");
        entity.setPassword("123");
        entity.setPhoneNumber("123");
        System.out.println(mapper.save(entity));
//        System.out.println(mapper.selectList(entity));
    }

    @org.junit.Test
    public void testWrapper() throws NoSuchMethodException, ClassNotFoundException {
//        QueryWrapper<User> wrapper = new QueryWrapper<>();
//        List<User> userList = wrapper.select(User.class, User::getId, User::getPhoneNumber, User::getPassword)
//                .gt(User::getPhoneNumber, "123456")
//                .list();
//        System.out.println(userList);
//        wrapper = new QueryWrapper<>();
//        User user = wrapper.select(User.class).eq(User::getId, "1").one();
//        System.out.println(user);
//
//        QueryWrapper<Student> studentWrapper = new QueryWrapper<>();
//        System.out.println(studentWrapper.select(Student.class).eq(Student::getId, "1").one());
        int update = new UpdateWrapper<Student>().update(Student.class).set(Student::getCardNum,"12312321").set(Student::getName, "update").eq(Student::getId, "1").execute();
        System.out.println(update);
    }
}
