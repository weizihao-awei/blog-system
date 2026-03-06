package com.ykw.blog_system.entity;

import lombok.Data;

/**
 * 文章标签关联实体类
 */
@Data
public class ArticleTag {
    
    private Long id;
    
    private Long articleId;
    
    private Long tagId;
}
