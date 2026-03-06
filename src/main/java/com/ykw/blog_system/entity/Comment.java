package com.ykw.blog_system.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论实体类
 */
@Data
public class Comment {
    
    private Long id;
    
    private Long articleId;
    
    private Long parentId;
    
    private Long userId;
    
    private String content;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    // 非数据库字段
    private String username;
    
    private String userAvatar;
    
    private String replyToUsername;
    
    private List<Comment> children;
}
