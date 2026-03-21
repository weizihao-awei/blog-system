package com.ykw.blog_system.dto;

import com.ykw.blog_system.enums.FollowOperationEnum;
import lombok.Data;

@Data
public class FollowDTO {
    
    private Long targetUserId;
    
    private FollowOperationEnum operation;
}
