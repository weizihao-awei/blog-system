package com.ykw.blog_system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章视图对象（VO）
 * 用于展示层，包含数据库字段和扩展字段
 */
@Data
public class ArticleVO {
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
     * 文章内容（HTML 格式）
     */
    private String htmlContent;

    /**
     * 封面图片路径
     */
    private String coverImage;

    /**
     * 作者 ID
     */
    private Long authorId;

    /**
     * 分类 ID
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

    // 扩展字段（非数据库字段）

    /**
     * 作者姓名
     */
    private String authorName;

    /**
     * 作者头像
     */
    private String authorAvatar;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 标签列表
     */
    private List<TagVO> tags;

    /**
     * 当前用户是否已点赞
     */
    private Boolean isLiked;

    /**
     * 当前用户是否已收藏
     */
    private Boolean isCollected;

    /**
     * 推荐得分（用于推荐算法）
     */
    private Double recommendScore;
}
