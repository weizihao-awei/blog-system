package com.ykw.blog_system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("blog_message_chat")
public class MessageChat {

    /**
     * 主键 ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户 1 的 ID
     */
    private Long userId1;

    /**
     * 用户 2 的 ID
     */
    private Long userId2;

    /**
     * 最后一条消息的 ID
     */
    private Long lastMsgId;

    /**
     * 最后一条消息的内容
     */
    private String lastMsgContent;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
