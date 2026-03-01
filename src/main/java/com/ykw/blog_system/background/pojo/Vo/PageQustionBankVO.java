package com.ykw.blog_system.background.pojo.Vo;

import lombok.Data;

import java.util.List;

/**
 * @BelongsProject: backend-of-blog-website
 * @BelongsPackage: com.ykw.blog_system.background.pojo.Vo
 * @Author: ykw-weizihao
 * @CreateTime: 2026-02-28 16:45
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class PageQustionBankVO {
    //分页查询结果
    private List<QuestionBankVO> Records;
    //总记录数
    private Long total;




}