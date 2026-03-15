package com.ykw.blog_system.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ykw.blog_system.Config.OrderEnumDeserializer;
import com.ykw.blog_system.enums.OrderEnum;
import lombok.Data;

/**
 * 用户足迹查询 DTO
 */
@Data
public class UserFootQueryDTO {
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
    
    /**
     * 排序方式：asc-从早到晚，desc-从晚到早
     */
    @JsonDeserialize(using = OrderEnumDeserializer.class)
    private OrderEnum order = OrderEnum.DESC;
    
    /**
     * 文档类型：1-文章，2-评论
     */
    private Integer documentType = 1;
}
