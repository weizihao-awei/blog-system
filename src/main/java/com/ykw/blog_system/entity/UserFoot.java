package com.ykw.blog_system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户足迹实体类（统一管理点赞、收藏）
 */
@Data
public class UserFoot {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long documentId;
    
    private Integer documentType; // 1-文章，2-评论
    
    private Long documentUserId; // 发布该文档的用户 ID
    
    private Integer     collectionStat; // 0-未收藏，1-已收藏，2-取消收藏
    
    private Integer readStat; // 0-未读，1-已读
    
    private Integer commentStat; // 0-未评论，1-已评论，2-删除评论
    
    private Integer praiseStat; // 0-未点赞，1-已点赞，2-取消点赞
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
