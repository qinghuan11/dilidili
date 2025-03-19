package com.dilidili.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dilidili.dao.domain.User;
import com.dilidili.dao.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final String USER_CACHE_KEY_PREFIX = "user:username:";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void register(User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        if (count(wrapper) > 0) {
            throw new IllegalArgumentException("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // 使用加密
        save(user);
        cacheUser(user);
        logger.info("User registered successfully: {}", user.getUsername());
    }

    public User findByUsername(String username) {
        String cacheKey = USER_CACHE_KEY_PREFIX + username;
        User cachedUser = (User) redisTemplate.opsForValue().get(cacheKey);
        if (cachedUser != null) {
            logger.debug("User found in cache: {}", username);
            return cachedUser;
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = getOne(wrapper);
        if (user != null) {
            cacheUser(user);
        }
        return user;
    }

    private void cacheUser(User user) {
        String cacheKey = USER_CACHE_KEY_PREFIX + user.getUsername();
        try {
            redisTemplate.opsForValue().set(cacheKey, user, 1, TimeUnit.HOURS);
            logger.debug("User cached: {}", user.getUsername());
        } catch (Exception e) {
            logger.error("Failed to cache user: {}", user.getUsername(), e);
        }
    }

    public void evictUserCache(String username) {
        String cacheKey = USER_CACHE_KEY_PREFIX + username;
        try {
            redisTemplate.delete(cacheKey);
            logger.debug("User cache evicted: {}", username);
        } catch (Exception e) {
            logger.error("Failed to evict user cache: {}", username, e);
        }
    }
}