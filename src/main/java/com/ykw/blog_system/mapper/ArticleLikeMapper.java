package com.ykw.blog_system.mapper;

import com.ykw.blog_system.entity.ArticleLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章点赞Mapper接口
 */
@Mapper
public interface ArticleLikeMapper {
    
    int insert(ArticleLike articleLike);
    
    int deleteById(Long id);
    
    int deleteByUserAndArticle(@Param("userId") Long userId, @Param("articleId") Long articleId);
    
    ArticleLike selectByUserAndArticle(@Param("userId") Long userId, @Param("articleId") Long articleId);
    
    List<ArticleLike> selectByUserId(Long userId);
    
    List<ArticleLike> selectByArticleId(Long articleId);
    
    Long countByArticleId(Long articleId);
}
