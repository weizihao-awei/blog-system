package com.ykw.blog_system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("article_detail")
public class ArticleDetail {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("article_id")
    private Long articleId;

    private Integer version;

    private String content;

    @TableField("html_content")
    private String htmlContent;

    /**
     * 逻辑删除标识：0-未删除，1-已删除
     */
    @TableLogic(value = "0", delval = "1")  // 这里明确标记
    private Integer deleted;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
