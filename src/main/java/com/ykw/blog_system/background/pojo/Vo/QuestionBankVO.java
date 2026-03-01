package com.ykw.blog_system.background.pojo.Vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 题库分页查询返回VO（仅包含前端需要的字段）
 */
@Data
public class QuestionBankVO {
    /** 题库名称 */
    private String bankName;
    /** 描述（对应实体的bankDesc） */
    private String bankDesc;
    /** 分类（对应实体的categoryName） */
    private String categoryName;
    /** 题目数（若表中无该字段，可后续通过关联查询补充） */
    private Integer questionCount;
    /** 难度（存储难度等级，前端可转义为“一级/简单”等） */
    private Integer difficultyLevel;
    /** 创建时间 */
    private LocalDateTime createTime;
}