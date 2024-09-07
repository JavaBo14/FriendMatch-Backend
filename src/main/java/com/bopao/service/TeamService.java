package com.bopao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bopao.model.domain.Team;
import com.bopao.model.domain.User;


/**
* @author Bo
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-09-07 15:09:50
*/
public interface TeamService extends IService<Team> {

    long addTeam(Team team, User loginUser);


}
