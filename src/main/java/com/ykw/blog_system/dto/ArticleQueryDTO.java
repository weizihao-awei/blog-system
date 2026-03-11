package com.ykw.blog_system.dto;

import lombok.Data;

/**
 * 文章查询 DTO
 */
@Data
public class ArticleQueryDTO {
    
    /**
     * 页码（默认 1）
     */
    private Integer pageNum = 1;
    
    /**
     * 每页数量（默认 10）
     */
    private Integer pageSize = 10;
    
    /**
     * 分类 ID（可选）
     */
    private Long categoryId;
    
    /**
     * 标签 ID（可选）
     */
    private Long tagId;
    
    /**
     * 关键字搜索（可选）
     */
    private String keyword;
    
    /**
     * 排序方式（可选）
     * latest: 最新，hot: 热门，recommend: 推荐
     */
    private String orderBy = "latest";
}
