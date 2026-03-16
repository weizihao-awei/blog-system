package com.ykw.blog_system.vo;

import lombok.Data;

/**
 * 登录响应 VO
 */
@Data
public class LoginVO {
    
    private String token;
    
    private String tokenType;
    
    private Long userId;
    
    private String username;
    
    private String nickname;
    
    private String avatar;
    
    private Integer role;
    
    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;
    
    /**
     * 用户自我介绍/个人简介
     */
    private String intro;
    
    /**
     * 个性签名
     */
    private String signature;
    
    private String email;
    
}
