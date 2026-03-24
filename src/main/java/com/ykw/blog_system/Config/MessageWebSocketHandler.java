package com.ykw.blog_system.Config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ykw.blog_system.entity.Message;
import com.ykw.blog_system.entity.MessageChat;
import com.ykw.blog_system.entity.User;
import com.ykw.blog_system.mapper.MessageChatMapper;
import com.ykw.blog_system.mapper.MessageMapper;
import com.ykw.blog_system.mapper.UserMapper;
import com.ykw.blog_system.vo.MessageVO;
import com.ykw.blog_system.vo.ResultVO;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MessageWebSocketHandler extends TextWebSocketHandler {
    Logger log = org.slf4j.LoggerFactory.getLogger(MessageWebSocketHandler.class);

    private static final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MessageChatMapper messageChatMapper;


    /**
     * 当 WebSocket 连接建立成功后调用。
     * 从会话的principal中获取用户信息，若获取成功则将会话存入在线用户映射表，
     * 否则关闭当前连接以拒绝未授权的访问。
     *
     * @param session 建立的 WebSocket 会话
     * @throws Exception 处理连接时可能抛出的异常
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("连接成功");
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            log.info("用户连接成功：{}", userId);
            userSessions.put(userId, session);
        } else {
            log.warn("用户连接失败：未找到用户ID");
            session.close();
        }
    }
    /**
     * 处理接收到的 WebSocket 消息。
     * 从会话的principal中获取用户信息，若获取成功则解析消息内容，
     * 创建消息对象并保存到数据库中，并返回结果给发送方。
     *
     * @param session 当前 WebSocket 会话
     * @param message 接收到的 WebSocket 消息
     * @throws Exception 处理消息时可能抛出的异常
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId == null) {
            session.close();
            return;
        }

        String payload = message.getPayload();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> messageData = objectMapper.readValue(payload, Map.class);

        Long receiverId = Long.valueOf(messageData.get("receiverId").toString());
        String content = (String) messageData.get("content");

        if (receiverId.equals(userId)) {
            sendErrorMessage(session, "不能给自己发送消息");
            return;
        }

        User receiver = userMapper.selectById(receiverId);
        if (receiver == null) {
            sendErrorMessage(session, "接收者不存在");
            return;
        }

        Long userId1 = Math.min(userId, receiverId);
        Long userId2 = Math.max(userId, receiverId);

        LambdaQueryWrapper<MessageChat> chatQueryWrapper = new LambdaQueryWrapper<>();
        chatQueryWrapper.eq(MessageChat::getUserId1, userId1)
                .eq(MessageChat::getUserId2, userId2);
        MessageChat chat = messageChatMapper.selectOne(chatQueryWrapper);

        if (chat == null) {
            chat = new MessageChat();
            chat.setUserId1(userId1);
            chat.setUserId2(userId2);
            messageChatMapper.insert(chat);

        }

        Message msg = new Message();
        msg.setChatId(chat.getId());
        msg.setSenderId(userId);
        msg.setReceiverId(receiverId);
        msg.setContent(content);
        msg.setIsRead(0);
        messageMapper.insert(msg);

        chat.setLastMsgId(msg.getId());
        chat.setLastMsgContent(content);
        messageChatMapper.updateById(chat);

        MessageVO messageVO = convertToMessageVO(msg);
        String response = objectMapper.writeValueAsString(messageVO);
        // 发送消息给接收者
        WebSocketSession receiverSession = userSessions.get(receiverId);
        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(response));
        }
        // 返回给发送方
        session.sendMessage(new TextMessage(response));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            userSessions.remove(userId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            userSessions.remove(userId);
        }
        if (session.isOpen()) {
            session.close();
        }
    }

    /**
     * 判断指定用户是否在线
     *
     * @param userId 用户 ID
     * @return 如果用户在线且会话已打开则返回 true，否则返回 false
     */
    public static boolean isUserOnline(Long userId) {
        WebSocketSession session = userSessions.get(userId);
        return session != null && session.isOpen();
    }

    /**
     * 向指定用户发送消息
     *
     * @param userId  接收消息的用户 ID
     * @param message 要发送的消息内容
     * @throws IOException 如果发送消息时发生 IO 异常
     */
    public static void sendMessageToUser(Long userId, String message) throws IOException {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }

    private MessageVO convertToMessageVO(Message message) {
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

    private void sendErrorMessage(WebSocketSession session, String errorMessage) throws IOException {
        ResultVO<Void> resultVO = ResultVO.error(errorMessage);
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(resultVO);
        session.sendMessage(new TextMessage(response));
    }
}
