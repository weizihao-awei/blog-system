package com.ykw.blog_system.controller;

import com.ykw.blog_system.entity.User;
import com.ykw.blog_system.service.UserService;
import com.ykw.blog_system.utils.SecurityUtil;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public ResultVO<User> getCurrentUserInfo() {
        Long userId = SecurityUtil.getCurrentUserId();
        return userService.getCurrentUserInfo(userId);
    }
    
    /**
     * 更新当前用户信息
     */
    @PutMapping("/info")
    public ResultVO<Void> updateUserInfo(@RequestBody User user) {
        user.setId(SecurityUtil.getCurrentUserId());
        return userService.updateUserInfo(user);
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
