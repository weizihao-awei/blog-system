package com.ykw.blog_system.vo;

import lombok.Data;

import java.util.List;

/**
 * 分页VO
 */
@Data
public class PageVO<T> {
    
    private List<T> list;
    
    private Long total;
    
    private Integer pageNum;
    
    private Integer pageSize;
    
    private Integer totalPages;
    
    private Boolean hasNextPage;
    
    private Boolean hasPreviousPage;
    
    public PageVO() {}
    
    public PageVO(List<T> list, Long total, Integer pageNum, Integer pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
        this.hasNextPage = pageNum < this.totalPages;
        this.hasPreviousPage = pageNum > 1;
    }
}
