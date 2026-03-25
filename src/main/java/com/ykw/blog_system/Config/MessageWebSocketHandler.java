package com.ykw.blog_system.Config;
import com.ykw.blog_system.mapper.MessageChatMapper;
import com.ykw.blog_system.mapper.MessageMapper;
import com.ykw.blog_system.mapper.UserMapper;
import org.slf4j.Logger;
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




}
