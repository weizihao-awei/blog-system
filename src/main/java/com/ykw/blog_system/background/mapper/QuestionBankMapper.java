package com.ykw.blog_system.background.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.ykw.blog_system.background.pojo.Entity.QuestionBank;
import org.apache.ibatis.annotations.Mapper;

/**
 * 题库Mapper接口（MyBatis-Plus 自动实现CRUD）
 */
@Mapper
public interface QuestionBankMapper extends BaseMapper<QuestionBank> {
    // 无需编写任何方法，BaseMapper 已包含所有基础CRUD
}