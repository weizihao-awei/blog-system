package com.ykw.blog_system.dto;

import com.ykw.blog_system.enums.ArticleOrderEnum;
import lombok.Data;

import java.util.List;

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
    private List<Long> tagIds;
    
    /**
     * 关键字搜索（可选）
     */
    private String keyword;
    
    /**
     * 排序方式（可选）
     * 默认：create_time_desc（创建时间降序，最新发布）
     * 支持：create_time_asc, create_time_desc, update_time_asc, update_time_desc
     */
    private ArticleOrderEnum orderBy = ArticleOrderEnum.CREATE_TIME_DESC;
}
