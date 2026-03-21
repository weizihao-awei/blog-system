package com.ykw.blog_system.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum FollowOperationEnum {
    
    FOLLOW(1, "关注"),
    
    UNFOLLOW(2, "取消关注");
    
    private final Integer code;
    private final String message;
    
    FollowOperationEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    
    @JsonCreator
    public static FollowOperationEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (FollowOperationEnum type : values()) {
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
