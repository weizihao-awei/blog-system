package com.ykw.blog_system.service;

import com.ykw.blog_system.dto.UserFootQueryDTO;
import com.ykw.blog_system.entity.User;
import com.ykw.blog_system.entity.UserFoot;
import com.ykw.blog_system.vo.ArticleVO;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    
    ResultVO<User> getCurrentUserInfo(Long userId);
    
    ResultVO<Void> updateUserInfo(User user);
    
    ResultVO<PageVO<User>> getUserList(Integer pageNum, Integer pageSize, String keyword);
    
    ResultVO<Void> updateUserStatus(Long userId, Integer status);
    
    ResultVO<Void> deleteUser(Long userId);
    
    /**
     * 分页查询用户收藏的文章
     */
    ResultVO<PageVO<ArticleVO>> getCollectionArticlesPage(Long userId, UserFootQueryDTO queryDTO);
    
    /**
     * 分页查询用户点赞的文章
     */
    ResultVO<PageVO<ArticleVO>> getPraiseArticlesPage(Long userId, UserFootQueryDTO queryDTO);
    
    /**
     * 分页查询用户浏览的文章
     */
    ResultVO<PageVO<ArticleVO>> getReadArticlesPage(Long userId, UserFootQueryDTO queryDTO);
    
    /**
     * 分页查询用户发布的文章
     */
    ResultVO<PageVO<ArticleVO>> getUserPublishedArticlesPage(Long userId, UserFootQueryDTO queryDTO);
}
