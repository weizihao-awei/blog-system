package com.ykw.blog_system.service;

import com.ykw.blog_system.dto.ChatIdQueryDTO;
import com.ykw.blog_system.dto.ChatListQueryDTO;
import com.ykw.blog_system.dto.MessageListQueryDTO;
import com.ykw.blog_system.dto.SendMessageDTO;
import com.ykw.blog_system.entity.Message;
import com.ykw.blog_system.vo.ChatVO;
import com.ykw.blog_system.vo.MessageVO;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;

public interface MessageService {

    ResultVO<PageVO<ChatVO>> getChatList(ChatListQueryDTO queryDTO);

    ResultVO<Long> getChatCount();

    ResultVO<PageVO<MessageVO>> getMessageList(MessageListQueryDTO queryDTO);

    ResultVO<Long> getMessageCount(Long chatId);

    ResultVO<Long> getTotalMessageCount();

    ResultVO<Long> getUnreadMessageCount();

    ResultVO<Long> getChatId(ChatIdQueryDTO queryDTO);

    ResultVO<Long> sendMessage(SendMessageDTO sendMessageDTO);
}
