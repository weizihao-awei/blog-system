package com.ykw.blog_system.dto;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class MonthStatisticsDTO {

    @NotNull(message = "年份不能为空")
    private Integer year;

    @NotNull(message = "月份不能为空")
    @Min(value = 1, message = "月份最小值为 1")
    @Max(value = 12, message = "月份最大值为 12")
    private Integer month;
}
