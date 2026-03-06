package com.ykw.blog_system.mapper;

import com.ykw.blog_system.entity.ArticleCollect;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章收藏Mapper接口
 */
@Mapper
public interface ArticleCollectMapper {
    
    int insert(ArticleCollect articleCollect);
    
    int deleteById(Long id);
    
    int deleteByUserAndArticle(@Param("userId") Long userId, @Param("articleId") Long articleId);
    
    ArticleCollect selectByUserAndArticle(@Param("userId") Long userId, @Param("articleId") Long articleId);
    
    List<ArticleCollect> selectByUserId(Long userId);
    
    List<ArticleCollect> selectByArticleId(Long articleId);
    
    Long countByArticleId(Long articleId);
}
