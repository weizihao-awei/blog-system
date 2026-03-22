package com.ykw.blog_system.service;

import com.ykw.blog_system.dto.ChatIdQueryDTO;
import com.ykw.blog_system.dto.ChatListQueryDTO;
import com.ykw.blog_system.dto.GetOrCreateChatDTO;
import com.ykw.blog_system.dto.MessageListQueryDTO;
import com.ykw.blog_system.dto.SendMessageDTO;
import com.ykw.blog_system.entity.Message;
import com.ykw.blog_system.vo.ChatVO;
import com.ykw.blog_system.vo.MessageVO;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;

public interface MessageService {

    /**
     * 获取聊天列表
     *
     * @param queryDTO 查询参数
     * @return 聊天列表分页数据
     */
    ResultVO<PageVO<ChatVO>> getChatList(ChatListQueryDTO queryDTO);

    /**
     * 获取消息列表
     *
     * @param queryDTO 查询参数
     * @return 消息列表分页数据
     */
    ResultVO<PageVO<MessageVO>> getMessageList(MessageListQueryDTO queryDTO);

    /**
     * 获取未读消息数量
     *
     * @return 未读消息数量
     */
    ResultVO<Long> getUnreadMessageCount();

    /**
     * 发送消息
     *
     * @param sendMessageDTO 发送消息参数
     * @return 消息 ID
     */
    ResultVO<Long> sendMessage(SendMessageDTO sendMessageDTO);

    /**
     * 获取或创建会话
     *
     * @param getOrCreateChatDTO 获取或创建会话参数
     * @return 会话 ID
     */
    ResultVO<Long> getOrCreateChat(GetOrCreateChatDTO getOrCreateChatDTO);
}
