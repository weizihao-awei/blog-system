package com.ykw.blog_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ykw.blog_system.dto.ArticleQueryDTO;
import com.ykw.blog_system.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {


    /**
     * 增加文章收藏数
     */
    @Update("UPDATE article SET collection_count = collection_count + #{increment} WHERE id = #{articleId}")
    int updateCollectionCount(@Param("articleId") Long articleId, @Param("increment") Integer increment);


    /**
     * 增加文章点赞数
     */
    @Update("UPDATE article SET like_count = like_count + #{increment} WHERE id = #{articleId}")
    int updateLikeCount(@Param("articleId") Long articleId, @Param("increment") Integer increment);





    @Select("SELECT a.*, u.nickname AS author_name, u.avatar AS author_avatar, c.name AS category_name " +
            "FROM article a " +
            "LEFT JOIN user u ON a.author_id = u.id " +
            "LEFT JOIN category c ON a.category_id = c.id " +
            "WHERE a.status = 1 " +
            "ORDER BY a.view_count DESC, a.like_count DESC")
    List<Article> selectHotArticles(@Param("limit") Integer limit);



    Long countArticles(@Param("status") Integer status);
    
    Long sumViewCount();

    /**
     * 分页查询文章（支持多条件查询，包括标签）
     */
    Page<Article> selectArticlesByCondition(Page<Article> page, @Param("queryDTO") ArticleQueryDTO queryDTO);
}
