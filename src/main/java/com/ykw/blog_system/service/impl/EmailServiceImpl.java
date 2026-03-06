package com.ykw.blog_system.service.impl;

import com.ykw.blog_system.service.EmailService;
import com.ykw.blog_system.vo.ResultVO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * 邮件服务实现类
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public ResultVO<Void> sendVerificationCode(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("【博客系统】邮箱验证码");

            String content = buildVerificationCodeEmail(code);
            helper.setText(content, true);

            mailSender.send(message);
            log.info("验证码邮件已发送至: {}", toEmail);
            return ResultVO.success();
        } catch (MessagingException e) {
            log.error("发送验证码邮件失败: {}", e.getMessage());
            return ResultVO.error("发送邮件失败，请稍后重试");
        }
    }

    @Override
    public ResultVO<Void> sendPasswordResetSuccess(String toEmail, String username) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("【博客系统】密码重置成功");

            String content = buildPasswordResetSuccessEmail(username);
            helper.setText(content, true);

            mailSender.send(message);
            log.info("密码重置成功邮件已发送至: {}", toEmail);
            return ResultVO.success();
        } catch (MessagingException e) {
            log.error("发送密码重置成功邮件失败: {}", e.getMessage());
            return ResultVO.error("发送邮件失败");
        }
    }

    /**
     * 构建验证码邮件内容
     */
    private String buildVerificationCodeEmail(String code) {
        return "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 5px;'>" +
                "<h2 style='color: #333; text-align: center;'>邮箱验证码</h2>" +
                "<p style='color: #666; font-size: 14px;'>您好，</p>" +
                "<p style='color: #666; font-size: 14px;'>您的验证码为：</p>" +
                "<div style='background-color: #f5f5f5; padding: 15px; text-align: center; margin: 20px 0; border-radius: 5px;'>" +
                "<span style='font-size: 28px; font-weight: bold; color: #1890ff; letter-spacing: 5px;'>" + code + "</span>" +
                "</div>" +
                "<p style='color: #666; font-size: 14px;'>此验证码有效期为 <strong>5分钟</strong>，请勿泄露给他人。</p>" +
                "<p style='color: #999; font-size: 12px; margin-top: 30px;'>如非本人操作，请忽略此邮件。</p>" +
                "<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 20px 0;'>" +
                "<p style='color: #999; font-size: 12px; text-align: center;'>博客系统</p>" +
                "</div>";
    }

    /**
     * 构建密码重置成功邮件内容
     */
    private String buildPasswordResetSuccessEmail(String username) {
        return "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 5px;'>" +
                "<h2 style='color: #333; text-align: center;'>密码重置成功</h2>" +
                "<p style='color: #666; font-size: 14px;'>您好，" + username + "</p>" +
                "<p style='color: #666; font-size: 14px;'>您的密码已成功重置。如果这不是您本人的操作，请立即联系管理员。</p>" +
                "<div style='background-color: #f6ffed; padding: 15px; border-left: 4px solid #52c41a; margin: 20px 0;'>" +
                "<p style='color: #52c41a; margin: 0;'>密码重置成功</p>" +
                "</div>" +
                "<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 20px 0;'>" +
                "<p style='color: #999; font-size: 12px; text-align: center;'>博客系统</p>" +
                "</div>";
    }
}
