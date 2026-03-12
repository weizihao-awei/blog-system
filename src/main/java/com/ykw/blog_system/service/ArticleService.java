package com.ykw.blog_system.service;

import com.ykw.blog_system.dto.ArticleDTO;
import com.ykw.blog_system.dto.ArticleQueryDTO;
import com.ykw.blog_system.entity.Article;
import com.ykw.blog_system.vo.ArticleVO;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;

import java.util.List;

/**
 * 文章服务接口
 */
public interface ArticleService {
    
    /**
     * 通用文章查询接口
     */
    ResultVO<PageVO<ArticleVO>> queryArticles(ArticleQueryDTO queryDTO);
    
    ResultVO<ArticleVO> getArticleDetail(Long articleId, Long currentUserId);
    
    ResultVO<Long> createArticle(ArticleDTO articleDTO, Long authorId);
    
    ResultVO<Void> updateArticle(ArticleDTO articleDTO, Long currentUserId);
    
    ResultVO<Void> deleteArticle(Long articleId, Long currentUserId);
    
    ResultVO<Void> likeArticle(Long articleId, Long userId);
    
    ResultVO<Void> unlikeArticle(Long articleId, Long userId);
    
    ResultVO<Void> collectArticle(Long articleId, Long userId);
    
    ResultVO<Void> uncollectArticle(Long articleId, Long userId);
    
    ResultVO<PageVO<ArticleVO>> getHotArticles(ArticleQueryDTO queryDTO);
    
    ResultVO<PageVO<ArticleVO>> getLatestArticles(ArticleQueryDTO queryDTO);
    
    ResultVO<PageVO<ArticleVO>> getRecommendArticles(Long userId, ArticleQueryDTO queryDTO);
    
    ResultVO<PageVO<ArticleVO>> getMyArticles(Long userId, Integer pageNum, Integer pageSize, Integer status);
    
    ResultVO<PageVO<ArticleVO>> getMyCollects(Long userId, Integer pageNum, Integer pageSize);
}
