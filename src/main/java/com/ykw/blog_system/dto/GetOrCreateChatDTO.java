package com.ykw.blog_system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetOrCreateChatDTO {

    @NotNull(message = "对方用户ID不能为空")
    private Long otherUserId;
}
