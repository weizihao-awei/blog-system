package com.ykw.blog_system.utils;

import com.ykw.blog_system.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 */
public class SecurityUtil {
    
    /**
     * 获取当前登录用户
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
    
    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }
    
    /**
     * 判断是否已登录
     */
    public static boolean isAuthenticated() {
        return getCurrentUser() != null;
    }
    
    /**
     * 判断当前用户是否为管理员
     */
    public static boolean isAdmin() {
        User user = getCurrentUser();
        return user != null && user.getRole() != null && user.getRole() == 1;
    }
}
