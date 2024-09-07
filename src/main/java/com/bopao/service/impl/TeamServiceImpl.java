package com.bopao.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bopao.common.ErrorCode;
import com.bopao.exception.BusinessException;
import com.bopao.exception.ThrowUtils;
import com.bopao.mapper.TeamMapper;
import com.bopao.model.domain.Team;
import com.bopao.model.domain.User;
import com.bopao.model.domain.UserTeam;
import com.bopao.model.enums.TeamStatusEnum;
import com.bopao.service.TeamService;
import com.bopao.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

/**
* @author Bo
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-09-07 15:09:50
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {
    @Resource
    private UserTeamService userTeamService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        //判断请求参数是否为空
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //是否登录
        if (loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //1. 队伍人数1~5
        Integer maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum <1 && maxNum>5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不满足要求");
        }
        //2. 队伍标题小于20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍标题不满足要求");
        }
        //3. 队伍描述大于512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length()>512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述过长");
        }
        //4. status 是否公开（int）不传默认为 0（公开）
        Integer status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum enumByValue = TeamStatusEnum.getEnumByValue(status);
        if (enumByValue == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不符合要求");

        }
        //   5. 如果 status 是加密状态，一定要有密码，且密码 <= 32
        String teamPassword = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(status)){
            if (teamPassword == null || teamPassword.length() <= 8){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码设置不正确");
            }
        }
        // 6. 超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"时间设置不正确");
        }
        // 7. 校验用户最多创建 5 个队伍
        // todo 有 bug，可能同时创建 100 个队伍
        Long loginUserId = loginUser.getId();
        QueryWrapper<Team> queryWrapper=new QueryWrapper<>();
        queryWrapper.like("userId", loginUserId);
        long count = this.count(queryWrapper);
        if (count >=5 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍过多");
        }
        // 8. 插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(loginUserId);
        boolean teamResult = this.save(team);
        Long teamId = team.getId();
        if (!teamResult || teamId ==null ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        // 9. 插入用户  => 队伍关系到关系表
        UserTeam userTeam=new UserTeam();
        userTeam.setUserId(loginUserId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        boolean userTeamResult = userTeamService.save(userTeam);
        ThrowUtils.throwIf(!userTeamResult,ErrorCode.SYSTEM_ERROR,"插入数据失败");
        return teamId;
    }
}




