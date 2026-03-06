package com.ykw.blog_system.service;

import com.ykw.blog_system.dto.CategoryDTO;
import com.ykw.blog_system.entity.Category;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {
    
    ResultVO<List<Category>> getCategoryList();
    
    ResultVO<Category> getCategoryDetail(Long categoryId);
    
    ResultVO<Long> createCategory(CategoryDTO categoryDTO);
    
    ResultVO<Void> updateCategory(CategoryDTO categoryDTO);
    
    ResultVO<Void> deleteCategory(Long categoryId);
    
    ResultVO<PageVO<Category>> getAdminCategoryList(Integer pageNum, Integer pageSize);
}
