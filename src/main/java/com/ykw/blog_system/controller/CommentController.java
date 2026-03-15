package com.ykw.blog_system.controller;

import com.ykw.blog_system.dto.CommentDTO;
import com.ykw.blog_system.service.CommentService;
import com.ykw.blog_system.utils.SecurityUtil;
import com.ykw.blog_system.vo.CommentVO;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/api/comment")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    /**
     * 获取文章评论列表
     */
    @GetMapping("/list/{articleId}")
    public ResultVO<List<CommentVO>> getCommentList(@PathVariable Long articleId) {
        return commentService.getCommentList(articleId);
    }
    
    /**
     * 创建评论
     */
    @PostMapping("/create")
    public ResultVO<Long> createComment(@Valid @RequestBody CommentDTO commentDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        return commentService.createComment(commentDTO, userId);
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/delete/{commentId}")
    public ResultVO<Void> deleteComment(@PathVariable Long commentId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return commentService.deleteComment(commentId, currentUserId);
    }
    
//    /**
//     * 更新评论状态（管理员）
//     */
//    @PutMapping("/{commentId}/status/{status}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResultVO<Void> updateCommentStatus(@PathVariable Long commentId, @PathVariable Integer status) {
//        return commentService.updateCommentStatus(commentId, status);
//    }
//
//    /**
//     * 获取评论列表（管理员分页）
//     */
//    @GetMapping("/admin-list")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResultVO<PageVO<CommentVO>> getAdminCommentList(
//            @RequestParam(defaultValue = "1") Integer pageNum,
//            @RequestParam(defaultValue = "10") Integer pageSize,
//            @RequestParam(required = false) Integer status) {
//        return commentService.getAdminCommentList(pageNum, pageSize, status);
//    }
}
