package com.dilidili.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    private static final Logger logger = LoggerFactory.getLogger(PasswordEncoderConfig.class);

    /**
     * 配置密码编码器
     *
     * @return 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.debug("创建 BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }
}