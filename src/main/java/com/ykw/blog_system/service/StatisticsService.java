package com.ykw.blog_system.service;

import com.ykw.blog_system.vo.ResultVO;
import com.ykw.blog_system.vo.StatisticsVO;

/**
 * 统计服务接口
 */
public interface StatisticsService {
    
    ResultVO<StatisticsVO> getStatistics();
}
