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
    public static Result success() {
        return buildResultWithoutData(ResultCodeEnum.SUCCESS);
    }

    // 成功：带数据返回
    public static Result success(Object data) {
        return buildResultWithData(ResultCodeEnum.SUCCESS, data);
    }

    // 成功：自定义提示信息+数据
    public static Result success(String msg, Object data) {
        Result result = buildResultWithData(ResultCodeEnum.SUCCESS, data);
        result.setMsg(msg);
        return result;
    }

    // 失败：使用默认失败提示
    public static Result error() {
        return buildResultWithoutData(ResultCodeEnum.FAIL);
    }

    // 失败：自定义错误提示
    public static Result error(String msg) {
        Result result = buildResultWithoutData(ResultCodeEnum.FAIL);
        result.setMsg(msg);
        return result;
    }

    // 失败：指定状态码+提示信息（无数据）
    public static Result error(ResultCodeEnum resultCodeEnum) {
        return buildResultWithoutData(resultCodeEnum);
    }

    // 失败：指定状态码+提示信息+数据
    public static Result error(ResultCodeEnum resultCodeEnum, Object data) {
        return buildResultWithData(resultCodeEnum, data);
    }

    // 通用构建方法：不带数据返回（data默认置为空字符串）
    private static Result buildResultWithoutData(ResultCodeEnum resultCodeEnum) {
        Result result = new Result();
        result.setCode(resultCodeEnum.getCode());
        result.setMsg(resultCodeEnum.getMsg());
        result.setData(""); // 避免返回null
        return result;
    }

    // 通用构建方法：带数据返回
    private static Result buildResultWithData(ResultCodeEnum resultCodeEnum, Object data) {
        Result result = new Result();
        result.setCode(resultCodeEnum.getCode());
        result.setMsg(resultCodeEnum.getMsg());
        result.setData(data == null ? "" : data); // 空数据仍置为空字符串
        return result;
    }
}