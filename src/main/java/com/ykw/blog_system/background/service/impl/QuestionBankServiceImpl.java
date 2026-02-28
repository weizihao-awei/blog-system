package com.ykw.blog_system.background.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ykw.blog_system.background.mapper.QuestionBankMapper;
import com.ykw.blog_system.background.pojo.Dto.QuestionBankDTO;
import com.ykw.blog_system.background.pojo.Dto.QuestionBankQueryDTO;
import com.ykw.blog_system.background.pojo.Entity.QuestionBank;
import com.ykw.blog_system.background.pojo.Vo.PageQustionBankVO;
import com.ykw.blog_system.background.pojo.Vo.QuestionBankVO;
import com.ykw.blog_system.background.service.QuestionBankService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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

    /**
     * MP 分页查询核心方法
     */
@Override
public PageQustionBankVO pageQuery(QuestionBankQueryDTO queryDTO) {
    // 1. 构建分页对象（默认第1页，每页10条）
    Page<QuestionBank> page = new Page<>(
            queryDTO.getPageNum() == null ? 1L : queryDTO.getPageNum(),
            queryDTO.getPageSize() == null ? 10L : queryDTO.getPageSize()
    );

    // 2. 构建查询条件（与之前一致）
    LambdaQueryWrapper<QuestionBank> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(QuestionBank::getDataState, 1); // 过滤逻辑删除
    if (StringUtils.hasText(queryDTO.getCategoryName())) {
        wrapper.eq(QuestionBank::getCategoryName, queryDTO.getCategoryName());
    }
    if (StringUtils.hasText(queryDTO.getBankName())) {
        wrapper.like(QuestionBank::getBankName, queryDTO.getBankName());
    }

    // 3. 执行分页查询（获取Entity分页结果）
    IPage<QuestionBank> entityPage = this.page(page, wrapper);

    // 4. 转换为VO分页结果（核心：只保留需要的字段）
    IPage<QuestionBankVO> voPage = entityPage.convert(entity -> {
        QuestionBankVO vo = new QuestionBankVO();
        // 字段映射（仅复制VO中存在的字段）
        BeanUtils.copyProperties(entity, vo);

        // 临时适配：题目数（若表中无该字段，可先写死，后续替换为关联查询）
        // todo: 实际场景：需关联题目表，统计该题库下的题目数量
        vo.setQuestionCount(11);

        return vo;
    });

    // 5. 封装PageQustionBankVO对象并返回
    PageQustionBankVO result = new PageQustionBankVO();
    result.setRecords(voPage.getRecords());
    result.setTotal(voPage.getTotal());


    return result;
}


}