package org.example;

import com.itbatis.sqlsession.SqlSession;
import com.itbatis.sqlsession.SqlSessionFactory;
import com.itbatis.wrapper.QueryWrapper;
import org.example.bean.User;
import org.example.mapper.UserMapper;
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
public class Test {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @org.junit.Test
    public void testMapper() throws NoSuchMethodException, ClassNotFoundException {
        SqlSession sqlSession = sqlSessionFactory.openSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        System.out.println(mapper.selectUserById(1L));
        System.out.println(mapper.selectUserByIdAndUsername(1L,"test"));
        System.out.println(mapper.selectUserByPassword("123456"));
    }

    @org.junit.Test
    public void testWrapper() throws NoSuchMethodException, ClassNotFoundException {
        SqlSession sqlSession = sqlSessionFactory.openSqlSession();
        QueryWrapper<User> wrapper = sqlSession.getWrapper();
        List<User> user = wrapper.eq(User::getUsername,"test").eq(User::getId,"1").list();
        System.out.println(user);
    }
}
