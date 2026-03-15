package com.ykw.blog_system.vo;

import lombok.Data;

/**
 * 图片上传响应VO
 */
@Data
public class ImageVO {
    /**
     * 图片URI
     */
    private String uri;

    /**
     * 文件名
     */
    private String filename;


}