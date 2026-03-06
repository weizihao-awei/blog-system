package com.ykw.blog_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 文章DTO
 */
@Data
public class ArticleDTO {
    
    private Long id;
    
    @NotBlank(message = "文章标题不能为空")
    private String title;
    
    private String summary;
    
    @NotBlank(message = "文章内容不能为空")
    private String content;
    
    private String htmlContent;
    
    private String coverImage;
    
    private Long categoryId;
    
    private List<Long> tagIds;
    
    private Integer status;
    
    private Integer isTop;
    
    private Integer isRecommend;
}
