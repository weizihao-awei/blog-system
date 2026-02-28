package com.ykw.blog_system.background.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ykw.blog_system.background.mapper.QuestionBankMapper;
import com.ykw.blog_system.background.pojo.Dto.QuestionBankDTO;
import com.ykw.blog_system.background.pojo.Entity.QuestionBank;
import com.ykw.blog_system.background.service.QuestionBankService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 题库Service实现类（复用MyBatis-Plus的方法）
 */
@Service
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank> implements QuestionBankService {

    /**
     * 新增题库
     */
    @Override
    public boolean addQuestionBank(QuestionBankDTO dto) {
        // 1. DTO 转 实体类
        QuestionBank questionBank = new QuestionBank();
        BeanUtils.copyProperties(dto, questionBank);
        
        // 2. 冗余字段categoryId置空（按要求无需前端传入）
        questionBank.setCategoryId(null);
        
        // 3. 调用MyBatis-Plus的save方法新增
        return save(questionBank);
    }

    /**
     * 更新题库
     */
    @Override
    public boolean updateQuestionBank(QuestionBankDTO dto) {
        // 1. 校验ID（更新必须传ID）
        if (dto.getId() == null) {
            throw new IllegalArgumentException("更新题库时ID不能为空");
        }
        
        // 2. 构建更新条件（仅更新指定ID的记录）
        LambdaUpdateWrapper<QuestionBank> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(QuestionBank::getId, dto.getId());
        
        // 3. 封装更新参数（冗余字段categoryId不更新）
        QuestionBank questionBank = new QuestionBank();
        BeanUtils.copyProperties(dto, questionBank);
        questionBank.setCategoryId(null); // 确保冗余字段不被更新
        
        // 4. 调用MyBatis-Plus的update方法
        return update(questionBank, updateWrapper);
    }

    /**
     * 逻辑删除题库
     */
    @Override
    public boolean deleteQuestionBank(List<Long> ids) {
        // 1. 校验ID列表
        if (CollectionUtils.isEmpty(ids)) {
            throw new IllegalArgumentException("删除题库时ID列表不能为空");
        }
        
        // 2. 调用MyBatis-Plus的removeByIds方法（自动触发逻辑删除）
        return removeByIds(ids);
    }
}