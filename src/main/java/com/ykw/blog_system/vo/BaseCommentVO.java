package com.ykw.blog_system.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @BelongsProject: blog-system
 * @BelongsPackage: com.ykw.blog_system.vo
 * @Author: ykw-weizihao
 * @CreateTime: 2026-03-15 09:31
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class BaseCommentVO implements Comparable<BaseCommentVO>{
    /** 评论 ID */
    private Long id;

    /** 文章 ID */
    private Long articleId;


    /** 用户 ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 用户头像 URL */
    private String userAvatar;

    /** 评论内容 */
    private String content;



    /** 评论创建时间 */
    private LocalDateTime createTime;

    // 排序
    @Override
    public int compareTo(@NotNull BaseCommentVO o) {
        return this.getCreateTime().compareTo(o.getCreateTime());
    }
}