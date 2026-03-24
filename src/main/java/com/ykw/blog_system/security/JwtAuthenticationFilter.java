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
 * JWT认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    //添加日志对象
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        if (!StringUtils.hasText(token)) {
            logger.info("请求未携带Token");
            filterChain.doFilter(request, response);
            return;
        }
        ResultCodeEnum resultCodeEnum = jwtUtil.validateToken(token);
        if (!resultCodeEnum.isSuccess()) {
            logger.info("Token验证失败: {}", resultCodeEnum.getMessage());
            //用httpServletResponse返回错误信息
            // 2. 设置响应头和状态码
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401
            ObjectMapper objectMapper = new ObjectMapper();
            // 4. 写入响应
            response.getWriter().write(objectMapper.writeValueAsString(ResultVO.error(resultCodeEnum)));
            return;


        }
        

        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userMapper.selectById(userId);
        logger.info(" Token 获取的用户信息 - ID: {}, 昵称：{}", userId, user != null ? user.getNickname() : "未知用户");

        if (user != null && user.getStatus() == 1) {
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        
        filterChain.doFilter(request, response);
    }
    
    /**
     * 从请求中获取 Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 从 Authorization Header 中获取
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
