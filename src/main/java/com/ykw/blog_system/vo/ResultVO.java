package com.ykw.blog_system.vo;

import lombok.Data;

/**
 * 统一响应结果VO
 */
@Data
public class ResultVO<T> {
    
    private Integer code;
    
    private String message;
    
    private T data;
    
    private Long timestamp;
    
    public ResultVO() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public static <T> ResultVO<T> success() {
        ResultVO<T> result = new ResultVO<>();
        result.setCode(200);
        result.setMessage("操作成功");
        return result;
    }
    
    public static <T> ResultVO<T> success(T data) {
        ResultVO<T> result = new ResultVO<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }
    // 带消息
    public static <T> ResultVO<T> success(String message) {
        ResultVO<T> result = new ResultVO<>();
        result.setCode(200);
        result.setMessage(message);
        return result;
    }
    
    public static <T> ResultVO<T> success(String message, T data) {
        ResultVO<T> result = new ResultVO<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
    
    public static <T> ResultVO<T> error(String message) {
        ResultVO<T> result = new ResultVO<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }
    
    public static <T> ResultVO<T> error(Integer code, String message) {
        ResultVO<T> result = new ResultVO<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    
    public static <T> ResultVO<T> error(Integer code, String message, T data) {
        ResultVO<T> result = new ResultVO<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
}
