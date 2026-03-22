package com.ykw.blog_system.dto;

import lombok.Data;

@Data
public class MessageListQueryDTO {

    private Long chatId;

    private Integer pageNum;

    private Integer pageSize;
}
