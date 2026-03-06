package com.ykw.blog_system.service;

import com.ykw.blog_system.dto.ArticleDTO;
import com.ykw.blog_system.entity.Article;
import com.ykw.blog_system.vo.ArticleVO;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;

import java.util.List;

/**
 * 文章服务接口
 */
public interface ArticleService {
    
    ResultVO<PageVO<Article>> getArticleList(Integer pageNum, Integer pageSize, 
                                              Long categoryId, String keyword);
    
    ResultVO<PageVO<Article>> getArticlesByTag(Long tagId, Integer pageNum, Integer pageSize);
    
    ResultVO<ArticleVO> getArticleDetail(Long articleId, Long currentUserId);
    
    ResultVO<Long> createArticle(ArticleDTO articleDTO, Long authorId);
    
    ResultVO<Void> updateArticle(ArticleDTO articleDTO, Long currentUserId);
    
    ResultVO<Void> deleteArticle(Long articleId, Long currentUserId);
    
    ResultVO<Void> likeArticle(Long articleId, Long userId);
    
    ResultVO<Void> unlikeArticle(Long articleId, Long userId);
    
    ResultVO<Void> collectArticle(Long articleId, Long userId);
    
    ResultVO<Void> uncollectArticle(Long articleId, Long userId);
    
    ResultVO<List<Article>> getHotArticles(Integer limit);
    
    ResultVO<List<Article>> getLatestArticles(Integer limit);
    
    ResultVO<List<Article>> getRecommendArticles(Long userId, Integer limit);
    
    ResultVO<PageVO<Article>> getMyArticles(Long userId, Integer pageNum, Integer pageSize, Integer status);
    
    ResultVO<PageVO<Article>> getMyCollects(Long userId, Integer pageNum, Integer pageSize);
}
