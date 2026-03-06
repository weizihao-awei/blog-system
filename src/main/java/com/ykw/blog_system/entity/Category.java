package com.ykw.blog_system.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类实体类
 */
@Data
public class Category {
    
    private Long id;
    
    private String name;
    
    private String description;
    
    private Integer sortOrder;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
