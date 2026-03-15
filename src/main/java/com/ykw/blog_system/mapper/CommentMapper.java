package com.ykw.blog_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ykw.blog_system.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论Mapper接口
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
//
//    Comment selectById(Long id);
//
//    int insert(Comment comment);
//
//    int update(Comment comment);
//
//    int deleteById(Long id);
//
//    int deleteByArticleId(Long articleId);
//
//    List<Comment> selectByArticleId(@Param("articleId") Long articleId,
//                                    @Param("status") Integer status);
//
//    List<Comment> selectByParentId(Long parentId);
//
//    Long countComments(@Param("status") Integer status);
//
//    Long countByArticleId(Long articleId);
//
//    List<Comment> selectPage(Page<Comment> page, @Param("status") Integer status);
}
