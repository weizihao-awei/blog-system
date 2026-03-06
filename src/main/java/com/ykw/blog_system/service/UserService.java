package com.ykw.blog_system.service;

import com.ykw.blog_system.entity.User;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    
    ResultVO<User> getCurrentUserInfo(Long userId);
    
    ResultVO<Void> updateUserInfo(User user);
    
    ResultVO<Void> updatePassword(Long userId, String oldPassword, String newPassword);
    
    ResultVO<PageVO<User>> getUserList(Integer pageNum, Integer pageSize, String keyword);
    
    ResultVO<Void> updateUserStatus(Long userId, Integer status);
    
    ResultVO<Void> deleteUser(Long userId);
}
