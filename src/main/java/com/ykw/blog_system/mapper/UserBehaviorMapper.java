package com.ykw.blog_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ykw.blog_system.entity.UserBehavior;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户行为记录Mapper接口
 */
@Mapper
public interface UserBehaviorMapper extends BaseMapper<UserBehavior> {
    
    int insert(UserBehavior userBehavior);
    
    int update(UserBehavior userBehavior);
    
    UserBehavior selectByUserAndArticleAndType(@Param("userId") Long userId, 
                                               @Param("articleId") Long articleId, 
                                               @Param("behaviorType") String behaviorType);
    
    List<UserBehavior> selectByUserId(Long userId);
    
    List<UserBehavior> selectByArticleId(Long articleId);
    
    List<UserBehavior> selectByUserIdAndType(@Param("userId") Long userId, 
                                             @Param("behaviorType") String behaviorType);
    
    int deleteByUserAndArticleAndType(@Param("userId") Long userId, 
                                      @Param("articleId") Long articleId, 
                                      @Param("behaviorType") String behaviorType);
}
