# itbatis

#### 介绍
因个人爱好而写的一个ORM框架，目前只作为springboot的组件，尚在开发阶段，暂命名为itbatis（不会起名）。

#### 可用功能

- 支持扫描并创建mapper接口的代理对象，已整合到spring的beanFactory，支持自动注入。
- 支持数据库字段映射至实体类，支持下划线命名转为驼峰命名。
- 提供TableId、TableField、TableName注解，自定义主键字段名、普通字段名、表名（某些情况下会失效）。
- 提供BaseMapper、自定义Sql、Wrapper方式进行增删改查操作。


#### 软件架构
- itbatis-framework：itbatis框架
- itbatis-test：框架测试项目

#### 配置说明

application.yml相关配置

```yml
#扫描的mapper接口位置
it-batis:
  mapper-location: org.example.mapper
#数据库信息
datasource:
  driver:  com.mysql.jdbc.Driver
  url: jdbc:mysql://localhost:3306/test
  username: root
  password: root
```

#### 使用说明

- baseMapper的使用

  实体类

  ```java
  @Data
  @Accessors(chain = true)
  public class Student {
      @TableId
      private Integer id;
      private String cardNum;
      private String name;
  }
  ```

  接口

  ```java
  public interface StudentMapper extends BaseMapper<Student> {
  }
  ```

  示例

  ```java
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
  ```

- wrapper的使用

  示例

  ```java
  @RunWith(SpringRunner.class)
  @SpringBootTest
  public class WrapperTest {
  
      @Test
      public void queryWrapper() throws NoSuchMethodException, ClassNotFoundException {
          QueryWrapper<User> wrapper = new QueryWrapper<>();
          //查询user的id为1所有字段，返回结果为单条
          User user = wrapper.select(User.class).eq(User::getId, "1").one();
          System.out.println(user);
          wrapper = new QueryWrapper<>();
          //查询user的id小于3的username字段，返回结果为多条
          List<User> list = wrapper.select(User.class,User::getUsername).lt(User::getId, "3").list();
          System.out.println(list);
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
  ```

- 自定义语句

  ```java
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
  ```

