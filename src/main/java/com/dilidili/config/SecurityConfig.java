package com.dilidili.config;

import com.dilidili.common.JwtUtil;
import com.dilidili.dao.domain.User;
import com.dilidili.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 安全配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    /**
     * 构造方法
     *
     * @param jwtUtil      JWT 工具类
     * @param objectMapper JSON 序列化工具
     */
    public SecurityConfig(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        logger.debug("SecurityConfig 初始化");
    }

    /**
     * 配置安全过滤器链
     *
     * @param http                 HTTP 安全配置
     * @param authenticationManager 认证管理器
     * @param userService          用户服务
     * @return 过滤器链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager, UserService userService) throws Exception {
        logger.info("配置安全过滤器链");
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 允许匿名访问 Swagger UI 和 API 文档
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 允许匿名访问注册和登录端点
                        .requestMatchers("/api/users/register", "/api/users/login").permitAll()
                        // 其他请求需要认证
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new AuthenticationFilter(authenticationManager, jwtUtil, userService, objectMapper), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new AuthorizationFilter(authenticationManager, jwtUtil, objectMapper), UsernamePasswordAuthenticationFilter.class);
        logger.debug("安全过滤器链配置完成");
        return http.build();
    }


    /**
     * 配置认证管理器
     *
     * @param authenticationConfiguration 认证配置
     * @return 认证管理器
     * @throws Exception 配置异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        logger.debug("创建 AuthenticationManager");
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 配置用户详情服务
     *
     * @param userService 用户服务
     * @return 用户详情服务
     */
    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        logger.debug("创建 UserDetailsService");
        return username -> {
            User user = userService.findByUsername(username);
            if (user == null) {
                logger.warn("用户未找到：username={}", username);
                throw new UsernameNotFoundException("User not found: " + username);
            }
            logger.debug("加载用户：username={}", username);
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
        };
    }
}