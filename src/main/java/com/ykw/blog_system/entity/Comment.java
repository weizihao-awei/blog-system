package com.ykw.blog_system.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论实体类
 */
@Data
public class Comment {
    
    /** 评论ID */
    private Long id;
    
    /** 关联文章ID */
    private Long articleId;
    
    /** 父评论ID，用于构建评论树 */
    private Long parentId;
    
    /** 用户ID */
    private Long userId;
    
    /** 评论内容 */
    private String content;
    
    /** 评论状态（如：0-待审核，1-已发布，2-已删除） */
    private Integer status;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    

}
