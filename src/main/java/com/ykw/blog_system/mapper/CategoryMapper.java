package com.ykw.blog_system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ykw.blog_system.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类Mapper接口
 */
@Mapper
public interface CategoryMapper {
    
    Category selectById(Long id);
    
    Category selectByName(String name);
    
    int insert(Category category);
    
    int update(Category category);
    
    int deleteById(Long id);
    
    List<Category> selectList(@Param("status") Integer status);
    
    Long countCategories();
    
    List<Category> selectPage(Page<Category> page);
}
