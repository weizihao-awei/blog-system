package com.ykw.blog_system.service.impl;

import com.ykw.blog_system.dto.LoginDTO;
import com.ykw.blog_system.dto.RegisterDTO;
import com.ykw.blog_system.dto.ResetPasswordDTO;
import com.ykw.blog_system.dto.SendCodeDTO;
import com.ykw.blog_system.entity.User;
import com.ykw.blog_system.enums.VerificationCodeType;
import com.ykw.blog_system.mapper.UserMapper;
import com.ykw.blog_system.service.AuthService;
import com.ykw.blog_system.service.EmailService;
import com.ykw.blog_system.utils.JwtUtil;
import com.ykw.blog_system.utils.VerificationCodeCache;
import com.ykw.blog_system.vo.LoginVO;
import com.ykw.blog_system.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private VerificationCodeCache verificationCodeCache;

    @Autowired
    private EmailService emailService;
    
    @Override
    public ResultVO<LoginVO> login(LoginDTO loginDTO) {
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            return ResultVO.error("用户名或密码错误");
        }
        
        if (user.getStatus() == 0) {
            return ResultVO.error("账号已被禁用");
        }
        
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return ResultVO.error("用户名或密码错误");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setTokenType("Bearer");
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        loginVO.setAvatar(user.getAvatar());
        loginVO.setRole(user.getRole());
        
        return ResultVO.success("登录成功", loginVO);
    }
    
    @Override
    @Transactional
    public ResultVO<Void> register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (userMapper.selectByUsername(registerDTO.getUsername()) != null) {
            return ResultVO.error("用户名已存在，注册失败");
        }

        // 检查邮箱是否已存在
        if (userMapper.selectByEmail(registerDTO.getEmail()) != null) {
            return ResultVO.error("邮箱已被注册");
        }

        // 检查两次密码是否一致
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            return ResultVO.error("两次输入的密码不一致");
        }

        // 校验验证码
        int verifyResult = verificationCodeCache.verifyCode(registerDTO.getEmail(), registerDTO.getVerificationCode());
        if (verifyResult == 3) {
            return ResultVO.error("验证码不存在，请先获取验证码");
        }
        if (verifyResult == 2) {
            return ResultVO.error("验证码已过期，请重新获取");
        }
        if (verifyResult == 1) {
            return ResultVO.error("验证码错误");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setNickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setRole(0);
        user.setStatus(1);

        userMapper.insert(user);

        // 注册成功后清除验证码
        verificationCodeCache.removeCode(registerDTO.getEmail());

        return ResultVO.success("注册成功", null);
    }

    @Override
    public ResultVO<Void> sendVerificationCode(SendCodeDTO sendCodeDTO) {
        String email = sendCodeDTO.getEmail();
        VerificationCodeType type = sendCodeDTO.getType();

        // 根据类型进行不同的校验
        if (type == VerificationCodeType.REGISTER) {
            // 注册时检查邮箱是否已被注册
            if (userMapper.selectByEmail(email) != null) {
                return ResultVO.error("邮箱已被注册");
            }
        } else if (type == VerificationCodeType.RESET) {
            // 重置密码时检查邮箱是否存在
            if (userMapper.selectByEmail(email) == null) {
                return ResultVO.error("该邮箱未注册");
            }
        }

        // 检查发送频率限制
        if (!verificationCodeCache.canSend(email)) {
            long remainingTime = verificationCodeCache.getRemainingWaitTime(email);
            return ResultVO.error(429, "发送过于频繁，请" + remainingTime + "秒后再试");
        }

        // 生成验证码
        String code = verificationCodeCache.generateCode();

        // 发送邮件
        ResultVO<Void> sendResult = emailService.sendVerificationCode(email, code);
        if (sendResult.getCode() != 200) {
            return sendResult;
        }

        // 保存验证码到缓存
        verificationCodeCache.saveCode(email, code);

        log.info("验证码已发送至邮箱: {}, 类型: {}", email, type);
        return ResultVO.success("验证码已发送，请查收邮件", null);
    }

    @Override
    @Transactional
    public ResultVO<Void> resetPassword(ResetPasswordDTO resetPasswordDTO) {
        // 检查两次密码是否一致
        if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getConfirmPassword())) {
            return ResultVO.error("两次输入的密码不一致");
        }

        // 检查邮箱是否存在
        User user = userMapper.selectByEmail(resetPasswordDTO.getEmail());
        if (user == null) {
            return ResultVO.error("该邮箱未注册");
        }

        // 校验验证码
        int verifyResult = verificationCodeCache.verifyCode(resetPasswordDTO.getEmail(), resetPasswordDTO.getVerificationCode());
        if (verifyResult == 3) {
            return ResultVO.error("验证码不存在，请先获取验证码");
        }
        if (verifyResult == 2) {
            return ResultVO.error("验证码已过期，请重新获取");
        }
        if (verifyResult == 1) {
            return ResultVO.error("验证码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        userMapper.update(user);

        // 清除验证码
        verificationCodeCache.removeCode(resetPasswordDTO.getEmail());

        // 发送密码重置成功邮件
        emailService.sendPasswordResetSuccess(user.getEmail(), user.getUsername());

        log.info("用户密码重置成功: {}", user.getUsername());
        return ResultVO.success("密码重置成功", null);
    }
}
