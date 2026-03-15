package com.ykw.blog_system.controller;

import com.ykw.blog_system.dto.ArticleDTO;
import com.ykw.blog_system.dto.ArticleOperationDTO;
import com.ykw.blog_system.dto.ArticleQueryDTO;

import com.ykw.blog_system.service.ArticleService;
import com.ykw.blog_system.utils.SecurityUtil;
import com.ykw.blog_system.vo.ArticleVO;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 文章控制器
 */
@RestController
@RequestMapping("/api/article")
public class ArticleController {
    
    @Autowired
    private ArticleService articleService;

    /**
     * 获取最新文章
     */
    @PostMapping("/latest")
    public ResultVO<PageVO<ArticleVO>> getLatestArticles(@RequestBody(required = false) ArticleQueryDTO queryDTO) {
        if (queryDTO == null) {
            queryDTO = new ArticleQueryDTO();
        }
        return articleService.getLatestArticles(queryDTO);
    }
    
    /**
     * 通用文章查询接口（POST）
     * 支持：分类、标签、关键字搜索、排序
     */
    @PostMapping("/query")
    public ResultVO<PageVO<ArticleVO>> queryArticles(@RequestBody(required = false) ArticleQueryDTO queryDTO) {
        // 如果 queryDTO 为 null，创建默认对象
        if (queryDTO == null) {
            queryDTO = new ArticleQueryDTO();
        }
        return articleService.queryArticles(queryDTO);
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
     * 统一操作接口（点赞、取消点赞、收藏、取消收藏）
     */
    @PostMapping("/operate")
    public ResultVO<Void> operateArticle(@Valid @RequestBody ArticleOperationDTO operationDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        return articleService.operateArticle(operationDTO, userId);
    }
    
    /**
     * 获取热门文章
     */
    @PostMapping("/hot")
    public ResultVO<PageVO<ArticleVO>> getHotArticles(@RequestBody(required = false) ArticleQueryDTO queryDTO) {
        if (queryDTO == null) {
            queryDTO = new ArticleQueryDTO();
        }
        return articleService.getHotArticles(queryDTO);
    }
    

    
    /**
     * 获取推荐文章
     */
    @PostMapping("/recommend")
    public ResultVO<PageVO<ArticleVO>> getRecommendArticles(@RequestBody(required = false) ArticleQueryDTO queryDTO) {
        if (queryDTO == null) {
            queryDTO = new ArticleQueryDTO();
        }
        Long userId = SecurityUtil.getCurrentUserId();
        return articleService.getRecommendArticles(userId, queryDTO);
    }
    
    /**
     * 获取我的文章列表
     */
    @GetMapping("/my")
    public ResultVO<PageVO<ArticleVO>> getMyArticles(
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
    public ResultVO<PageVO<ArticleVO>> getMyCollects(

            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = SecurityUtil.getCurrentUserId();
        return articleService.getMyCollects(userId, pageNum, pageSize);
    }
}
