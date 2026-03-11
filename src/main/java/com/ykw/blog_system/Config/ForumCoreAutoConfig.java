package com.ykw.blog_system.Config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: blog-system
 * @BelongsPackage: com.ykw.blog_system.config
 * @Author: ykw-weizihao
 * @CreateTime: 2026-03-06 14:32
 * @Description: TODO
 * @Version: 1.0
 */
@Configuration
public class ForumCoreAutoConfig {

    /**
     * 定义缓存管理器，配合Spring的 @Cache 来使用
     *
     * @return
     */
    @Bean("caffeineCacheManager")
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 设置过期时间，写入后五分钟过期
                .expireAfterWrite(5, TimeUnit.MINUTES)
                //todo: 这个会有问题，如果真的上线用户量比较大情况
                .initialCapacity(100)
                // 最大的缓存条数
                .maximumSize(200)
        );
        return cacheManager;
    }

}