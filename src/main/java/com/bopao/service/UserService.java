package com.bopao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bopao.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bopao.model.request.UserRegisterRequest;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author
 * @from
 */
@Service
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userRegisterRequest
     * @return
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);


    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取当前登录用户
     * @param
     * @return
     */
    User getLoginUser(HttpServletRequest request);
    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     * 通过标签查询用户
     * @param tagNameList
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList);

    /**
     * 主页推荐
     * @param pageSize
     * @param pageNum
     * @param request
     * @return
     */
    Page<User> recommendUsers(long pageSize, long pageNum, HttpServletRequest request);

    /**
     * 随机匹配
     * @param num
     * @param loginUser
     * @return
     */
    List<User> matchUsers(long num, User loginUser);

    /**
     * 发送邮箱
     * @param email
     * @param type
     * @return
     */
//    String sendEmail(String email, String type);
}
