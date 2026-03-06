package com.ykw.blog_system.service;

import com.ykw.blog_system.vo.ResultVO;

/**
 * 邮件服务接口
 */
public interface EmailService {

    /**
     * 发送验证码邮件
     *
     * @param toEmail 收件人邮箱
     * @param code    验证码
     * @return 发送结果
     */
    ResultVO<Void> sendVerificationCode(String toEmail, String code);

    /**
     * 发送密码重置成功邮件
     *
     * @param toEmail  收件人邮箱
     * @param username 用户名
     * @return 发送结果
     */
    ResultVO<Void> sendPasswordResetSuccess(String toEmail, String username);
}
