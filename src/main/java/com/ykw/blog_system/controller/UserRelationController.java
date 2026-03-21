package com.ykw.blog_system.controller;

import com.ykw.blog_system.dto.FollowDTO;
import com.ykw.blog_system.dto.UserFootQueryDTO;
import com.ykw.blog_system.entity.User;
import com.ykw.blog_system.service.UserRelationService;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/relation")
public class UserRelationController {
    
    @Autowired
    private UserRelationService userRelationService;
    
    /**
     * 关注或取消关注用户
     * @param followDTO 包含目标用户ID和操作类型的DTO
     * @return 操作结果
     */
    @PostMapping("/follow")
    public ResultVO<Void> followOrUnfollow(@RequestBody FollowDTO followDTO) {
        return userRelationService.followOrUnfollow(followDTO.getTargetUserId(), followDTO.getOperation());
    }
    
    /**
     * 获取粉丝列表
     * @param queryDTO 查询参数（页码、页数等）
     * @return 粉丝用户分页数据
     */
    @PostMapping("/followers")
    public ResultVO<PageVO<User>> getFollowersList(@RequestBody UserFootQueryDTO queryDTO) {
        return userRelationService.getFollowersList(queryDTO);
    }
    
    /**
     * 获取关注列表
     * @param queryDTO 查询参数（页码、页数等）
     * @return 关注用户分页数据
     */
    @PostMapping("/following")
    public ResultVO<PageVO<User>> getFollowingList(@RequestBody UserFootQueryDTO queryDTO) {
        return userRelationService.getFollowingList(queryDTO);
    }
    
    /**
     * 获取粉丝数量
     * @param userId 用户ID
     * @return 粉丝总数
     */
    @GetMapping("/followers/count")
    public ResultVO<Long> getFollowersCount(@RequestParam Long userId) {
        return userRelationService.getFollowersCount(userId);
    }
    
    /**
     * 获取关注数量
     * @param userId 用户ID
     * @return 关注总数
     */
    @GetMapping("/following/count")
    public ResultVO<Long> getFollowingCount(@RequestParam Long userId) {
        return userRelationService.getFollowingCount(userId);
    }
}
