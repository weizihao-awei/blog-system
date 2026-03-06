package com.ykw.blog_system.service;

import com.ykw.blog_system.dto.CommentDTO;
import com.ykw.blog_system.vo.CommentVO;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;

import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService {
    
    ResultVO<List<CommentVO>> getCommentList(Long articleId);
    
    ResultVO<Long> createComment(CommentDTO commentDTO, Long userId);
    
    ResultVO<Void> deleteComment(Long commentId, Long currentUserId);
    
    ResultVO<Void> updateCommentStatus(Long commentId, Integer status);
    
    ResultVO<PageVO<CommentVO>> getAdminCommentList(Integer pageNum, Integer pageSize, Integer status);
}
