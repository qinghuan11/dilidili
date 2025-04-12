package com.dilidili.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI dilidiliOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dilidili 视频平台 API")
                        .version("v1.0")
                        .description("基于 Spring Boot + MyBatis-Plus 的视频分享平台后端接口文档"));
    }
}
