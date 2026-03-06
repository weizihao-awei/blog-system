package com.ykw.blog_system.controller;

import com.ykw.blog_system.service.StatisticsService;
import com.ykw.blog_system.vo.ResultVO;
import com.ykw.blog_system.vo.StatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计控制器
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    /**
     * 获取统计数据
     */
    @GetMapping
    public ResultVO<StatisticsVO> getStatistics() {
        return statisticsService.getStatistics();
    }
}
