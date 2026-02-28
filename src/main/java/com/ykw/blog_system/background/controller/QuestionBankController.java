package com.ykw.blog_system.background.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ykw.blog_system.background.pojo.Dto.QuestionBankDTO;
import com.ykw.blog_system.background.pojo.Dto.QuestionBankDeleteDTO;
import com.ykw.blog_system.background.pojo.Dto.QuestionBankQueryDTO;
import com.ykw.blog_system.background.pojo.Entity.QuestionBank;
import com.ykw.blog_system.background.pojo.Result;
import com.ykw.blog_system.background.pojo.Vo.PageQustionBankVO;
import com.ykw.blog_system.background.pojo.Vo.QuestionBankVO;
import com.ykw.blog_system.background.service.QuestionBankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger logger = LoggerFactory.getLogger(QuestionBankController.class);

    @Autowired
    private QuestionBankService questionBankService;




    /**
     * 题库分页查询接口（返回VO）
     */
    @PostMapping("/page-query")
    public Result pageQuery(@RequestBody(required = false) QuestionBankQueryDTO queryDTO) {
        logger.info("分页查询接口：{}", queryDTO);
        if (queryDTO == null) {
            queryDTO = new QuestionBankQueryDTO();
        }
        // 调用改造后的方法，获取VO分页结果
        PageQustionBankVO pageResult = questionBankService.pageQuery(queryDTO);
        return Result.success("分页查询成功", pageResult);
    }

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
