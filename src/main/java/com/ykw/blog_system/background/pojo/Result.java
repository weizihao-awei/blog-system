package com.ykw.blog_system.background.pojo;

import com.ykw.blog_system.background.Enum.ResultCodeEnum;
import lombok.Data;
import java.io.Serializable;

/**
 * 后端统一返回结果
 */
@Data
public class Result implements Serializable {
    // 序列化版本号（规范）
    private static final long serialVersionUID = 1L;


    private Integer code; // 状态码
    private String msg;   // 提示信息
    private Object data;  // 返回数据

    // 私有构造方法，避免外部直接new
    private Result() {}

    // 成功：无数据返回



    // 成功：自定义提示信息+数据
    public static Result success(String msg, Object data) {
        return buildResult(ResultCodeEnum.SUCCESS.getCode(), msg, data);
    }
    // 成功：自定义提示信息
    public static Result success(String msg) {
        return buildResult(ResultCodeEnum.SUCCESS.getCode(), msg, "");
    }


    // 失败：自定义错误提示
    public static Result error(String msg) {
        return buildResult(ResultCodeEnum.FAIL.getCode(), msg, "");
    }

    // 失败：指定状态码+提示信息（无数据）
    public static Result error(ResultCodeEnum resultCodeEnum) {
        return buildResult(resultCodeEnum.getCode(), resultCodeEnum.getMsg(), "");
    }

    // 失败：指定状态码+提示信息+数据
    public static Result error(ResultCodeEnum resultCodeEnum, Object data) {
        return buildResult(resultCodeEnum.getCode(), resultCodeEnum.getMsg(), data);
    }

    // 通用构建方法
    private static Result buildResult(Integer code, String msg, Object data) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data == null ? "" : data);
        return result;
    }
}

