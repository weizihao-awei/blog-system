package com.ykw.blog_system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("blog_message")
public class Message {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long chatId;

    private Long senderId;

    private Long receiverId;

    private String content;

    private Integer isRead;

    private LocalDateTime createTime;
}
