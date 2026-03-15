package com.ykw.blog_system.enums;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 排序方向枚举
 */
@Getter
@AllArgsConstructor
public enum OrderEnum {
    
    /**
     * 从早到晚（升序）
     */
    ASC("asc", true),
    
    /**
     * 从晚到早（降序）
     */
    DESC("desc", false);
    
    private final String value;
    private final boolean isAsc;
    
    /**
     * 根据值获取枚举
     * @param value 排序值
     * @return 排序枚举
     */
    public static OrderEnum fromValue(String value) {
        if (value == null) {
            return DESC;
        }
        for (OrderEnum order : values()) {
            if (order.value.equalsIgnoreCase(value)) {
                return order;
            }
        }
        return DESC;
    }
    
    /**
     * 转换为 MyBatis-Plus 的 OrderItem
     * @param field 字段名
     * @return OrderItem
     */
    public OrderItem toOrderItem(String field) {
        return isAsc ? OrderItem.asc(field) : OrderItem.desc(field);
    }
}
