package com.ykw.blog_system.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户视图对象（VO）
 * 用于展示层，包含用户相关展示数据
 */
@Data
public class UserVO {
    
    /**
     * 用户 ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像地址
     */
    private String avatar;
    
    /**
     * 邮箱地址
     */
    private String email;
    
    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;
    
    /**
     * 自我介绍/个人简介
     */
    private String intro;
    
    /**
     * 个性签名
     */
    private String signature;
    
    /**
     * 用户角色
     */
    private Integer role;
    
    /**
     * 用户状态
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
