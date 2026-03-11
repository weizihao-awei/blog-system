package com.ykw.blog_system.mapper;

import com.ykw.blog_system.dto.ArticleQueryDTO;
import com.ykw.blog_system.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章Mapper接口
 */
@Mapper
public interface ArticleMapper {
    
    Article selectById(Long id);
    
    Article selectByIdWithTags(Long id);
    
    int insert(Article article);
    
    int update(Article article);
    
    int deleteById(Long id);
    
    List<Article> selectList(@Param("status") Integer status, 
                             @Param("categoryId") Long categoryId,
                             @Param("keyword") String keyword);
    
    List<Article> selectByTagId(@Param("tagId") Long tagId, 
                                @Param("status") Integer status);
    
    List<Article> selectByUserId(@Param("userId") Long userId, 
                                 @Param("status") Integer status);
    
    List<Article> selectHotArticles(@Param("limit") Integer limit);
    

    
    List<Article> selectRecommendArticles(@Param("limit") Integer limit);
    
    int increaseViewCount(Long articleId);
    
    int increaseLikeCount(@Param("articleId") Long articleId, @Param("increment") Integer increment);
    
    int increaseCommentCount(@Param("articleId") Long articleId, @Param("increment") Integer increment);
    
    Long countArticles(@Param("status") Integer status);
    
    Long sumViewCount();

    List<Article> selectLatestArticles(@Param("queryDTO") ArticleQueryDTO queryDTO);
}
