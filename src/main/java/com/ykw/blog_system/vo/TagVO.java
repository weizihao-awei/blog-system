package com.ykw.blog_system.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签VO
 */
@Data
public class TagVO {
    
    private Long id;
    
    private String name;
    
    private Integer articleCount;
    
    private LocalDateTime createTime;

}
