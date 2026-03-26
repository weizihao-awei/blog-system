package com.ykw.blog_system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ykw.blog_system.entity.ArticleDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArticleDetailMapper extends BaseMapper<ArticleDetail> {
    @Select("SELECT MAX(version) FROM article_detail WHERE article_id = #{articleId} AND deleted = 0")
    Integer getMaxVersion(@Param("articleId") Long articleId);

    @Select("SELECT * FROM article_detail WHERE article_id = #{articleId} AND version = #{version} AND deleted = 0")
    ArticleDetail selectByArticleIdAndVersion(@Param("articleId") Long articleId, @Param("version") Integer version);
}
