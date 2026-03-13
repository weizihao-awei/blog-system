package com.ykw.blog_system.enums;

import lombok.Getter;

/**
 * 文章排序枚举
 */
@Getter
public enum ArticleOrderEnum {
    
    /**
     * 按创建时间升序（最早发布）
     */
    CREATE_TIME_ASC("create_time_asc", "createTime", true),
    
    /**
     * 按创建时间降序（最新发布）
     */
    CREATE_TIME_DESC("create_time_desc", "createTime", false),
    
    /**
     * 按更新时间升序（最早编辑）
     */
    UPDATE_TIME_ASC("update_time_asc", "updateTime", true),
    
    /**
     * 按更新时间降序（最新编辑）
     */
    UPDATE_TIME_DESC("update_time_desc", "updateTime", false);
    
    /**
     * 枚举值（用于接口传参）
     */
    private final String value;
    
    /**
     * 对应的字段名
     */
    private final String fieldName;
    
    /**
     * 是否升序
     */
    private final boolean ascending;
    
    ArticleOrderEnum(String value, String fieldName, boolean ascending) {
        this.value = value;
        this.fieldName = fieldName;
        this.ascending = ascending;
    }
    
    /**
     * 根据值获取枚举
     * @param value 排序值
     * @return 排序枚举
     */
    public static ArticleOrderEnum fromValue(String value) {
        for (ArticleOrderEnum order : values()) {
            if (order.value.equalsIgnoreCase(value)) {
                return order;
            }
        }
        // 默认返回创建时间降序（最新发布）
        return CREATE_TIME_DESC;
    }
    
    /**
     * 判断是否支持该排序值
     * @param value 排序值
     * @return 是否支持
     */
    public static boolean isSupported(String value) {
        for (ArticleOrderEnum order : values()) {
            if (order.value.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
