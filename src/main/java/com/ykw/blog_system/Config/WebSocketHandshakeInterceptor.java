package com.ykw.blog_system.Config;

import com.ykw.blog_system.entity.User;
import org.slf4j.Logger;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {
    
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(WebSocketHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                    WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        log.info("WebSocket握手请求：{}", request.getURI());
        
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                
                if (principal instanceof User) {
                    User user = (User) principal;
                    attributes.put("userId", user.getId());
                    attributes.put("user", user);
                    log.info("WebSocket握手成功，用户ID：{}", user.getId());
                    return true;
                }
            }
            
            log.warn("WebSocket握手失败：未找到认证用户");
            return false;
        }
        
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("WebSocket握手异常", exception);
        }
    }
}
