package com.ykw.blog_system.background.pojo.Dto;

import lombok.Data;

/**
 * 题库分页查询参数
 * 包含：查询条件（分类名、题库名） + 分页参数（页码、每页条数）
 */
@Data
public class QuestionBankQueryDTO {
    // 查询条件
    private String categoryName; // 分类名称（精准匹配，空则全选）
    private String bankName;     // 题库名称（模糊匹配，空则不筛）
    
    // 分页参数（默认：第1页，每页10条）
    private Long pageNum = 1L;   // 当前页码
    private Long pageSize = 10L; // 每页条数
}