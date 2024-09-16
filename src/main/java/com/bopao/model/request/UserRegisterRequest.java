package com.bopao.model.request;

import lombok.Data;
import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author <a href="https://github.com/JavaBo14">Bo</a>
 * @from https://github.com/JavaBo14/Matching
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String emailCode;

    private String email;

    private String planetCode;
}
