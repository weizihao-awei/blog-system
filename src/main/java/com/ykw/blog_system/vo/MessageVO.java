package com.ykw.blog_system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
/**
 * 消息视图对象 (Message View Object)
 * 用于封装消息相关的展示数据，包含发送者、接收者信息及消息内容等。
 */
public class MessageVO {

    /** 消息唯一标识 */
    private Long id;

    /** 会话聊天 ID */
    private Long chatId;

    /** 发送者用户 ID */
    private Long senderId;



    /** 发送者昵称 */
    private String senderNickname;

    /** 发送者头像地址 */
    private String senderAvatar;

    /** 接收者用户 ID */
    private Long receiverId;


    /** 接收者昵称 */
    private String receiverNickname;

    /** 接收者头像地址 */
    private String receiverAvatar;

    /** 消息内容 */
    private String content;

    /** 是否已读 (0:未读, 1:已读) */
    private Integer isRead;

    /** 创建时间 */
    private LocalDateTime createTime;
}
