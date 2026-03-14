package com.ykw.blog_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ykw.blog_system.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类 Mapper 接口
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    // 所有单表查询方法已移除，直接使用 BaseMapper 提供的方法
}
