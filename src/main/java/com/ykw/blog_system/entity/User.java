package com.ykw.blog_system.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
public class User {
    @TableId
    private Long id;
    
    private String username;
    
    @JsonIgnore
    private String password;
    
    private String nickname;
    
    private String email;
    
    private String avatar;
    
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
    
    private Integer role;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
