package com.ykw.blog_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ykw.blog_system.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签Mapper接口
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {
    
    // 以下方法为多表联查或复杂查询，保留 XML 实现
    
    /**
     * 根据文章 ID 查询标签列表 (多表联查)
     */
    List<Tag> selectByArticleId(Long articleId);
    
    /**
     * 根据多个文章 ID 批量查询标签 (多表联查)
     */
    List<Tag> selectByArticleIds(@Param("articleIds") List<Long> articleIds);
    
    /**
     * 查询热门标签 (带统计排序)
     */
    List<Tag> selectHotTags(@Param("limit") Integer limit);
}
