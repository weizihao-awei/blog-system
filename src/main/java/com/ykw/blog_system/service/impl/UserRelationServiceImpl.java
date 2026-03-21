package com.ykw.blog_system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ykw.blog_system.dto.UserFootQueryDTO;
import com.ykw.blog_system.entity.User;
import com.ykw.blog_system.entity.UserRelation;
import com.ykw.blog_system.enums.FollowOperationEnum;
import com.ykw.blog_system.enums.FollowStateEnum;
import com.ykw.blog_system.enums.ResultCodeEnum;
import com.ykw.blog_system.mapper.UserMapper;
import com.ykw.blog_system.mapper.UserRelationMapper;
import com.ykw.blog_system.service.UserRelationService;
import com.ykw.blog_system.utils.SecurityUtil;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ykw.blog_system.enums.FollowStateEnum.*;

@Service
public class UserRelationServiceImpl implements UserRelationService {
    
    @Autowired
    private UserRelationMapper userRelationMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    @Transactional
    /**
     * 关注或取消关注用户
     *
     * @param targetUserId 目标用户 ID
     * @param operation    操作类型
     * @return 响应结果
     */
    public ResultVO<Void> followOrUnfollow(Long targetUserId, FollowOperationEnum operation) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        // 校验目标用户 ID 是否为空
        if (targetUserId == null) {
            return ResultVO.error(ResultCodeEnum.PARAM_ERROR);
        }
        
        // 校验是否关注自己
        if (currentUserId != null && currentUserId.equals(targetUserId)) {
            return ResultVO.error(ResultCodeEnum.CANNOT_FOLLOW_SELF);
        }
        // 查询当前用户与目标用户之间的关注关系
        LambdaQueryWrapper<UserRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRelation::getUserId, targetUserId)
                .eq(UserRelation::getFollowUserId, currentUserId);
        UserRelation existingRelation = userRelationMapper.selectOne(wrapper);

        // 如果关系记录不存在，则创建一条新的未关注状态记录
        if (existingRelation == null) {
            existingRelation = new UserRelation();
            existingRelation.setUserId(targetUserId);
            existingRelation.setFollowUserId(currentUserId);
            existingRelation.setFollowState(FollowStateEnum.UNFOLLOWED.getCode());
        }

        //switch判断是关注还是取消关注
        switch (operation){
            case FOLLOW:
                if (Objects.equals(existingRelation.getFollowState(), FOLLOWED.getCode()))
                    //已关注就返回
                    return ResultVO.error(ResultCodeEnum.ALREADY_FOLLOWED);
                existingRelation.setFollowState(FollowStateEnum.FOLLOWED.getCode());
                break;

            case UNFOLLOW:
                //没办法取消已经取消关注或跟本没有关注的
                if (!Objects.equals(existingRelation.getFollowState(), FOLLOWED.getCode())) {
                    return ResultVO.error(ResultCodeEnum.NOT_FOLLOWED_YET);
                }
                existingRelation.setFollowState(CANCELLED.getCode());
                break;
        }


        userRelationMapper.updateById(existingRelation);
        return ResultVO.success(ResultCodeEnum.SUCCESS);


    }
    
    @Override
    public ResultVO<PageVO<User>> getFollowersList(UserFootQueryDTO queryDTO) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        Page<UserRelation> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<UserRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRelation::getUserId, currentUserId)
                .eq(UserRelation::getFollowState, 1)
                .orderByDesc(UserRelation::getCreateTime);
        Page<UserRelation> relationPage = userRelationMapper.selectPage(page, wrapper);
        
        List<Long> followerIds = relationPage.getRecords().stream()
                .map(UserRelation::getFollowUserId)
                .collect(Collectors.toList());
        
        List<User> followerList = new ArrayList<>();
        if (!followerIds.isEmpty()) {
            followerList = userMapper.selectBatchIds(followerIds);
            followerList.forEach(user -> user.setPassword(null));
        }
        
        PageVO<User> pageVO = new PageVO<>(followerList, relationPage.getTotal(), 
                queryDTO.getPageNum(), queryDTO.getPageSize());
        return ResultVO.success(ResultCodeEnum.SUCCESS, pageVO);
    }
    
    @Override
    public ResultVO<PageVO<User>> getFollowingList(UserFootQueryDTO queryDTO) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        Page<UserRelation> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<UserRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRelation::getFollowUserId, currentUserId)
                .eq(UserRelation::getFollowState, 1)
                .orderByDesc(UserRelation::getCreateTime);
        Page<UserRelation> relationPage = userRelationMapper.selectPage(page, wrapper);
        
        List<Long> followingIds = relationPage.getRecords().stream()
                .map(UserRelation::getUserId)
                .collect(Collectors.toList());
        
        List<User> followingList = new ArrayList<>();
        if (!followingIds.isEmpty()) {
            followingList = userMapper.selectBatchIds(followingIds);
            followingList.forEach(user -> user.setPassword(null));
        }
        
        PageVO<User> pageVO = new PageVO<>(followingList, relationPage.getTotal(), 
                queryDTO.getPageNum(), queryDTO.getPageSize());
        return ResultVO.success(ResultCodeEnum.SUCCESS, pageVO);
    }
    
    @Override
    public ResultVO<Long> getFollowersCount() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        LambdaQueryWrapper<UserRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRelation::getUserId, currentUserId)
                .eq(UserRelation::getFollowState, 1);
        Long count = userRelationMapper.selectCount(wrapper);
        return ResultVO.success(ResultCodeEnum.SUCCESS, count);
    }
    
    @Override
    public ResultVO<Long> getFollowingCount() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        LambdaQueryWrapper<UserRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRelation::getFollowUserId, currentUserId)
                .eq(UserRelation::getFollowState, 1);
        Long count = userRelationMapper.selectCount(wrapper);
        return ResultVO.success(ResultCodeEnum.SUCCESS, count);
    }
}
