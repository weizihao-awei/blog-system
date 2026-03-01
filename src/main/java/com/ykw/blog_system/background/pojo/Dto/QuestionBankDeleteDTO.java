package com.ykw.blog_system.background.pojo.Dto;

import lombok.Data;

import java.util.List;

/**
 * 题库删除请求DTO
 */
@Data
public class QuestionBankDeleteDTO {
    /**
     * 要删除的题库ID列表（必填）
     */

    private List<Long> ids;
}