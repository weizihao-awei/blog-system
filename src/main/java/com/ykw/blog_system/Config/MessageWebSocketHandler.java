package com.ykw.blog_system.Config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ykw.blog_system.entity.Message;
import com.ykw.blog_system.entity.MessageChat;
import com.ykw.blog_system.entity.User;
import com.ykw.blog_system.enums.ResultCodeEnum;
import com.ykw.blog_system.mapper.MessageChatMapper;
import com.ykw.blog_system.mapper.MessageMapper;
import com.ykw.blog_system.mapper.UserMapper;
import com.ykw.blog_system.utils.JwtUtil;
import com.ykw.blog_system.vo.MessageVO;
import com.ykw.blog_system.vo.ResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MessageChatMapper messageChatMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.put(userId, session);
        } else {
            session.close();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long userId = getUserIdFromSession(session);
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
        messageChatMapper.updateById(chat);

        MessageVO messageVO = convertToMessageVO(msg);
        ResultVO<MessageVO> resultVO = ResultVO.success(messageVO);
        String response = objectMapper.writeValueAsString(resultVO);

        WebSocketSession receiverSession = userSessions.get(receiverId);
        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(response));
        }

        session.sendMessage(new TextMessage(response));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.remove(userId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.remove(userId);
        }
        if (session.isOpen()) {
            session.close();
        }
    }

    public static boolean isUserOnline(Long userId) {
        WebSocketSession session = userSessions.get(userId);
        return session != null && session.isOpen();
    }

    public static void sendMessageToUser(Long userId, String message) throws IOException {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }

    private Long getUserIdFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null && query.contains("token=")) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("token=")) {
                    try {
                        String token = param.substring(6);
                        ResultCodeEnum resultCode = jwtUtil.validateToken(token);
                        if (resultCode == ResultCodeEnum.SUCCESS) {
                            return jwtUtil.getUserIdFromToken(token);
                        }
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    private MessageVO convertToMessageVO(Message message) {
        MessageVO messageVO = new MessageVO();
        BeanUtils.copyProperties(message, messageVO);

        User sender = userMapper.selectById(message.getSenderId());
        if (sender != null) {
            messageVO.setSenderUsername(sender.getUsername());
            messageVO.setSenderNickname(sender.getNickname());
            messageVO.setSenderAvatar(sender.getAvatar());
        }

        User receiver = userMapper.selectById(message.getReceiverId());
        if (receiver != null) {
            messageVO.setReceiverUsername(receiver.getUsername());
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
