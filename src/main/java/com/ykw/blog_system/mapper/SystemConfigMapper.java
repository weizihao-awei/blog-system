package com.ykw.blog_system.mapper;

import com.ykw.blog_system.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 系统配置Mapper接口
 */
@Mapper
public interface SystemConfigMapper {
    
    SystemConfig selectById(Long id);
    
    SystemConfig selectByKey(String configKey);
    
    int insert(SystemConfig systemConfig);
    
    int update(SystemConfig systemConfig);
    
    int deleteById(Long id);
    
    List<SystemConfig> selectAll();
}
