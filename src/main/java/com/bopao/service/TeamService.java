package com.bopao.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bopao.model.domain.Team;
import com.bopao.model.domain.User;
import com.bopao.model.dto.TeamQuery;
import com.bopao.model.request.TeamJoinRequest;
import com.bopao.model.request.TeamQuitRequest;
import com.bopao.model.request.TeamUpdateRequest;
import com.bopao.model.vo.TeamUserVO;
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

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */

    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 队长解散队伍
     * @param id
     * @param loginUser
     * @return
     */
    boolean removeTeam(long id, User loginUser);

    /**
     * 退出队伍
     * @param teamquitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamquitRequest,User loginUser);
}
