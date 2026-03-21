package com.ykw.blog_system.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 统一响应状态码枚举
 * 用于管理系统中所有的响应状态码
 */
@Getter
public enum ResultCodeEnum {

    // ========== 成功状态码 ==========
    /** 请求成功 */
    SUCCESS(200, "操作成功"),


    // ========== 客户端错误状态码 ==========
    //token过期
    TOKEN_EXPIRED(401, "token已过期"),

    //token无效
    TOKEN_INVALID(402, "token无效"),



    //关注相关错误
    CANNOT_FOLLOW_SELF(404, "不能关注自己"),

    ALREADY_FOLLOWED(405, "已关注该用户"),

    NOT_FOLLOWED_YET(406, "未关注该用户"),

    PARAM_ERROR(407, "参数错误");

    private final Integer code;
    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据状态码获取枚举
     *
     * @param code 状态码
     * @return 对应的枚举，找不到返回 null
     */
    @JsonCreator
    public static ResultCodeEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ResultCodeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否成功状态码
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return this.code == 200;
    }

    /**
     * 判断是否客户端错误
     *
     * @return 是否客户端错误
     */
    public boolean isClientError() {
        return this.code >= 400 && this.code < 500;
    }

    /**
     * 判断是否服务端错误
     *
     * @return 是否服务端错误
     */
    public boolean isServerError() {
        return this.code >= 500;
    }
}
