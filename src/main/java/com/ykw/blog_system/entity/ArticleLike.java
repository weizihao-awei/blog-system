package com.ykw.blog_system.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章点赞实体类
 */
@Data
public class ArticleLike {
    
    private Long id;
    
    private Long userId;
    
    private Long articleId;
    
    private LocalDateTime createTime;
}
