package com.ykw.blog_system.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private final String uploadPath;
    
    public WebConfig(@Value("${file.upload.path:uploads/}") String uploadPath) {
        // 如果是相对路径，转换为项目根目录下的绝对路径
        if (!uploadPath.startsWith("/") && !uploadPath.matches("^[A-Za-z]:.*")) {
            this.uploadPath = System.getProperty("user.dir") + "/" + uploadPath;
        } else {
            this.uploadPath = uploadPath;
        }
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
