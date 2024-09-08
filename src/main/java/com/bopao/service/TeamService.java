package com.bopao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bopao.model.domain.Team;
import com.bopao.model.domain.User;
import com.bopao.model.dto.TeamQuery;
import com.bopao.model.request.TeamUpdateRequest;
import com.bopao.model.vo.TeamUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
* @author Bo
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-09-07 15:09:50
*/
public interface TeamService extends IService<Team> {
    /**
     * 创建队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser);

    /**
     * 查询队伍列表
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);
}
