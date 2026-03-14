package com.ykw.blog_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 标签DTO
 */
@Data
public class TagDTO {
    
    private Long id;
    
    @NotBlank(message = "标签名称不能为空")
    private String name;
    
    private Integer status;
}
