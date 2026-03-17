package com.ykw.blog_system.exception;

import lombok.Data;
import lombok.Getter;

// 自定义异常，标识认证失败
@Getter
public class AuthException extends RuntimeException {
    private Integer code;

    public AuthException(Integer code, String message) {
        super(message);
        this.code = code;
    }


}