package com.ykw.blog_system.mapper;

import com.ykw.blog_system.entity.ArticleTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章标签关联Mapper接口
 */
@Mapper
public interface ArticleTagMapper {
    
    int insert(ArticleTag articleTag);
    
    int insertBatch(@Param("articleId") Long articleId, @Param("tagIds") List<Long> tagIds);
    
    int deleteByArticleId(Long articleId);
    
    int deleteByTagId(Long tagId);
    
    List<ArticleTag> selectByArticleId(Long articleId);
    
    List<ArticleTag> selectByTagId(Long tagId);
}
