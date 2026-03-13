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
public interface TagMapper  extends BaseMapper<Tag> {
    
    Tag selectById(Long id);
    
    Tag selectByName(String name);
    
    int insert(Tag tag);
    
    int update(Tag tag);
    
    int deleteById(Long id);
    
    List<Tag> selectList(@Param("status") Integer status);
    
    List<Tag> selectByArticleId(Long articleId);
    
    List<Tag> selectByArticleIds(@Param("articleIds") List<Long> articleIds);
    
    List<Tag> selectHotTags(@Param("limit") Integer limit);
    
    Long countTags();
    
    List<Tag> selectPage(Page<Tag> page);
}
