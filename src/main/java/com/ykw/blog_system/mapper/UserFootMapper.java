package com.ykw.blog_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ykw.blog_system.entity.UserFoot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserFootMapper extends BaseMapper<UserFoot> {

    @Select("SELECT * FROM user_foot " +
            "WHERE user_id = #{userId} " +
            "AND document_id = #{documentId} " +
            "AND document_type = #{documentType}")
    UserFoot selectByUserAndDocument(@Param("userId") Long userId, 
                                     @Param("documentId") Long documentId,
                                     @Param("documentType") Integer documentType);

    @Select("SELECT * FROM user_foot " +
            "WHERE user_id = #{userId} " +
            "AND document_type = #{documentType} " +
            "AND collection_stat = 1 " +
            "ORDER BY update_time DESC")
    List<UserFoot> selectByUserIdAndType(@Param("userId") Long userId,
                                         @Param("documentType") Integer documentType);

    @Select("SELECT COUNT(*) FROM user_foot " +
            "WHERE document_id = #{documentId} " +
            "AND document_type = #{documentType} " +
            "AND praise_stat = 1")
    Long countPraiseByDocumentId(@Param("documentId") Long documentId,
                                 @Param("documentType") Integer documentType);

    @Select("SELECT COUNT(*) FROM user_foot " +
            "WHERE document_id = #{documentId} " +
            "AND document_type = #{documentType} " +
            "AND collection_stat = 1")
    Long countCollectionByDocumentId(@Param("documentId") Long documentId,
                                     @Param("documentType") Integer documentType);

    /**
     * 分页查询用户收藏记录
     */
    Page<UserFoot> selectCollectionPage(Page<UserFoot> page,
                                        @Param("userId") Long userId,
                                        @Param("documentType") Integer documentType,
                                        @Param("order") String order);

    /**
     * 分页查询用户点赞记录
     */
    Page<UserFoot> selectPraisePage(Page<UserFoot> page,
                                    @Param("userId") Long userId,
                                    @Param("documentType") Integer documentType,
                                    @Param("order") String order);

    /**
     * 分页查询用户浏览记录
     */
    Page<UserFoot> selectReadPage(Page<UserFoot> page,
                                  @Param("userId") Long userId,
                                  @Param("documentType") Integer documentType,
                                  @Param("order") String order);
}
