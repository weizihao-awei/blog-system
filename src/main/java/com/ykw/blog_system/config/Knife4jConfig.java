package com.ykw.blog_system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")  // 仅在开发环境启用
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("博客系统API文档")
                        .version("1.0.0")
                        .description("基于 Spring Boot + MyBatis 的个人博客系统接口文档")
                        .contact(new Contact().name("作者"))
                        .license(new License().name("Apache License 2.0")));
    }

    @Bean
    public GroupedOpenApi defaultApi() {
        return GroupedOpenApi.builder()
                .group("default")
                .packagesToScan("com.ykw.blog_system.controller")
                .build();
    }
}