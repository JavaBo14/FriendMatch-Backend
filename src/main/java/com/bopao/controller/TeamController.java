package com.bopao.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bopao.common.BaseResponse;
import com.bopao.common.DeleteRequest;
import com.bopao.common.ErrorCode;
import com.bopao.common.ResultUtils;
import com.bopao.exception.BusinessException;
import com.bopao.exception.ThrowUtils;
import com.bopao.model.domain.Team;
import com.bopao.model.domain.User;
import com.bopao.model.dto.TeamQuery;
import com.bopao.model.request.TeamAddRequest;
import com.bopao.model.request.TeamUpdateRequest;
import com.bopao.model.vo.TeamUserVO;
import com.bopao.service.TeamService;
import com.bopao.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/team")
public class TeamController {


    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建队伍
     *
     * @param teamAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team=new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        teamService.addTeam(team,loginUser);
        return ResultUtils.success(team.getId());
    }

    /**
     * 删除队伍
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = teamService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新队伍
     *
     * @param teamUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest,
                                            HttpServletRequest request) {
        if (teamUpdateRequest == null || teamUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest,loginUser);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取队伍
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        ThrowUtils.throwIf(team == null, ErrorCode.SYSTEM_ERROR);
        return ResultUtils.success(team);
    }

    /**
     * 获取队伍列表
     *
     * @param teamQuery
     * @param
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery,HttpServletRequest httpServletRequest) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(httpServletRequest);
        List<TeamUserVO> listTeam = teamService.listTeams(teamQuery, isAdmin);


        return ResultUtils.success(listTeam);
    }
    /**
     * 分页获取队伍列表
     *
     * @param teamQuery
     * @param
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);
        Page<Team> page = new Page<>(teamQuery.getCurrent(), teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> resultPage = teamService.page(page, queryWrapper);
        return ResultUtils.success(resultPage);
    }
    // endregion

}
