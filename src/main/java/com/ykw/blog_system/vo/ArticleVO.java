package com.ykw.blog_system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章详情VO
 */
@Data
public class ArticleVO {
    
    private Long id;
    
    private String title;
    
    private String summary;
    
    private String content;
    
    private String htmlContent;
    
    private String coverImage;
    
    private Long authorId;
    
    private String authorName;
    
    private String authorAvatar;
    
    private Long categoryId;
    
    private String categoryName;
    
    private Integer viewCount;
    
    private Integer likeCount;
    
    private Integer commentCount;
    
    private Integer status;
    
    private Integer isTop;
    
    private Integer isRecommend;
    
    private LocalDateTime publishTime;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private List<TagVO> tags;
    
    private Boolean isLiked;
    
    private Boolean isCollected;
}
