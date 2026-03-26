package com.ykw.blog_system.dto;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Data
public class TimelineQueryDTO {

    @NotNull(message = "日期不能为空")
    private LocalDate date;

    @Min(value = 1, message = "页码最小值为1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页数量最小值为1")
    private Integer pageSize = 10;
}
