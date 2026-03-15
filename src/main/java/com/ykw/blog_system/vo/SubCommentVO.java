package com.ykw.blog_system.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 子评论 VO，用于一级评论下的回复评论
 */
@Data
public class SubCommentVO extends BaseCommentVO{

    /** 父评论 ID，用于回复评论 */
    private Long parentId;

    //回复用户的id
    private Long replyToUserId;
    //回复用户的用户名
    private String replyToUsername;
    //回复用户的头像
    private String replyToUserAvatar;

    @Override
    public int compareTo(@NotNull BaseCommentVO o) {
        return this.getCreateTime().compareTo(o.getCreateTime());
    }
}
