package com.ykw.blog_system.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum FollowStateEnum {
    
    UNFOLLOWED(0, "未关注"),
    
    FOLLOWED(1, "已关注"),
    
    CANCELLED(2, "取消关注");
    
    private final Integer code;
    private final String message;
    
    FollowStateEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    
    @JsonCreator
    public static FollowStateEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (FollowStateEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
    
    @JsonValue
    public Integer getCode() {
        return code;
    }
}
