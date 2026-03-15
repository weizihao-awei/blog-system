package com.ykw.blog_system.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 评论VO
 */
@Data
public class CommentVO extends BaseCommentVO{


    private List<SubCommentVO> children;

    //排序
    @Override
    public int compareTo(@NotNull BaseCommentVO o) {
        return o.getCreateTime().compareTo(this.getCreateTime());
    }
}
