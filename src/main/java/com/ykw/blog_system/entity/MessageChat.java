package com.ykw.blog_system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("blog_message_chat")
public class MessageChat {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId1;

    private Long userId2;

    private Long lastMsgId;

    private LocalDateTime updateTime;
}
