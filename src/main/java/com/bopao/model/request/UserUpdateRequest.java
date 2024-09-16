package com.bopao.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户实体
 *
 * @author <a href="https://github.com/JavaBo14">Bo</a>
 * @from https://github.com/JavaBo14/Matching
 */
@Data
public class UserUpdateRequest implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;
    /**
     * id
     */
    private Long id;
    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 电话
     */
    private String phone;


    /**
     * 邮箱
     */
    private String email;


    /**
     * 星球编号
     */
    private String planetCode;

}
