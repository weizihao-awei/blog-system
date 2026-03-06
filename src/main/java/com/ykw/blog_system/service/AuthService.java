package com.ykw.blog_system.service;

import com.ykw.blog_system.dto.LoginDTO;
import com.ykw.blog_system.dto.RegisterDTO;
import com.ykw.blog_system.dto.ResetPasswordDTO;
import com.ykw.blog_system.dto.SendCodeDTO;
import com.ykw.blog_system.vo.LoginVO;
import com.ykw.blog_system.vo.ResultVO;

/**
 * 认证服务接口
 */
public interface AuthService {

    ResultVO<LoginVO> login(LoginDTO loginDTO);

    ResultVO<Void> register(RegisterDTO registerDTO);

    /**
     * 发送验证码
     *
     * @param sendCodeDTO 发送验证码DTO
     * @return 发送结果
     */
    ResultVO<Void> sendVerificationCode(SendCodeDTO sendCodeDTO);

    /**
     * 重置密码
     *
     * @param resetPasswordDTO 重置密码DTO
     * @return 重置结果
     */
    ResultVO<Void> resetPassword(ResetPasswordDTO resetPasswordDTO);
}
