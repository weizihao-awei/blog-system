package com.ykw.blog_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ykw.blog_system.dto.ArticleQueryDTO;
import com.ykw.blog_system.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    @Select("SELECT DAY(publish_time) as day, COUNT(*) as count " +
            "FROM article " +
            "WHERE status = 1 " +
            "AND YEAR(publish_time) = #{year} " +
            "AND MONTH(publish_time) = #{month} " +
            "GROUP BY DAY(publish_time) " +
    List<Map<String, Object>> selectDailyCountByMonth(@Param("year") Integer year, @Param("month") Integer month);

    @Select("SELECT a.*, u.nickname AS author_name, u.avatar AS author_avatar, c.name AS category_name " +
            "FROM article a " +
            "LEFT JOIN user u ON a.author_id = u.id " +
            "LEFT JOIN category c ON a.category_id = c.id " +
            "WHERE a.status = 1 " +
            "AND YEAR(a.publish_time) = YEAR(#{date}) " +
            "AND MONTH(a.publish_time) = MONTH(#{date}) " +
            "AND DATE(a.publish_time) <= #{date} " +
            "ORDER BY a.publish_time DESC")
    Page<Article> selectArticlesBeforeDate(Page<Article> page, @Param("date") LocalDate date);

    @Select("SELECT a.*, u.nickname AS author_name, u.avatar AS author_avatar, c.name AS category_name " +
            "FROM article a " +
            "LEFT JOIN user u ON a.author_id = u.id " +
            "LEFT JOIN category c ON a.category_id = c.id " +
            "WHERE a.status = 1 " +
            "AND YEAR(a.publish_time) = YEAR(#{date}) " +
            "AND MONTH(a.publish_time) = MONTH(#{date}) " +
            "AND DATE(a.publish_time) >= #{date} " +
            "ORDER BY a.publish_time ASC")
    Page<Article> selectArticlesAfterDate(Page<Article> page, @Param("date") LocalDate date);
}
