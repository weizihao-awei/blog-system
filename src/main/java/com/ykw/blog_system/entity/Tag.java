package com.ykw.blog_system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签实体类
 */
@Data
@TableName("tag")
public class Tag {
    
    private Long id;
    
    private String name;
    
    private Integer status;
    
    private LocalDateTime createTime;

    // 核心：标记该字段不是数据库表字段，MP 会忽略它
    @TableField(exist = false)
    private Integer articleCount; // 仅用于自定义查询的临时字段
    

}
