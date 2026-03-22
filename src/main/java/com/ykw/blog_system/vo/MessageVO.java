package com.ykw.blog_system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageVO {

    private Long id;

    private Long chatId;

    private Long senderId;

    private String senderUsername;

    private String senderNickname;

    private String senderAvatar;

    private Long receiverId;

    private String receiverUsername;

    private String receiverNickname;

    private String receiverAvatar;

    private String content;

    private Integer isRead;

    private LocalDateTime createTime;
}
