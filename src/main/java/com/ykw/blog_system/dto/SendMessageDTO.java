package com.ykw.blog_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageDTO {
    @NotNull(message = "会话id不能为空")
    private Long chatId;

    @NotNull(message = "接收者ID不能为空")
    private Long receiverId;

    @NotBlank(message = "消息内容不能为空")
    private String content;
}
