package com.ykw.blog_system.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 验证码类型枚举
 */
public enum VerificationCodeType {

    /**
     * 注册
     */
    REGISTER("register", "注册"),

    /**
     * 重置密码
     */
    RESET("reset", "重置密码");

    private final String code;
    private final String description;

    VerificationCodeType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据 code 获取枚举
     *
     * @param code 类型代码
     * @return 对应的枚举，找不到返回 null
     */
    @JsonCreator
    public static VerificationCodeType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (VerificationCodeType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}
