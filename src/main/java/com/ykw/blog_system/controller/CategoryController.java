package com.ykw.blog_system.controller;

import com.ykw.blog_system.dto.CategoryDTO;
import com.ykw.blog_system.entity.Category;
import com.ykw.blog_system.service.CategoryService;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    /**
     * 获取分类列表
     */
    @GetMapping("/list")
    public ResultVO<List<Category>> getCategoryList() {
        return categoryService.getCategoryList();
    }
    
    /**
     * 获取分类详情
     */
    @GetMapping("/detail/{categoryId}")
    public ResultVO<Category> getCategoryDetail(@PathVariable Long categoryId) {
        return categoryService.getCategoryDetail(categoryId);
    }
    
//    /**
//     * 创建分类（管理员）
//     */
//    @PostMapping("/create")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResultVO<Long> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
//        return categoryService.createCategory(categoryDTO);
//    }
//
//    /**
//     * 更新分类（管理员）
//     */
//    @PutMapping("/update")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResultVO<Void> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
//        return categoryService.updateCategory(categoryDTO);
//    }
//
//    /**
//     * 删除分类（管理员）
//     */
//    @DeleteMapping("/delete/{categoryId}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResultVO<Void> deleteCategory(@PathVariable Long categoryId) {
//        return categoryService.deleteCategory(categoryId);
//    }
//
//    /**
//     * 获取分类列表（管理员分页）
//     */
//    @GetMapping("/admin-list")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResultVO<PageVO<Category>> getAdminCategoryList(
//            @RequestParam(defaultValue = "1") Integer pageNum,
//            @RequestParam(defaultValue = "10") Integer pageSize) {
//        return categoryService.getAdminCategoryList(pageNum, pageSize);
//    }
}
