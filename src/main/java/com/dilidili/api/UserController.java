package com.dilidili.api;

import com.dilidili.api.dto.UserRegisterRequest;
import com.dilidili.api.service.UserApiService;
import com.dilidili.common.Result;
import com.dilidili.dao.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserApiService userApiService;

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<Result<?>> register(@Validated @RequestBody UserRegisterRequest request) {
        logger.info("处理注册请求：username={}", request.getUsername());
        Result<?> result = userApiService.register(request);
        logger.debug("注册成功：username={}", request.getUsername());
        return ResponseEntity.ok(result);
    }

    /**
     * 用户登出
     *
     * @param request HTTP 请求，包含 token
     * @return 登出结果
     */
    @PostMapping("/logout")
    public ResponseEntity<Result<?>> logout(HttpServletRequest request) {
        String token = (String) request.getAttribute("token");
        logger.info("处理登出请求：token={}", token.substring(0, Math.min(10, token.length())));
        Result<?> result = userApiService.logout(token);
        logger.debug("登出成功：username={}", result.getData());
        return ResponseEntity.ok(result);
    }

    /**
     * 获取用户资料
     *
     * @param request HTTP 请求，包含 token
     * @return 用户资料
     */
    @GetMapping("/profile")
    public ResponseEntity<Result<User>> getProfile(HttpServletRequest request) {
        String token = (String) request.getAttribute("token");
        logger.info("处理获取资料请求：token={}", token.substring(0, Math.min(10, token.length())));
        Result<User> result = userApiService.getProfile(token);
        logger.debug("获取资料成功：username={}", result.getData().getUsername());
        return ResponseEntity.ok(result);
    }
}