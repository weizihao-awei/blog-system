package com.ykw.blog_system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_relation")
public class UserRelation {
    
    @TableId(type = IdType.AUTO)
    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 粉丝ID
     */
    private Long followUserId;

    /**
     * 关注状态 (0:未关注，1:已关注)
     */
    private Integer followState;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
