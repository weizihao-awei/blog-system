package com.ykw.blog_system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ykw.blog_system.entity.Comment;
import com.ykw.blog_system.mapper.*;
import com.ykw.blog_system.service.StatisticsService;
import com.ykw.blog_system.vo.ResultVO;
import com.ykw.blog_system.vo.StatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ArticleMapper articleMapper;
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Override
    public ResultVO<StatisticsVO> getStatistics() {
        StatisticsVO statisticsVO = new StatisticsVO();
        
        statisticsVO.setUserCount(userMapper.countUsers());
        statisticsVO.setArticleCount(articleMapper.countArticles(1));
        statisticsVO.setViewCount(articleMapper.sumViewCount());
        statisticsVO.setCommentCount(commentMapper.selectCount(new LambdaQueryWrapper<Comment>().eq(Comment::getStatus, 1)));
        statisticsVO.setTodayViewCount(0L); // 简化处理
        statisticsVO.setWeekArticleCount(0L); // 简化处理
        return ResultVO.success(statisticsVO);
    }
}
