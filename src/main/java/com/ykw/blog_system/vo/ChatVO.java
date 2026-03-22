package com.ykw.blog_system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
/**
 * 聊天会话视图对象 (VO)
 * 用于封装聊天列表或聊天详情展示所需的数据
 */
public class ChatVO {

    /** 聊天会话 ID */
    private Long chatId;

    /** 对方用户 ID */
    private Long otherUserId;



    /** 对方昵称 */
    private String otherNickname;

    /** 对方头像 URL */
    private String otherAvatar;

    /** 最后一条消息 ID */
    private Long lastMessageId;

    /** 最后一条消息内容 */
    private String lastMessageContent;

    /** 最后一条消息发送时间 */
    private LocalDateTime lastMessageTime;

    /** 会话更新时间 */
    private LocalDateTime updateTime;

    /** 未读消息数量 */
    private Integer unreadCount;
}
