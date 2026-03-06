package com.ykw.blog_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
public class User {
    
    private Long id;
    
    private String username;
    
    @JsonIgnore
    private String password;
    
    private String nickname;
    
    private String email;
    
    private String avatar;
    
    private Integer role;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
