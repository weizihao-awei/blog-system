package com.ykw.blog_system.background.pojo.Dto;


import lombok.Data;

/**
 * 题库新增/更新请求DTO
 */
@Data
public class QuestionBankDTO {
    /**
     * 主键ID（更新时必填，新增时无需传）
     */
    private Long id;

    /**
     * 题库名称（必填）
     */

    private String bankName;

    /**
     * 题库描述
     */
    private String bankDesc;

    /**
     * 所属分类名称（必填）
     */

    private String categoryName;

    /**
     * 所属分类ID（冗余字段，前端传了也不处理）
     */
    private Long categoryId;

    /**
     * 难度等级：1-简单，2-中等，3-困难（必填）
     */

    private Integer difficultyLevel;
}
