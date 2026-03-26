package com.ykw.blog_system.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MonthStatisticsVO {

    private Integer year;

    private Integer month;

    private List<DailyCountVO> dailyCounts;

    @Data
    public static class DailyCountVO {
        private Integer day;
        private Integer count;
    }
}
