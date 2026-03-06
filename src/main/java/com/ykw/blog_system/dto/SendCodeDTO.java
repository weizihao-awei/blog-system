package com.ykw.blog_system.dto;

import com.ykw.blog_system.enums.VerificationCodeType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发送验证码DTO
 */
@Data
public class SendCodeDTO {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 验证码类型：REGISTER-注册，RESET-重置密码
     */
    @NotNull(message = "验证码类型不能为空")
    private VerificationCodeType type;
}
