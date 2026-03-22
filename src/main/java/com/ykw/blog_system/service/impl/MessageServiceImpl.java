package com.ykw.blog_system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ykw.blog_system.Config.MessageWebSocketHandler;
import com.ykw.blog_system.dto.ChatIdQueryDTO;
import com.ykw.blog_system.dto.ChatListQueryDTO;
import com.ykw.blog_system.dto.MessageListQueryDTO;
import com.ykw.blog_system.dto.SendMessageDTO;
import com.ykw.blog_system.entity.Message;
import com.ykw.blog_system.entity.MessageChat;
import com.ykw.blog_system.entity.User;
import com.ykw.blog_system.enums.ResultCodeEnum;
import com.ykw.blog_system.mapper.MessageChatMapper;
import com.ykw.blog_system.mapper.MessageMapper;
import com.ykw.blog_system.mapper.UserMapper;
import com.ykw.blog_system.service.MessageService;
import com.ykw.blog_system.utils.SecurityUtil;
import com.ykw.blog_system.vo.ChatVO;
import com.ykw.blog_system.vo.MessageVO;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageChatMapper messageChatMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ResultVO<PageVO<ChatVO>> getChatList(ChatListQueryDTO queryDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        Integer pageNum = queryDTO.getPageNum() != null ? queryDTO.getPageNum() : 1;
        Integer pageSize = queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 10;

        LambdaQueryWrapper<MessageChat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq(MessageChat::getUserId1, userId)
                .or()
                .eq(MessageChat::getUserId2, userId));
        queryWrapper.orderByDesc(MessageChat::getUpdateTime);

        Page<MessageChat> page = new Page<>(pageNum, pageSize);
        Page<MessageChat> resultPage = messageChatMapper.selectPage(page, queryWrapper);

        List<ChatVO> chatVOList = resultPage.getRecords().stream().map(chat -> {
            ChatVO chatVO = new ChatVO();
            chatVO.setChatId(chat.getId());

            Long otherUserId = chat.getUserId1().equals(userId) ? chat.getUserId2() : chat.getUserId1();
            chatVO.setOtherUserId(otherUserId);

            User otherUser = userMapper.selectById(otherUserId);
            if (otherUser != null) {
                chatVO.setOtherUsername(otherUser.getUsername());
                chatVO.setOtherNickname(otherUser.getNickname());
                chatVO.setOtherAvatar(otherUser.getAvatar());
            }

            chatVO.setLastMessageId(chat.getLastMsgId());
            chatVO.setLastMessageContent(chat.getLastMsgContent());
            chatVO.setUpdateTime(chat.getUpdateTime());

            if (chat.getLastMsgId() != null) {
                Message lastMessage = messageMapper.selectById(chat.getLastMsgId());
                if (lastMessage != null) {
                    chatVO.setLastMessageTime(lastMessage.getCreateTime());
                }
            }

            LambdaQueryWrapper<Message> unreadQueryWrapper = new LambdaQueryWrapper<>();
            unreadQueryWrapper.eq(Message::getChatId, chat.getId())
                    .eq(Message::getReceiverId, userId)
                    .eq(Message::getIsRead, 0);
            Long unreadCount = messageMapper.selectCount(unreadQueryWrapper);
            chatVO.setUnreadCount(unreadCount.intValue());

            return chatVO;
        }).collect(Collectors.toList());

        PageVO<ChatVO> pageVO = new PageVO<>(chatVOList, resultPage.getTotal(), pageNum, pageSize);
        return ResultVO.success(pageVO);
    }

    @Override
    public ResultVO<Long> getChatCount() {
        Long userId = SecurityUtil.getCurrentUserId();
        LambdaQueryWrapper<MessageChat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq(MessageChat::getUserId1, userId)
                .or()
                .eq(MessageChat::getUserId2, userId));
        Long count = messageChatMapper.selectCount(queryWrapper);
        return ResultVO.success(count);
    }


    /**
     * 获取消息列表
     *
     * @param queryDTO 获取消息列表参数
     * @return 消息列表
     */
    @Override
    @Transactional
    public ResultVO<PageVO<MessageVO>> getMessageList(MessageListQueryDTO queryDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        Long chatId = queryDTO.getChatId();
        int pageNum = queryDTO.getPageNum() != null ? queryDTO.getPageNum() : 1;
        int pageSize = queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 20;

        MessageChat chat = messageChatMapper.selectById(chatId);
        if (chat == null) {
            return ResultVO.error("会话不存在");
        }

        if (!chat.getUserId1().equals(userId) && !chat.getUserId2().equals(userId)) {
            return ResultVO.error("无权访问此会话");
        }

        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getChatId, chatId);
        queryWrapper.orderByAsc(Message::getCreateTime);

        Page<Message> page = new Page<>(pageNum, pageSize);
        Page<Message> resultPage = messageMapper.selectPage(page, queryWrapper);



        // 构建消息视图对象
        List<MessageVO> messageVOList = resultPage.getRecords().stream().map(message -> {
            MessageVO messageVO = new MessageVO();
            BeanUtils.copyProperties(message, messageVO);
            return messageVO;
        }).collect(Collectors.toList());


        //判断一下消息视图是否为空，不为空填充用户信息
        if (!messageVOList.isEmpty()) {
            //获取发送者信息
            Long senderId = messageVOList.get(0).getSenderId();
            User sender = userMapper.selectById(senderId);
            //获取接收者信息
            Long receiverId = messageVOList.get(0).getReceiverId();
            User receiver = userMapper.selectById(receiverId);
            messageVOList.forEach(messageVO -> {
                messageVO.setSenderNickname(sender.getNickname());
                messageVO.setSenderAvatar(sender.getAvatar());
                messageVO.setReceiverNickname(receiver.getNickname());
                messageVO.setReceiverAvatar(receiver.getAvatar());
            });



        }

        //更新列表消息为已读
        LambdaQueryWrapper<Message> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(Message::getChatId, chatId)
                .eq(Message::getReceiverId, userId)
                .eq(Message::getIsRead, 0);
        Message updateMessage = new Message();
        updateMessage.setIsRead(1);
        messageMapper.update(updateMessage, updateWrapper);

        PageVO<MessageVO> pageVO = new PageVO<>(messageVOList, resultPage.getTotal(), pageNum, pageSize);
        return ResultVO.success(pageVO);
    }

    @Override
    public ResultVO<Long> getMessageCount(Long chatId) {
        Long userId = SecurityUtil.getCurrentUserId();
        MessageChat chat = messageChatMapper.selectById(chatId);
        if (chat == null) {
            return ResultVO.error("会话不存在");
        }

        if (!chat.getUserId1().equals(userId) && !chat.getUserId2().equals(userId)) {
            return ResultVO.error("无权访问此会话");
        }

        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getChatId, chatId);
        Long count = messageMapper.selectCount(queryWrapper);
        return ResultVO.success(count);
    }

    @Override
    public ResultVO<Long> getTotalMessageCount() {
        Long userId = SecurityUtil.getCurrentUserId();
        LambdaQueryWrapper<MessageChat> chatQueryWrapper = new LambdaQueryWrapper<>();
        chatQueryWrapper.and(wrapper -> wrapper.eq(MessageChat::getUserId1, userId)
                .or()
                .eq(MessageChat::getUserId2, userId));
        List<MessageChat> chatList = messageChatMapper.selectList(chatQueryWrapper);

        Long totalCount = 0L;
        for (MessageChat chat : chatList) {
            LambdaQueryWrapper<Message> messageQueryWrapper = new LambdaQueryWrapper<>();
            messageQueryWrapper.eq(Message::getChatId, chat.getId());
            totalCount += messageMapper.selectCount(messageQueryWrapper);
        }

        return ResultVO.success(totalCount);
    }

    @Override
    public ResultVO<Long> getUnreadMessageCount() {
        Long userId = SecurityUtil.getCurrentUserId();
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getReceiverId, userId)
                .eq(Message::getIsRead, 0);
        Long count = messageMapper.selectCount(queryWrapper);
        return ResultVO.success(count);
    }

    @Override
    @Transactional
    public ResultVO<Long> getChatId(ChatIdQueryDTO queryDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        Long otherUserId = queryDTO.getOtherUserId();

        if (otherUserId.equals(userId)) {
            return ResultVO.error("不能与自己聊天");
        }

        Long userId1 = Math.min(userId, otherUserId);
        Long userId2 = Math.max(userId, otherUserId);

        LambdaQueryWrapper<MessageChat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MessageChat::getUserId1, userId1)
                .eq(MessageChat::getUserId2, userId2);
        MessageChat chat = messageChatMapper.selectOne(queryWrapper);

        if (chat == null) {
            chat = new MessageChat();
            chat.setUserId1(userId1);
            chat.setUserId2(userId2);
            messageChatMapper.insert(chat);
        }

        return ResultVO.success(chat.getId());
    }


    /**
     * 发送消息
     * @param sendMessageDTO 发送消息参数
     * @return 响应结果
     */
    @Override
    @Transactional
    public ResultVO<Long> sendMessage(SendMessageDTO sendMessageDTO) {
        Long senderId = SecurityUtil.getCurrentUserId();
        Long receiverId = sendMessageDTO.getReceiverId();
        String content = sendMessageDTO.getContent();

        if (receiverId.equals(senderId)) {
            return ResultVO.error(ResultCodeEnum.MESSAGE_CANNOT_SEND_TO_SELF);
        }

        User receiver = userMapper.selectById(receiverId);
        if (receiver == null) {
            return ResultVO.error(ResultCodeEnum.MESSAGE_RECEIVER_NOT_FOUND);
        }

        Long userId1 = Math.min(senderId, receiverId);
        Long userId2 = Math.max(senderId, receiverId);

        LambdaQueryWrapper<MessageChat> chatQueryWrapper = new LambdaQueryWrapper<>();
        chatQueryWrapper.eq(MessageChat::getUserId1, userId1)
                .eq(MessageChat::getUserId2, userId2);
        MessageChat chat = messageChatMapper.selectOne(chatQueryWrapper);

        if (chat == null) {
            chat = new MessageChat();
            chat.setUserId1(userId1);
            chat.setUserId2(userId2);
        }

        Message message = new Message();
        message.setChatId(chat.getId());
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setIsRead(0);
        messageMapper.insert(message);

        chat.setLastMsgId(message.getId());
        chat.setLastMsgContent(content);
        messageChatMapper.updateById(chat);


        // 尝试推送消息，失败则直接返回成功响应
        try {
            MessageVO messageVO = buildMessageVO(message);
            ResultVO<MessageVO> pushResult = ResultVO.success(ResultCodeEnum.SUCCESS, messageVO);
            String messageJson = objectMapper.writeValueAsString(pushResult);
            MessageWebSocketHandler.sendMessageToUser(receiverId, messageJson);
        } catch (Exception e) {

        }

        return ResultVO.success(ResultCodeEnum.SUCCESS, message.getId());
    }

    private MessageVO buildMessageVO(Message message) {
        MessageVO messageVO = new MessageVO();
        BeanUtils.copyProperties(message, messageVO);

        User sender = userMapper.selectById(message.getSenderId());
        if (sender != null) {

            messageVO.setSenderNickname(sender.getNickname());
            messageVO.setSenderAvatar(sender.getAvatar());
        }

        User receiver = userMapper.selectById(message.getReceiverId());
        if (receiver != null) {

            messageVO.setReceiverNickname(receiver.getNickname());
            messageVO.setReceiverAvatar(receiver.getAvatar());
        }

        return messageVO;
    }
}
