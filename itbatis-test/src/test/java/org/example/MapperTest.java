package org.example;

import org.example.mapper.UserMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zgc
 * @since 2020/7/7
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MapperTest {

    @Autowired
    private UserMapper mapper;

    @org.junit.Test
    public void testMapper() throws NoSuchMethodException, ClassNotFoundException {
//        int result = mapper.save("saveTest", "123", "123");
//        System.out.println(result);
        System.out.println(mapper.selectMapById(6L));
//        System.out.println(mapper.update("updateTest3", 4L));
        System.out.println(mapper.selectMapById(6L));
//        System.out.println(mapper.delete(2L));
    }
}
