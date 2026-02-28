package com.ykw.blog_system.Config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 配置类
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 自动填充创建/修改时间
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
                strictInsertFill(metaObject, "modifyTime", LocalDateTime.class, LocalDateTime.now());
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                strictUpdateFill(metaObject, "modifyTime", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}