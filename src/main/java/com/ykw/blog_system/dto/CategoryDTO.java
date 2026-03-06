package com.ykw.blog_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 分类DTO
 */
@Data
public class CategoryDTO {
    
    private Long id;
    
    @NotBlank(message = "分类名称不能为空")
    private String name;
    
    private String description;
    
    private Integer sortOrder;
    
    private Integer status;
}
