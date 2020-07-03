package org.example;

import com.itbatis.sqlsession.SqlSessionFactory;
import com.itbatis.wrapper.QueryWrapper;
import org.example.bean.User;
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
    private SqlSessionFactory sqlSessionFactory;

    @org.junit.Test
    public void testMapper() throws NoSuchMethodException, ClassNotFoundException {
//        SqlSession sqlSession = sqlSessionFactory.openSqlSession();
//        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
//        System.out.println(mapper.selectUserById(1L));
//        System.out.println(mapper.selectUserByIdAndUsername(1L,"test"));
//        System.out.println(mapper.selectUserByPassword("123456"));
    }

    @org.junit.Test
    public void testWrapper() throws NoSuchMethodException, ClassNotFoundException {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        System.out.println(wrapper.select(User.class).le(User::getId, "3").list());
    }
}
