package com.ykw.blog_system.exception;

import com.ykw.blog_system.enums.ResultCodeEnum;
import com.ykw.blog_system.vo.ResultVO;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 捕获认证异常
    @ExceptionHandler(AuthException.class)
    public ResultVO<Void> handleAuthException(AuthException e) {
        //通过e.getCode()获取枚举
        ResultCodeEnum resultCode = ResultCodeEnum.fromCode(e.getCode());
        return ResultVO.error(resultCode);
    }



    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVO<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResultVO.error(400, message);
    }
    
    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResultVO<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResultVO.error(400, message);
    }
    
    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResultVO<Void> handleException(Exception e) {
        e.printStackTrace();
        return ResultVO.error(500, "服务器内部错误: " + e.getMessage());
    }
}
