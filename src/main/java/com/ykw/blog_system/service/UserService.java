package com.ykw.blog_system.service;

import com.ykw.blog_system.dto.UserFootQueryDTO;
import com.ykw.blog_system.entity.User;
import com.ykw.blog_system.vo.ArticleVO;
import com.ykw.blog_system.vo.AuthorInfoVO;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;
import com.ykw.blog_system.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 获取当前用户信息
     */
    ResultVO<UserVO> getCurrentUserInfo(Long userId);
    
    /**
     * 更新用户信息
     */
    ResultVO<Void> updateUserInfo(User user);
    
    /**
     * 分页查询用户列表
     */
    ResultVO<PageVO<User>> getUserList(Integer pageNum, Integer pageSize, String keyword);
    
    /**
     * 更新用户状态
     */
    ResultVO<Void> updateUserStatus(Long userId, Integer status);
    
    /**
     * 删除用户
     */
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
    ResultVO<PageVO<ArticleVO>> getUserPublishedArticlesPage( UserFootQueryDTO queryDTO);
    
    /**
     * 获取作者信息（包含统计数据）
     */
    ResultVO<AuthorInfoVO> getAuthorInfo(Long userId);
}
