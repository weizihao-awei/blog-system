package com.ykw.blog_system.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ykw.blog_system.dto.CategoryDTO;
import com.ykw.blog_system.entity.Category;
import com.ykw.blog_system.mapper.CategoryMapper;
import com.ykw.blog_system.service.CategoryService;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Override
    public ResultVO<List<Category>> getCategoryList() {
        List<Category> list = categoryMapper.selectList(1);
        return ResultVO.success(list);
    }
    
    @Override
    public ResultVO<Category> getCategoryDetail(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            return ResultVO.error("分类不存在");
        }
        return ResultVO.success(category);
    }
    
    @Override
    public ResultVO<Long> createCategory(CategoryDTO categoryDTO) {
        // 检查分类名是否已存在
        if (categoryMapper.selectByName(categoryDTO.getName()) != null) {
            return ResultVO.error("分类名称已存在");
        }
        
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(1);
        
        categoryMapper.insert(category);
        
        return ResultVO.success("创建成功", category.getId());
    }
    
    @Override
    public ResultVO<Void> updateCategory(CategoryDTO categoryDTO) {
        Category existingCategory = categoryMapper.selectById(categoryDTO.getId());
        if (existingCategory == null) {
            return ResultVO.error("分类不存在");
        }
        
        // 检查新名称是否与其他分类重复
        Category nameCategory = categoryMapper.selectByName(categoryDTO.getName());
        if (nameCategory != null && !nameCategory.getId().equals(categoryDTO.getId())) {
            return ResultVO.error("分类名称已存在");
        }
        
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.update(category);
        
        return ResultVO.success();
    }
    
    @Override
    public ResultVO<Void> deleteCategory(Long categoryId) {
        categoryMapper.deleteById(categoryId);
        return ResultVO.success();
    }
    
    @Override
    public ResultVO<PageVO<Category>> getAdminCategoryList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Category> list = categoryMapper.selectList(null);
        PageInfo<Category> pageInfo = new PageInfo<>(list);
        
        PageVO<Category> pageVO = new PageVO<>(list, pageInfo.getTotal(), pageNum, pageSize);
        return ResultVO.success(pageVO);
    }
}
