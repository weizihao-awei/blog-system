package com.ykw.blog_system.background.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ykw.blog_system.background.pojo.Dto.QuestionBankDTO;
import com.ykw.blog_system.background.pojo.Dto.QuestionBankQueryDTO;
import com.ykw.blog_system.background.pojo.Entity.QuestionBank;
import com.ykw.blog_system.background.pojo.Vo.PageQustionBankVO;
import com.ykw.blog_system.background.pojo.Vo.QuestionBankVO;


import java.util.List;

/**
 * 题库Service接口
 */
public interface QuestionBankService extends IService<QuestionBank> {

    /**
     * 新增题库
     * @param dto 新增请求DTO
     * @return 是否成功
     */
    boolean addQuestionBank(QuestionBankDTO dto);

    /**
     * 更新题库
     * @param dto 更新请求DTO
     * @return 是否成功
     */
    boolean updateQuestionBank(QuestionBankDTO dto);

    /**
     * 逻辑删除题库（支持单条/多条）
     * @param ids 要删除的ID列表
     * @return 是否成功
     */
    boolean deleteQuestionBank(List<Long> ids);

    /**
     * 分页查询接口
     * @param queryDTO 查询参数DTO
     * @return 分页结果
     */

    PageQustionBankVO pageQuery(QuestionBankQueryDTO queryDTO);
}