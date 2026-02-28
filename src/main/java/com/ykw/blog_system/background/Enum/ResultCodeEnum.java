package com.ykw.blog_system.background.Enum;

import lombok.Getter;

/**
 * 统一返回结果状态码枚举
 */
@Getter
public enum ResultCodeEnum {
    // 成功
    SUCCESS(1, "操作成功"),
    // 通用失败
    FAIL(0, "操作失败"),
    // 业务异常示例
    PARAM_ERROR(2, "参数错误"),
    AUTH_ERROR(3, "权限不足"),
    SYSTEM_ERROR(500, "系统异常");

    private final Integer code;
    private final String msg;

    ResultCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}