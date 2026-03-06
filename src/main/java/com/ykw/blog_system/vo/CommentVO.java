package com.ykw.blog_system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论VO
 */
@Data
public class CommentVO {
    
    private Long id;
    
    private Long articleId;
    
    private Long parentId;
    
    private Long userId;
    
    private String username;
    
    private String userAvatar;
    
    private String content;
    
    private String replyToUsername;
    
    private LocalDateTime createTime;
    
    private List<CommentVO> children;
}
