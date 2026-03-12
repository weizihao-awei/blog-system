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


    @Select("SELECT a.*, u.nickname AS author_name, u.avatar AS author_avatar, c.name AS category_name " +
            "FROM article a " +
            "LEFT JOIN user u ON a.author_id = u.id " +
            "LEFT JOIN category c ON a.category_id = c.id " +
            "WHERE a.status = 1 " +
            "ORDER BY a.view_count DESC, a.like_count DESC")
    List<Article> selectHotArticles(@Param("limit") Integer limit);



    Long countArticles(@Param("status") Integer status);
    
    Long sumViewCount();
}
