package com.bopao.model.request;

// 本项目_所属 [程序员鱼皮](https://github.com/liyupi)

import lombok.Data;

import java.io.Serializable;

/**
 * 用户加入队伍请求体
 *
 * @author <a href="https://github.com/JavaBo14">Bo</a>
 * @from https://github.com/JavaBo14/Matching
 */
@Data
public class TeamJoinRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;
}
