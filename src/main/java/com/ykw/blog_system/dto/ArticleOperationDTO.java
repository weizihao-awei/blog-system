package com.ykw.blog_system.dto;

import com.ykw.blog_system.enums.ArticleOperationType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ArticleOperationDTO {

    @NotNull(message = "文章ID不能为空")
    private Long articleId;

    @NotNull(message = "操作类型不能为空")
    private ArticleOperationType operationType;
}
