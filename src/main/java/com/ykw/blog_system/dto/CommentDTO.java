package com.ykw.blog_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 评论DTO
 */
@Data
public class CommentDTO {
    
    @NotNull(message = "文章ID不能为空")
    private Long articleId;
    
    private Long parentId;
    
    @NotBlank(message = "评论内容不能为空")
    private String content;
}
