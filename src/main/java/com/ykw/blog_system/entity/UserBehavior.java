package com.ykw.blog_system.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户行为记录实体类（用于推荐算法）
 */
@Data
public class UserBehavior {
    
    private Long id;
    
    private Long userId;
    
    private Long articleId;
    
    private String behaviorType;
    
    private BigDecimal behaviorWeight;
    
    private LocalDateTime createTime;
}
