package com.bopao.service;


import com.bopao.model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 用户服务测试
 *
 * @author
 * @from
 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    /**
     * 测试添加用户
     */
    @Test
    public void testAddUser() {
        User user = new User();
        user.setUsername("Yupi");
        user.setUserAccount("asdfgd");
        user.setAvatarUrl("https://vip.helloimg.com/i/2024/05/02/6633586a8550e.jpg");
        user.setGender(0);
        user.setUserPassword("123456789");
        user.setPhone("123");
        user.setEmail("456");
        user.setUserRole(2);
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void update() {
        User user = new User();
        user.setId(2L);
        user.setUsername("123321");
        user.setUserAccount("123321");
        user.setAvatarUrl("321321312/img/logo.png");
        user.setGender(0);
        user.setUserPassword("32132");
        user.setPhone("3213");
        user.setEmail("4563213");
        User user1 = userService.userUpdate(user);
        System.out.println(user1.getId());
    }


    /**
     * 测试更新用户
     */
    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("dogYupi");
        user.setUserAccount("123");
        user.setAvatarUrl("https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("456");
        boolean result = userService.updateById(user);
        Assertions.assertTrue(result);
    }

    /**
     * 测试删除用户
     */
    @Test
    public void testDeleteUser() {
        boolean result = userService.removeById(1L);
        Assertions.assertTrue(result);
    }


    /**
     * 测试获取用户
     */
    @Test
    public void testGetUser() {
        User user = userService.getById(1L);
        Assertions.assertNotNull(user);
    }

    /**
     * 测试用户注册
     */
    @Test
    void userRegister() {
        String userAccount = "yupi";
        String userPassword = "";
        String checkPassword = "123456";
        String planetCode = "1";
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "yu";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "yupi";
        userPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "yu pi";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        checkPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "dogYupi";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "yupi";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
    }

    @Test
    void userLogin() {
    }

    @Test
    void getSafetyUser() {
    }

    @Test
    void userLogout() {
    }

    @Test
    void userUpdate() {
    }

    @Test
    public void searchUsersByTags() {
        List<String> tagNameList= Arrays.asList("java","c++");
        List<User> userList = userService.searchUsersByTags(tagNameList);
        //断言如果返回的用户列表为空，则测试失败；如果不为空，则测试通过
        Assert.assertNotNull(userList);
  }
}