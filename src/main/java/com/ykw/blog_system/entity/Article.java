package com.ykw.blog_system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("article")
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String summary;

    @TableField("cover_image")
    private String coverImage;

    @TableField("author_id")
    private Long authorId;

    @TableField("category_id")
    private Long categoryId;

    @TableField("article_content_id")
    private Long articleContentId;

    @TableField("view_count")
    private Integer viewCount;

    @TableField("like_count")
    private Integer likeCount;

    @TableField("comment_count")
    private Integer commentCount;

    @TableField("collection_count")
    private Integer collectionCount;

    private Integer status;

    @TableField("is_top")
    private Integer isTop;

    @TableField("is_recommend")
    private Integer isRecommend;

    @TableField("publish_time")
    private LocalDateTime publishTime;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
