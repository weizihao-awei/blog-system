package com.ykw.blog_system.service;

import com.ykw.blog_system.dto.UserFootQueryDTO;
import com.ykw.blog_system.entity.User;
import com.ykw.blog_system.enums.FollowOperationEnum;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;

public interface UserRelationService {
    
    ResultVO<Void> followOrUnfollow(Long targetUserId, FollowOperationEnum operation);
    
    ResultVO<PageVO<User>> getFollowersList(UserFootQueryDTO queryDTO);
    
    ResultVO<PageVO<User>> getFollowingList(UserFootQueryDTO queryDTO);
    
    ResultVO<Long> getFollowersCount();
    
    ResultVO<Long> getFollowingCount();
}
