package com.ykw.blog_system.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ykw.blog_system.entity.User;
import com.ykw.blog_system.enums.ResultCodeEnum;
import com.ykw.blog_system.mapper.UserMapper;
import com.ykw.blog_system.utils.JwtUtil;
import com.ykw.blog_system.vo.ResultVO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * WebSocket JWT 认证过滤器
 * 专门处理 WebSocket 请求中的 Token 验证（从 URI 参数中提取）
 */
@Component
public class WebSocketJwtFilter extends OncePerRequestFilter {
    
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(WebSocketJwtFilter.class);
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        // 只处理 WebSocket 请求
        String path = request.getRequestURI();
        if (!path.startsWith("/ws/")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        logger.info("WebSocket 请求：{}", path);
        
        // 从 URI 参数中获取 token
        String token = getTokenFromUri(request);
        
        if (!StringUtils.hasText(token)) {
            logger.warn("WebSocket 请求未携带 Token: {}", path);
            sendErrorResponse(response, ResultCodeEnum.UNAUTHORIZED);
            return;
        }
        
        // 验证 token
        ResultCodeEnum resultCodeEnum = jwtUtil.validateToken(token);
        if (!resultCodeEnum.isSuccess()) {
            logger.info("WebSocket Token 验证失败：{}", resultCodeEnum.getMessage());
            sendErrorResponse(response, resultCodeEnum);
            return;
        }
        
        // 获取用户信息并设置到 SecurityContext
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userMapper.selectById(userId);
        logger.info("WebSocket Token 验证成功，用户 ID: {}", userId);
        
        if (user != null && user.getStatus() == 1) {
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("WebSocket 用户认证成功：{}", user.getUsername());
        } else {
            logger.warn("WebSocket 用户不存在或已被禁用：{}", userId);
            sendErrorResponse(response, ResultCodeEnum.UNAUTHORIZED);
            return;
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * 从 URI 参数中获取 Token
     */
    private String getTokenFromUri(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString != null && queryString.contains("token=")) {
            String[] params = queryString.split("&");
            for (String param : params) {
                if (param.startsWith("token=")) {
                    return param.substring(6);
                }
            }
        }
        return null;
    }
    
    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, ResultCodeEnum resultCodeEnum) 
            throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(ResultVO.error(resultCodeEnum)));
    }
}
