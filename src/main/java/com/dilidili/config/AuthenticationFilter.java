package com.dilidili.config;

import com.dilidili.common.JwtUtil;
import com.dilidili.common.Result;
import com.dilidili.dao.domain.User;
import com.dilidili.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证过滤器，处理用户登录并生成 JWT 令牌
 */
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    /**
     * 构造方法
     *
     * @param authenticationManager 认证管理器
     * @param jwtUtil               JWT 工具类
     * @param userService           用户服务
     * @param objectMapper          JSON 序列化工具
     */
    public AuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService, ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.objectMapper = objectMapper;
        setFilterProcessesUrl("/api/users/login");
        logger.debug("AuthenticationFilter 初始化");
    }

    /**
     * 尝试认证
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @return 认证结果
     * @throws AuthenticationException 如果认证失败
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            User creds = objectMapper.readValue(request.getInputStream(), User.class);
            logger.debug("尝试登录：username={}", creds.getUsername());
            if (creds.getUsername() == null || creds.getPassword() == null) {
                logger.warn("登录失败：用户名或密码为空");
                throw new AuthenticationException("用户名或密码不能为空") {};
            }
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getUsername(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (IOException e) {
            logger.error("解析登录请求失败：error={}", e.getMessage());
            throw new RuntimeException("解析登录请求失败: " + e.getMessage(), e);
        }
    }

    /**
     * 认证成功处理
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param chain    过滤器链
     * @param authResult 认证结果
     * @throws IOException      如果写入响应失败
     * @throws ServletException 如果过滤器链执行失败
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        try {
            String username = authResult.getName();
            User dbUser = userService.findByUsername(username);
            if (dbUser == null) {
                logger.warn("登录成功但用户未找到：username={}", username);
                writeErrorResponse(response, 500, "用户未找到");
                return;
            }
            String token = jwtUtil.generateToken(username, dbUser.getId());
            logger.info("登录成功，生成令牌：username={}, token={}", username, token.substring(0, Math.min(10, token.length())));

            Map<String, String> responseData = new HashMap<>();
            responseData.put("token", token);
            responseData.put("username", username);

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getOutputStream(), Result.success(responseData));
        } catch (IOException e) {
            logger.error("写入响应失败：error={}", e.getMessage());
            writeErrorResponse(response, 500, "写入响应失败: " + e.getMessage());
        }
    }

    /**
     * 认证失败处理
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param failed   认证异常
     * @throws IOException      如果写入响应失败
     * @throws ServletException 如果过滤器链执行失败
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        logger.warn("登录失败：error={}", failed.getMessage());
        writeErrorResponse(response, 401, "登录失败: " + failed.getMessage());
    }

    /**
     * 写入错误响应
     *
     * @param response HTTP 响应
     * @param status   状态码
     * @param message  错误消息
     * @throws IOException 如果写入失败
     */
    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        objectMapper.writeValue(response.getOutputStream(), Result.error(status, message));
    }
}