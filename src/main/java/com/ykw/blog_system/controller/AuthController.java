package com.ykw.blog_system.controller;

import com.ykw.blog_system.dto.LoginDTO;
import com.ykw.blog_system.dto.RegisterDTO;
import com.ykw.blog_system.dto.ResetPasswordDTO;
import com.ykw.blog_system.dto.SendCodeDTO;
import com.ykw.blog_system.service.AuthService;
import com.ykw.blog_system.vo.LoginVO;
import com.ykw.blog_system.vo.ResultVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResultVO<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResultVO<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        return authService.register(registerDTO);
    }

    /**
     * 发送验证码
     * type: register-注册，reset-重置密码
     */
    @PostMapping("/send-code")
    public ResultVO<Void> sendVerificationCode(@Valid @RequestBody SendCodeDTO sendCodeDTO) {
        return authService.sendVerificationCode(sendCodeDTO);
    }

    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public ResultVO<Void> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        return authService.resetPassword(resetPasswordDTO);
    }
}
