package com.ykw.blog_system.background.controller;

import com.ykw.blog_system.background.pojo.Dto.QuestionBankDTO;
import com.ykw.blog_system.background.pojo.Dto.QuestionBankDeleteDTO;
import com.ykw.blog_system.background.pojo.Result;
import com.ykw.blog_system.background.service.QuestionBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 题库Controller（统一返回Result格式）
 */
@RestController
@RequestMapping("/api/v1/question-bank")
public class QuestionBankController {

    @Autowired
    private QuestionBankService questionBankService;

    /**
     * 新增题库接口
     */
    @PostMapping("/add")
    public Result add(@Validated @RequestBody QuestionBankDTO dto) {
        boolean success = questionBankService.addQuestionBank(dto);
        if (success) {
            return Result.success("新增题库成功");
        } else {
            return Result.error("新增题库失败");
        }
    }

    /**
     * 更新题库接口
     */
    @PostMapping("/update")
    public Result update(@Validated @RequestBody QuestionBankDTO dto) {
        boolean success = questionBankService.updateQuestionBank(dto);
        if (success) {
            return Result.success("更新题库成功");
        } else {
            return Result.error("更新题库失败");
        }
    }

    /**
     * 删除题库接口（逻辑删除）
     */
    @PostMapping("/delete")
    public Result delete(@Validated @RequestBody QuestionBankDeleteDTO dto) {
        boolean success = questionBankService.deleteQuestionBank(dto.getIds());
        if (success) {
            return Result.success("删除题库成功");
        } else {
            return Result.error("删除题库失败");
        }
    }
}
