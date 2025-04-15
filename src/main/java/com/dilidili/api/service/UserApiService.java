package com.dilidili.api.service;

import com.dilidili.api.dto.UserRegisterRequest;
import com.dilidili.api.mapper.UserDtoMapper;
import com.dilidili.common.JwtUtil;
import com.dilidili.common.Result;
import com.dilidili.dao.domain.User;
import com.dilidili.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 用户相关接口服务
 */
@Service
@RequiredArgsConstructor
public class UserApiService {

    private static final Logger logger = LoggerFactory.getLogger(UserApiService.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String BLACKLIST_PREFIX = "blacklist:token:";

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserDtoMapper userDtoMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder; // 新增 PasswordEncoder 依赖

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册结果
     * @throws IllegalArgumentException 如果请求数据无效
     */
    public Result<?> register(UserRegisterRequest request) {
        if (request == null || !StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getEmail())) {
            logger.warn("注册失败：请求数据无效，用户名或邮箱为空");
            throw new IllegalArgumentException("用户名和邮箱不能为空");
        }
        try {
            User user = userDtoMapper.toEntity(request);
            // 在这里加密密码
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.register(user);
            logger.info("用户注册成功：username={}, email={}", request.getUsername(), request.getEmail());
            return Result.success("注册成功");
        } catch (IllegalArgumentException e) {
            logger.warn("注册失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("用户注册失败：username={}, error={}", request.getUsername(), e.getMessage());
            throw new IllegalArgumentException("注册失败：" + e.getMessage(), e);
        }
    }

    /**
     * 用户登出
     *
     * @param token JWT 令牌
     * @return 登出结果
     * @throws IllegalArgumentException 如果令牌无效
     */
    public Result<?> logout(String token) {
        if (!StringUtils.hasText(token)) {
            logger.warn("登出失败：令牌为空");
            throw new IllegalArgumentException("无效的令牌");
        }
        try {
            String username = jwtUtil.verifyToken(token);
            userService.evictUserCache(username);
            redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, true, jwtUtil.getExpirationTime(), TimeUnit.MILLISECONDS);
            logger.info("用户登出成功：username={}, 令牌已加入黑名单", username);
            return Result.success(username);
        } catch (Exception e) {
            logger.error("登出失败：error={}", e.getMessage());
            throw new IllegalArgumentException("令牌验证失败：" + e.getMessage(), e);
        }
    }

    /**
     * 获取用户资料
     *
     * @param token JWT 令牌
     * @return 用户资料结果
     * @throws IllegalArgumentException 如果令牌无效或用户不存在
     */
    public Result<User> getProfile(String token) {
        if (!StringUtils.hasText(token)) {
            logger.warn("获取资料失败：令牌为空");
            throw new IllegalArgumentException("无效的令牌");
        }
        try {
            String username = jwtUtil.verifyToken(token);
            User user = userService.findByUsername(username);
            if (user == null) {
                logger.warn("获取资料失败：用户不存在，{}", username);
                throw new IllegalArgumentException("用户不存在：" + username);
            }
            logger.debug("获取用户资料成功：{}", username);
            return Result.success(user);
        } catch (Exception e) {
            logger.error("获取资料失败：error={}", e.getMessage());
            throw new IllegalArgumentException("令牌验证失败：" + e.getMessage(), e);
        }
    }

    /**
     * 提取 JWT 令牌
     *
     * @param authorizationHeader 授权头
     * @return 令牌字符串
     * @throws IllegalArgumentException 如果授权头无效
     */
    public String extractToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            logger.warn("提取令牌失败：授权头为空");
            throw new IllegalArgumentException("无效的 Authorization 头：头为空");
        }
        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
            logger.warn("提取令牌失败：无效的授权头格式，{}", authorizationHeader);
            throw new IllegalArgumentException("无效的 Authorization 头：" + authorizationHeader);
        }
        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        if (!StringUtils.hasText(token)) {
            logger.warn("提取令牌失败：令牌为空，头：{}", authorizationHeader);
            throw new IllegalArgumentException("无效的 Authorization 头：令牌为空");
        }
        logger.debug("成功提取令牌");
        return token;
    }
}