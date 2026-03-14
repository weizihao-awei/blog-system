package com.ykw.blog_system.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
        // 使用 MyBatis-Plus LambdaQueryWrapper 查询
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getStatus, 1)
                .orderByAsc(Category::getSortOrder)
                .orderByDesc(Category::getCreateTime);
        List<Category> list = categoryMapper.selectList(wrapper);
        return ResultVO.success(list);
    }
    
    @Override
    public ResultVO<Category> getCategoryDetail(Long categoryId) {
        // 使用 MyBatis-Plus selectById 方法
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            return ResultVO.error("分类不存在");
        }
        return ResultVO.success(category);
    }
    
    @Override
    public ResultVO<Long> createCategory(CategoryDTO categoryDTO) {
        // 使用 MyBatis-Plus 检查分类名是否已存在
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, categoryDTO.getName());
        if (categoryMapper.selectOne(wrapper) != null) {
            return ResultVO.error("分类名称已存在");
        }
        
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(1);
        
        // 使用 MyBatis-Plus insert 方法
        categoryMapper.insert(category);
        
        return ResultVO.success("创建成功", category.getId());
    }
    
    @Override
    public ResultVO<Void> updateCategory(CategoryDTO categoryDTO) {
        // 使用 MyBatis-Plus selectById 方法
        Category existingCategory = categoryMapper.selectById(categoryDTO.getId());
        if (existingCategory == null) {
            return ResultVO.error("分类不存在");
        }
        
        // 检查新名称是否与其他分类重复
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, categoryDTO.getName())
                .ne(Category::getId, categoryDTO.getId());
        if (categoryMapper.selectOne(wrapper) != null) {
            return ResultVO.error("分类名称已存在");
        }
        
        // 使用 MyBatis-Plus updateById 方法
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.updateById(category);
        
        return ResultVO.success();
    }
    
    @Override
    public ResultVO<Void> deleteCategory(Long categoryId) {
        // 使用 MyBatis-Plus deleteById 方法
        categoryMapper.deleteById(categoryId);
        return ResultVO.success();
    }
    
    @Override
    public ResultVO<PageVO<Category>> getAdminCategoryList(Integer pageNum, Integer pageSize) {
        // 创建 Page 对象
        Page<Category> page = new Page<>(pageNum, pageSize);
        
        // 使用 MyBatis-Plus 分页查询
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSortOrder)
                .orderByDesc(Category::getCreateTime);
        
        Page<Category> pageResult = categoryMapper.selectPage(page, wrapper);
        List<Category> list = pageResult.getRecords();
        
        // 构建分页结果
        PageVO<Category> pageVO = new PageVO<>(
            list, 
            pageResult.getTotal(), 
            pageNum, 
            pageSize
        );
        
        return ResultVO.success(pageVO);
    }
}
