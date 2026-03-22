package com.ykw.blog_system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatVO {

    private Long chatId;

    private Long otherUserId;

    private String otherUsername;

    private String otherNickname;

    private String otherAvatar;

    private Long lastMessageId;

    private String lastMessageContent;

    private LocalDateTime lastMessageTime;

    private LocalDateTime updateTime;

    private Integer unreadCount;
}
