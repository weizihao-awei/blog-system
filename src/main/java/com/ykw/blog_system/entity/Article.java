package com.ykw.blog_system.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章实体类
 */
@Data
public class Article {
    
    private Long id;
    
    private String title;
    
    private String summary;
    
    private String content;
    
    private String htmlContent;
    
    private String coverImage;
    
    private Long authorId;
    
    private Long categoryId;
    
    private Integer viewCount;
    
    private Integer likeCount;
    
    private Integer commentCount;
    
    private Integer status;
    
    private Integer isTop;
    
    private Integer isRecommend;
    
    private LocalDateTime publishTime;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    // 非数据库字段
    private String authorName;
    
    private String authorAvatar;
    
    private String categoryName;
    
    private List<Tag> tags;
    
    // 当前用户是否已点赞
    private Boolean isLiked;
    
    // 当前用户是否已收藏
    private Boolean isCollected;
    
    // 推荐得分（用于推荐算法）
    private Double recommendScore;
}
