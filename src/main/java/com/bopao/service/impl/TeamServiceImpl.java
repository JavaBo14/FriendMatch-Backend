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
import com.bopao.model.dto.TeamQuery;
import com.bopao.model.enums.TeamStatusEnum;
import com.bopao.model.request.TeamJoinRequest;
import com.bopao.model.request.TeamQuitRequest;
import com.bopao.model.request.TeamUpdateRequest;
import com.bopao.model.vo.TeamUserVO;
import com.bopao.model.vo.UserVO;
import com.bopao.service.TeamService;
import com.bopao.service.UserService;
import com.bopao.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.*;

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

    @Resource
    private UserService userService;

    /**
     * 创建队伍
     * @param team
     * @param loginUser
     * @return
     */
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
        if (maxNum <1 || maxNum>5){
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
        if (TeamStatusEnum.SECRET.equals(enumByValue)){
            if (StringUtils.isBlank(teamPassword) || teamPassword.length() >= 8){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码设置不正确");
            }
        }
        //todo
        //如果status为公开的不允许设置密码
        if (TeamStatusEnum.PUBLIC.equals(enumByValue) && StringUtils.isNotBlank(teamPassword)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"公开房间不允许设置密码");
        }
        if (team.getUserId() != loginUser.getId()){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"只能创建自己的队伍");
        }
        // 6. 超时时间 > 当前时间
        // todo 时区问题
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"时间设置不正确");
        }
        // 7. 校验用户最多创建 5 个队伍
        // todo 有 bug，可能同时创建 100 个队伍(加锁)
        Long loginUserId = loginUser.getId();
        QueryWrapper<Team> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("userId", loginUserId);
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

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser) {
        if (teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(teamUpdateRequest.getId());
        if (oldTeam == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        }
        //只有管理员或者队伍的创建者可以修改
        //todo 测试好像有问题
        if (oldTeam.getUserId() != loginUser.getId() && !userService.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        TeamStatusEnum enumByValue = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        String password = teamUpdateRequest.getPassword();
        if (enumByValue.equals(TeamStatusEnum.SECRET) && StringUtils.isBlank(password)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"加密房间必须设置密码");
        }
        //如果status为公开的不允许设置密码
        if (TeamStatusEnum.PUBLIC.equals(enumByValue) && StringUtils.isNotBlank(password)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"公开房间不允许设置密码");
        }
        Team newTeam=new Team();
        BeanUtils.copyProperties(teamUpdateRequest,newTeam);
        boolean result = this.updateById(newTeam);
        return result;
    }

    /**
     * 查询队伍列表
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    @Override
    //todo 测试有问题！！！！
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper=new QueryWrapper<>();
        if (teamQuery !=null){
            Long id = teamQuery.getId();
            if (id != null && id >0){
                queryWrapper.eq("id",id);
            }
            List<Long> idList = teamQuery.getIdList();
            if (CollectionUtils.isNotEmpty(idList)){
                queryWrapper.in("id",id);
            }
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)){
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)){
                queryWrapper.like("name",name);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)){
                queryWrapper.like("description",description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0){
                queryWrapper.eq("maxNum",maxNum);
            }
            Long userId = teamQuery.getUserId();
            if (userId != null && userId >0){
                queryWrapper.eq("userId",userId);
            }
            Integer status = teamQuery.getStatus();
            TeamStatusEnum enumByValue = TeamStatusEnum.getEnumByValue(status);
            if (enumByValue == null){
                enumByValue = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && enumByValue.equals(TeamStatusEnum.PRIVATE)){
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            queryWrapper.eq("status",enumByValue.getValue());
        }

        //不展示已过期的队伍
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)){
            return new ArrayList<>();
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        for (Team team : teamList){
            Long userId = team.getUserId();
            if (userId == null){
                continue;
            }
            User user = userService.getById(userId);
            UserVO userVO=new UserVO();
            BeanUtils.copyProperties(user,userVO);

            TeamUserVO teamUserVO=new TeamUserVO();
            BeanUtils.copyProperties(team,teamUserVO);
            teamUserVO.setCreateUser(userVO);
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
    if (teamJoinRequest == null){
        throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
    }
        //队伍必须存在，只能加入未满，未过期队伍
        Long teamId = teamJoinRequest.getTeamId();
        if (teamId == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"队伍不存在");
        }
        //todo 过期时间测试
        Team team=getTeamById(teamId);
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum enumByValue = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(enumByValue)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "不允许加入私有队伍");
        }
        String password = teamJoinRequest.getPassword();
        if (TeamStatusEnum.PUBLIC.equals(enumByValue) && StringUtils.isNotBlank(password)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "此队伍为公开无需密码");
        }
        if (TeamStatusEnum.SECRET.equals(enumByValue)){
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())){
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "密码错误");
            }
        }
        //todo 需要多数据测试（优化点：用户可以创建5个队伍，然后加入5个队伍？创建队伍只插入team表数据）
        //用户最多加入5个队伍
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        long userJoinTeamCount = userTeamService.count(queryWrapper);
        if (userJoinTeamCount > 5){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"用户只能加入5个队伍");
        }
        //不能重复加入已加入的队伍
        queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        queryWrapper.eq("teamId",teamId);
        long teamJoinCount = userTeamService.count(queryWrapper);
        if (teamJoinCount > 0){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"不能重复加入队伍");
        }
        //已经加入队伍的人数
        long joinTeamCount = this.countTeamUserByTeamId(teamId);
        if (joinTeamCount  > team.getMaxNum()){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"队伍已满");
        }
        //修改用户队伍信息
        UserTeam userTeam=new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        boolean result = userTeamService.save(userTeam);
        return result;
    }

    /**
     * 队长解散队伍
     * @param id
     * @param loginUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeTeam(long id, User loginUser) {
        //校验队伍是否存在
        Team team = getTeamById(id);
        Long teamId = team.getId();
        //校验你不是队伍的队长
        if (loginUser.getId() != team.getUserId()){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"仅队长可以解散队伍");
        }
        QueryWrapper<UserTeam> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        boolean result = userTeamService.remove(queryWrapper);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR,"删除队伍关联信息失败");
        return this.removeById(teamId);
    }

    /**
     * 退出队伍
     * @param teamquitRequest
     * @param loginUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamquitRequest, User loginUser) {
        if (teamquitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamquitRequest.getTeamId();
        Team team = getTeamById(teamId);
        Long userId = loginUser.getId();
        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setUserId(userId);
        queryUserTeam.setTeamId(teamId);
        QueryWrapper<UserTeam> queryWrapper =new QueryWrapper<>(queryUserTeam);
        long count = userTeamService.count();
        if (count == 0){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"未加入队伍");
        }
        long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
        //队伍只剩一人，解散
        if (teamHasJoinNum == 1){
            this.removeById(teamId);
        }else {
            //队伍还剩至少两人
            //是队长
            if (team.getUserId() == userId){
                //把队伍转移给最早加入的用户
                //查询已经加入队伍的所有用户和加入时间
                QueryWrapper<UserTeam> userTeamQueryWrapper=new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                //更新当前队伍的队长
                Team newTeam=new Team();
                newTeam.setId(teamId);
                newTeam.setUserId(nextTeamLeaderId);
                boolean result = this.updateById(newTeam);
                ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR,"更换队长失败");
            }
        }
        //移除关系
        return userTeamService.remove(queryWrapper);
    }

    /**
     * 根据 id 获取队伍信息
     *
     * @param teamId
     * @return
     */
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "队伍不存在");
        }
        return team;
    }

    /**
     * 获取某队伍当前人数
     *
     * @param teamId
     * @return
     */
    private long countTeamUserByTeamId(long teamId) {
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        return userTeamService.count(userTeamQueryWrapper);
    }
}



