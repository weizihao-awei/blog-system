package com.ykw.blog_system.controller;

import com.ykw.blog_system.dto.TagDTO;
import com.ykw.blog_system.entity.Tag;
import com.ykw.blog_system.service.TagService;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器
 */
@RestController
@RequestMapping("/api/tag")
public class TagController {
    
    @Autowired
    private TagService tagService;
    
    /**
     * 获取标签列表
     */
    @GetMapping("/list")
    public ResultVO<List<Tag>> getTagList() {
        return tagService.getTagList();
    }
    
    /**
     * 获取热门标签
     */
    @GetMapping("/hot")
    public ResultVO<List<Tag>> getHotTags(@RequestParam(defaultValue = "10") Integer limit) {
        return tagService.getHotTags(limit);
    }
    
    /**
     * 获取标签详情
     */
    @GetMapping("/detail/{tagId}")
    public ResultVO<Tag> getTagDetail(@PathVariable Long tagId) {
        return tagService.getTagDetail(tagId);
    }
    
    /**
     * 创建标签（管理员）
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<Long> createTag(@Valid @RequestBody TagDTO tagDTO) {
        return tagService.createTag(tagDTO);
    }
    
    /**
     * 更新标签（管理员）
     */
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<Void> updateTag(@Valid @RequestBody TagDTO tagDTO) {
        return tagService.updateTag(tagDTO);
    }
    
    /**
     * 删除标签（管理员）
     */
    @DeleteMapping("/delete/{tagId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<Void> deleteTag(@PathVariable Long tagId) {
        return tagService.deleteTag(tagId);
    }
    
    /**
     * 获取标签列表（管理员分页）
     */
    @GetMapping("/admin-list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<PageVO<Tag>> getAdminTagList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return tagService.getAdminTagList(pageNum, pageSize);
    }
}
