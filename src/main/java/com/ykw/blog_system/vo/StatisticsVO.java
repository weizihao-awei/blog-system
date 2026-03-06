package com.ykw.blog_system.vo;

import lombok.Data;

/**
 * 统计数据VO
 */
@Data
public class StatisticsVO {
    
    private Long userCount;
    
    private Long articleCount;
    
    private Long viewCount;
    
    private Long commentCount;
    
    private Long todayViewCount;
    
    private Long weekArticleCount;
}
