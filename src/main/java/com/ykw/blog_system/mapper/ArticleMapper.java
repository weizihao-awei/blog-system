package com.ykw.blog_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ykw.blog_system.dto.ArticleQueryDTO;
import com.ykw.blog_system.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    @Select("SELECT a.*, u.nickname AS author_name, u.avatar AS author_avatar, c.name AS category_name " +
            "FROM article a " +
            "LEFT JOIN user u ON a.author_id = u.id " +
            "LEFT JOIN category c ON a.category_id = c.id " +
            "WHERE a.id = #{id}")
    Article selectByIdWithTags(Long id);

    @Select("<script>" +
            "SELECT a.*, u.nickname AS author_name, u.avatar AS author_avatar, c.name AS category_name " +
            "FROM article a " +
            "INNER JOIN article_tag at ON a.id = at.article_id " +
            "LEFT JOIN user u ON a.author_id = u.id " +
            "LEFT JOIN category c ON a.category_id = c.id " +
            "WHERE at.tag_id = #{tagId} " +
            "<if test='status != null'>AND a.status = #{status}</if> " +
            "ORDER BY a.is_top DESC, a.publish_time DESC" +
            "</script>")
    List<Article> selectByTagId(@Param("tagId") Long tagId, @Param("status") Integer status);

    @Select("<script>" +
            "SELECT a.*, u.nickname AS author_name, u.avatar AS author_avatar, c.name AS category_name " +
            "FROM article a " +
            "LEFT JOIN user u ON a.author_id = u.id " +
            "LEFT JOIN category c ON a.category_id = c.id " +
            "WHERE a.author_id = #{userId} " +
            "<if test='status != null'>AND a.status = #{status}</if> " +
            "ORDER BY a.create_time DESC" +
            "</script>")
    List<Article> selectByUserId(@Param("userId") Long userId, @Param("status") Integer status);

    @Select("SELECT a.*, u.nickname AS author_name, u.avatar AS author_avatar, c.name AS category_name " +
            "FROM article a " +
            "LEFT JOIN user u ON a.author_id = u.id " +
            "LEFT JOIN category c ON a.category_id = c.id " +
            "WHERE a.status = 1 " +
            "ORDER BY a.view_count DESC, a.like_count DESC")
    List<Article> selectHotArticles(@Param("limit") Integer limit);

    @Select("SELECT a.*, u.nickname AS author_name, u.avatar AS author_avatar, c.name AS category_name " +
            "FROM article a " +
            "LEFT JOIN user u ON a.author_id = u.id " +
            "LEFT JOIN category c ON a.category_id = c.id " +
            "WHERE a.status = 1 AND a.is_recommend = 1 " +
            "ORDER BY a.publish_time DESC " +
            "LIMIT #{limit}")
    List<Article> selectRecommendArticles(@Param("limit") Integer limit);

    @Select("<script>" +
            "SELECT a.*, u.nickname AS author_name, u.avatar AS author_avatar, c.name AS category_name " +
            "FROM article a " +
            "LEFT JOIN user u ON a.author_id = u.id " +
            "LEFT JOIN category c ON a.category_id = c.id " +
            "<if test='queryDTO.tagId != null'>INNER JOIN article_tag at ON a.id = at.article_id</if> " +
            "WHERE a.status = 1 " +
            "AND a.publish_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
            "<if test='queryDTO.categoryId != null'>AND a.category_id = #{queryDTO.categoryId}</if> " +
            "<if test='queryDTO.tagId != null'>AND at.tag_id = #{queryDTO.tagId}</if> " +
            "ORDER BY a.is_top DESC, a.publish_time DESC" +
            "</script>")
    List<Article> selectLatestArticles(@Param("queryDTO") ArticleQueryDTO queryDTO);
}
