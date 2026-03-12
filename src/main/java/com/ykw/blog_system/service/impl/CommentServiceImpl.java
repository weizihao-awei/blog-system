package com.ykw.blog_system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ykw.blog_system.dto.CommentDTO;
import com.ykw.blog_system.entity.Comment;
import com.ykw.blog_system.entity.User;
import com.ykw.blog_system.mapper.ArticleMapper;
import com.ykw.blog_system.mapper.CommentMapper;
import com.ykw.blog_system.mapper.UserMapper;
import com.ykw.blog_system.service.CommentService;
import com.ykw.blog_system.utils.SecurityUtil;
import com.ykw.blog_system.vo.CommentVO;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 */
@Service
public class CommentServiceImpl implements CommentService {
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ArticleMapper articleMapper;
    
    @Override
    public ResultVO<List<CommentVO>> getCommentList(Long articleId) {
        List<Comment> comments = commentMapper.selectByArticleId(articleId, 1);
        
        // 构建评论树
        List<CommentVO> commentVOList = buildCommentTree(comments);
        
        return ResultVO.success(commentVOList);
    }
    
    /**
     * 构建评论树
     */
    private List<CommentVO> buildCommentTree(List<Comment> comments) {
        List<CommentVO> rootComments = new ArrayList<>();
        
        for (Comment comment : comments) {
            if (comment.getParentId() == null) {
                CommentVO commentVO = convertToVO(comment);
                commentVO.setChildren(findChildren(comment.getId(), comments));
                rootComments.add(commentVO);
            }
        }
        
        return rootComments;
    }
    
    /**
     * 查找子评论
     */
    private List<CommentVO> findChildren(Long parentId, List<Comment> allComments) {
        List<CommentVO> children = new ArrayList<>();
        
        for (Comment comment : allComments) {
            if (parentId.equals(comment.getParentId())) {
                CommentVO commentVO = convertToVO(comment);
                commentVO.setChildren(findChildren(comment.getId(), allComments));
                children.add(commentVO);
            }
        }
        
        return children.isEmpty() ? null : children;
    }
    
    /**
     * 转换为VO
     */
    private CommentVO convertToVO(Comment comment) {
        CommentVO commentVO = new CommentVO();
        BeanUtils.copyProperties(comment, commentVO);
        return commentVO;
    }
    
    @Override
    @Transactional
    public ResultVO<Long> createComment(CommentDTO commentDTO, Long userId) {
        Comment comment = new Comment();
        comment.setArticleId(commentDTO.getArticleId());
        comment.setParentId(commentDTO.getParentId());
        comment.setUserId(userId);
        comment.setContent(commentDTO.getContent());
        comment.setStatus(1); // 默认通过审核
        
        commentMapper.insert(comment);
        

        return ResultVO.success("评论成功", comment.getId());
    }
    
    @Override
    @Transactional
    public ResultVO<Void> deleteComment(Long commentId, Long currentUserId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            return ResultVO.error("评论不存在");
        }
        
        // 检查权限
        if (!comment.getUserId().equals(currentUserId) && !SecurityUtil.isAdmin()) {
            return ResultVO.error("无权删除此评论");
        }
        
        // 递归删除子评论
        deleteCommentAndChildren(commentId);
        

        return ResultVO.success();
    }
    
    /**
     * 递归删除评论及其子评论
     */
    private void deleteCommentAndChildren(Long commentId) {
        List<Comment> children = commentMapper.selectByParentId(commentId);
        for (Comment child : children) {
            deleteCommentAndChildren(child.getId());
        }
        commentMapper.deleteById(commentId);
    }
    
    @Override
    public ResultVO<Void> updateCommentStatus(Long commentId, Integer status) {
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setStatus(status);
        commentMapper.update(comment);
        return ResultVO.success();
    }
    
    @Override
    public ResultVO<PageVO<CommentVO>> getAdminCommentList(Integer pageNum, Integer pageSize, Integer status) {
        // 创建 Page 对象
        Page<Comment> page = new Page<>(pageNum, pageSize);
        
        // 执行分页查询
        List<Comment> list = commentMapper.selectPage(page, status);
        
        // 转换为 VO 列表
        List<CommentVO> voList = list.stream().map(this::convertToVO).collect(Collectors.toList());
        
        // 构建分页结果
        PageVO<CommentVO> pageVO = new PageVO<>(
            voList, 
            page.getTotal(), 
            pageNum, 
            pageSize
        );
        
        return ResultVO.success(pageVO);
    }
}
