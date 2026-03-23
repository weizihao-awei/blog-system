package com.ykw.blog_system.controller;

import com.ykw.blog_system.dto.AuthorInfoQueryDTO;
import com.ykw.blog_system.dto.UserFootQueryDTO;
import com.ykw.blog_system.entity.*;
import com.ykw.blog_system.mapper.*;
import com.ykw.blog_system.service.UserService;
import com.ykw.blog_system.utils.SecurityUtil;
import com.ykw.blog_system.vo.ArticleVO;
import com.ykw.blog_system.vo.AuthorInfoVO;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;
import com.ykw.blog_system.vo.TagVO;
import com.ykw.blog_system.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;

    /**
     * 获取用户信息
     * @param userId 用户 ID（可选，不传则查询当前登录用户）
     * @return 用户信息
     */
    @GetMapping("/info")
    public ResultVO<UserVO> getUserInfo(@RequestParam(required = false) Long userId) {
        // 如果没有传入 userId，则使用当前登录用户的 ID
        if (userId == null) {
            userId = SecurityUtil.getCurrentUserId();
        }
        return userService.getUserInfo(userId);
    }

    /**
     * 获取作者信息
     * @param queryDTO 查询参数
     * @return 作者信息
     */
    @PostMapping("/author/info")
    public ResultVO<AuthorInfoVO> getAuthorInfo(@RequestBody AuthorInfoQueryDTO queryDTO) {
        return userService.getAuthorInfo(queryDTO.getUserId());
    }
    
    /**
     * 更新当前用户信息
     */
    @PutMapping("/info")
    public ResultVO<Void> updateUserInfo(@RequestBody User user) {
        user.setId(SecurityUtil.getCurrentUserId());
        return userService.updateUserInfo(user);
    }
    
    /**
     * 查询用户收藏的文章
     */
    @PostMapping("/foot/collection")
    public ResultVO<PageVO<ArticleVO>> getUserCollection(@RequestBody UserFootQueryDTO queryDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        return userService.getCollectionArticlesPage(userId, queryDTO);
    }
    
    /**
     * 查询用户点赞的文章
     */
    @PostMapping("/foot/praise")
    public ResultVO<PageVO<ArticleVO>> getUserPraise(@RequestBody UserFootQueryDTO queryDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        return userService.getPraiseArticlesPage(userId, queryDTO);
    }
    
    /**
     * 查询用户浏览的文章
     */
    @PostMapping("/foot/read")
    public ResultVO<PageVO<ArticleVO>> getUserRead(@RequestBody UserFootQueryDTO queryDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        return userService.getReadArticlesPage(userId, queryDTO);
    }
    
    /**
     * 查询用户发布的文章
     */
    @PostMapping("/articles/published")
    public ResultVO<PageVO<ArticleVO>> getUserPublishedArticles(@RequestBody UserFootQueryDTO queryDTO) {

        return userService.getUserPublishedArticlesPage(queryDTO);
    }



//    /**
//     * 获取用户列表（管理员）
//     */
//    @GetMapping("/list")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResultVO<PageVO<User>> getUserList(
//            @RequestParam(defaultValue = "1") Integer pageNum,
//            @RequestParam(defaultValue = "10") Integer pageSize,
//            @RequestParam(required = false) String keyword) {
//        return userService.getUserList(pageNum, pageSize, keyword);
//    }
//
//    /**
//     * 更新用户状态（管理员）
//     */
//    @PutMapping("/{userId}/status/{status}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResultVO<Void> updateUserStatus(@PathVariable Long userId, @PathVariable Integer status) {
//        return userService.updateUserStatus(userId, status);
//    }
//
//    /**
//     * 删除用户（管理员）
//     */
//    @DeleteMapping("/{userId}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResultVO<Void> deleteUser(@PathVariable Long userId) {
//        return userService.deleteUser(userId);
//    }
}
