package com.ykw.blog_system.mapper;

import com.ykw.blog_system.entity.UserFoot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户足迹 Mapper 接口
 */
@Mapper
public interface UserFootMapper {
    
    /**
     * 插入用户足迹
     */
    int insert(UserFoot userFoot);
    
    /**
     * 更新用户足迹
     */
    int update(UserFoot userFoot);
    
    /**
     * 根据用户和文档查询足迹
     */
    UserFoot selectByUserAndDocument(@Param("userId") Long userId, 
                                     @Param("documentId") Long documentId,
                                     @Param("documentType") Integer documentType);
    
    /**
     * 查询用户的文章足迹列表（用于收藏列表）
     */
    List<UserFoot> selectByUserIdAndType(@Param("userId") Long userId,
                                         @Param("documentType") Integer documentType);
    
    /**
     * 统计文章的点赞数
     */
    Long countPraiseByDocumentId(@Param("documentId") Long documentId,
                                 @Param("documentType") Integer documentType);
    
    /**
     * 统计文章的收藏数
     */
    Long countCollectionByDocumentId(@Param("documentId") Long documentId,
                                     @Param("documentType") Integer documentType);
}
