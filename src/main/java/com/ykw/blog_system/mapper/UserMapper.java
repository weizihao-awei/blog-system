package com.ykw.blog_system.mapper;

import com.ykw.blog_system.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    
    User selectById(Long id);
    
    User selectByUsername(String username);
    
    User selectByEmail(String email);
    
    int insert(User user);
    
    int update(User user);
    
    int deleteById(Long id);
    
    List<User> selectList(@Param("keyword") String keyword);
    
    Long countUsers();
}
