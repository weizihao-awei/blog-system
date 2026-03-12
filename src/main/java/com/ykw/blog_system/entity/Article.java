package com.ykw.blog_system.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章实体类
 */
@Data
public class Article {
    /**
     * 文章唯一标识
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章内容（纯文本）
     */
    private String content;

    /**
     * 文章内容（HTML格式）
     */
    private String htmlContent;

    /**
     * 封面图片路径
     */
    private String coverImage;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 收藏数
     */
    private Integer collectionCount;

    /**
     * 状态（0:草稿，1:发布，2:下架等）
     */
    private Integer status;

    /**
     * 是否置顶（0:否，1:是）
     */
    private Integer isTop;

    /**
     * 是否推荐（0:否，1:是）
     */
    private Integer isRecommend;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
