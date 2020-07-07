package org.example;

import org.example.bean.Student;
import org.example.mapper.StudentMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author zgc
 * @since 2020/7/1
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BaseMapperTest {

    @Autowired
    private StudentMapper studentMapper;

    /**
     * 执行insert操作
     * 语句中的字段会根据实体类中不为null的属性生成
     */
    @Test
    public void saveTest() {
        Student user = new Student()
                .setName("testUser")
                .setCardNum("12345678");
        studentMapper.save(user);
    }

    /**
     * 执行update操作
     * 语句中更新字段会根据实体类中不为null的属性生成
     * 注意：此方法为根据id更新数据，故传入的实体类id不能为null，且需要@TableId注解指定
     */
    @Test
    public void updateTest() {
        Student user = new Student()
                .setName("testUser")
                .setCardNum("123456798")
                .setId(1);
        studentMapper.updateById(user);
    }

    /**
     * 执行select操作
     * 语句的查询条件会根据实体类中不为null的属性生成
     * selectOne()方法适用于单条返回结果
     * selectList()方法适用于多条返回结果
     */
    @Test
    public void selectTest() {
        Student user = new Student().setId(2);
        user = studentMapper.selectOne(user);
        System.out.println(user);
        user.setId(null).setName(null).setCardNum("12345678");
        List<Student> userList = studentMapper.selectList(user);
        System.out.println(userList);
    }

    /**
     * 执行delete操作
     * 语句的查询条件会根据传入的实体类中不为null的属性盛昌
     */
    @Test
    public void deleteTest() {
        Student user = new Student().setId(1);
        studentMapper.delete(user);
    }
}
