package com.ykw.blog_system.controller;

import com.ykw.blog_system.dto.ArticleDTO;
import com.ykw.blog_system.entity.Article;
import com.ykw.blog_system.service.ArticleService;
import com.ykw.blog_system.utils.SecurityUtil;
import com.ykw.blog_system.vo.ArticleVO;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章控制器
 */
@RestController
@RequestMapping("/api/article")
public class ArticleController {
    
    @Autowired
    private ArticleService articleService;
    
    /**
     * 获取文章列表
     */
    @GetMapping("/list")
    public ResultVO<PageVO<Article>> getArticleList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        return articleService.getArticleList(pageNum, pageSize, categoryId, keyword);
    }
    
    /**
     * 根据标签获取文章列表
     */
    @GetMapping("/tag/{tagId}")
    public ResultVO<PageVO<Article>> getArticlesByTag(
            @PathVariable Long tagId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return articleService.getArticlesByTag(tagId, pageNum, pageSize);
    }
    
    /**
     * 获取文章详情
     */
    @GetMapping("/detail/{articleId}")
    public ResultVO<ArticleVO> getArticleDetail(@PathVariable Long articleId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return articleService.getArticleDetail(articleId, currentUserId);
    }
    
    /**
     * 创建文章
     */
    @PostMapping("/create")
    public ResultVO<Long> createArticle(@Valid @RequestBody ArticleDTO articleDTO) {
        Long authorId = SecurityUtil.getCurrentUserId();
        return articleService.createArticle(articleDTO, authorId);
    }
    
    /**
     * 更新文章
     */
    @PutMapping("/update")
    public ResultVO<Void> updateArticle(@Valid @RequestBody ArticleDTO articleDTO) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return articleService.updateArticle(articleDTO, currentUserId);
    }
    
    /**
     * 删除文章
     */
    @DeleteMapping("/delete/{articleId}")
    public ResultVO<Void> deleteArticle(@PathVariable Long articleId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return articleService.deleteArticle(articleId, currentUserId);
    }
    
    /**
     * 点赞文章
     */
    @PostMapping("/like/{articleId}")
    public ResultVO<Void> likeArticle(@PathVariable Long articleId) {
        Long userId = SecurityUtil.getCurrentUserId();
        return articleService.likeArticle(articleId, userId);
    }
    
    /**
     * 取消点赞
     */
    @DeleteMapping("/like/{articleId}")
    public ResultVO<Void> unlikeArticle(@PathVariable Long articleId) {
        Long userId = SecurityUtil.getCurrentUserId();
        return articleService.unlikeArticle(articleId, userId);
    }
    
    /**
     * 收藏文章
     */
    @PostMapping("/collect/{articleId}")
    public ResultVO<Void> collectArticle(@PathVariable Long articleId) {
        Long userId = SecurityUtil.getCurrentUserId();
        return articleService.collectArticle(articleId, userId);
    }
    
    /**
     * 取消收藏
     */
    @DeleteMapping("/collect/{articleId}")
    public ResultVO<Void> uncollectArticle(@PathVariable Long articleId) {
        Long userId = SecurityUtil.getCurrentUserId();
        return articleService.uncollectArticle(articleId, userId);
    }
    
    /**
     * 获取热门文章
     */
    @GetMapping("/hot")
    public ResultVO<List<Article>> getHotArticles(
            @RequestParam(defaultValue = "10") Integer limit) {
        return articleService.getHotArticles(limit);
    }
    
    /**
     * 获取最新文章
     */
    @GetMapping("/latest")
    public ResultVO<List<Article>> getLatestArticles(
            @RequestParam(defaultValue = "10") Integer limit) {
        return articleService.getLatestArticles(limit);
    }
    
    /**
     * 获取推荐文章
     */
    @GetMapping("/recommend")
    public ResultVO<List<Article>> getRecommendArticles(
            @RequestParam(defaultValue = "10") Integer limit) {
        Long userId = SecurityUtil.getCurrentUserId();
        return articleService.getRecommendArticles(userId, limit);
    }
    
    /**
     * 获取我的文章列表
     */
    @GetMapping("/my")
    public ResultVO<PageVO<Article>> getMyArticles(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        Long userId = SecurityUtil.getCurrentUserId();
        return articleService.getMyArticles(userId, pageNum, pageSize, status);
    }
    
    /**
     * 获取我的收藏列表
     */
    @GetMapping("/my-collects")
    public ResultVO<PageVO<Article>> getMyCollects(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = SecurityUtil.getCurrentUserId();
        return articleService.getMyCollects(userId, pageNum, pageSize);
    }
}
