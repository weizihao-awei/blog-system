package com.ykw.blog_system.vo;

import lombok.Data;

/**
 * 登录响应VO
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
}
