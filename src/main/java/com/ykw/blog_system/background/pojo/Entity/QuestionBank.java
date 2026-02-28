package com.ykw.blog_system.background.pojo.Entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 题库信息实体类
 */
@Data
@TableName("question_bank")
public class QuestionBank {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 题库名称
     */
    private String bankName;

    /**
     * 题库描述
     */
    private String bankDesc;

    /**
     * 所属分类名称
     */
    private String categoryName;

    /**
     * 所属分类ID（冗余字段，无需前端传入）
     */
    private Long categoryId;

    /**
     * 难度等级：1-简单，2-中等，3-困难
     */
    private Integer difficultyLevel;

    /**
     * 启用状态：1-启用，0-禁用
     */
    private Integer status = 1;

    /**
     * 创建时间（自动填充）
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 创建人ID
     */
    private Long createrId;

    /**
     * 创建人姓名
     */
    private String createrName;

    /**
     * 修改时间（自动填充）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime modifyTime;

    /**
     * 修改人ID
     */
    private Long modifierId;

    /**
     * 修改人姓名
     */
    private String modifierName;

    /**
     * 数据状态：1-正常，0-逻辑删除（MyBatis-Plus 逻辑删除字段）
     */
    @TableLogic
    private Integer dataState = 1;
}