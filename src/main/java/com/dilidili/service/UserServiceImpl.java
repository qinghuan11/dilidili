package com.dilidili.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dilidili.dao.domain.User;
import com.dilidili.dao.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final String USER_CACHE_KEY_PREFIX = "user:username:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 注册用户
     *
     * @param user 用户对象
     * @throws IllegalArgumentException 如果用户名或邮箱已存在
     */
    @Override
    public void register(User user) {
        if (user == null || user.getUsername() == null || user.getEmail() == null) {
            logger.warn("注册失败：用户数据无效");
            throw new IllegalArgumentException("用户数据无效");
        }
        // 检查用户名唯一性
        QueryWrapper<User> usernameWrapper = new QueryWrapper<>();
        usernameWrapper.eq("username", user.getUsername());
        if (count(usernameWrapper) > 0) {
            logger.warn("注册失败：用户名已存在，{}", user.getUsername());
            throw new IllegalArgumentException("用户名已存在：" + user.getUsername());
        }
        // 检查邮箱唯一性
        QueryWrapper<User> emailWrapper = new QueryWrapper<>();
        emailWrapper.eq("email", user.getEmail());
        if (count(emailWrapper) > 0) {
            logger.warn("注册失败：邮箱已存在，{}", user.getEmail());
            throw new IllegalArgumentException("邮箱已存在：" + user.getEmail());
        }
        // 不再处理密码加密，假设密码已在上层加密
        save(user);
        cacheUser(user);
        logger.info("用户注册成功，username: {}, userId: {}", user.getUsername(), user.getId());
    }

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户对象，未找到返回 null
     */
    @Override
    public User findByUsername(String username) {
        if (username == null) {
            logger.warn("查找用户失败：用户名为空");
            return null;
        }
        String cacheKey = USER_CACHE_KEY_PREFIX + username;
        String lockKey = "lock:" + cacheKey;
        // 使用分布式锁防止缓存击穿
        try {
            Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, true, 10, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(locked)) {
                try {
                    User cachedUser = (User) redisTemplate.opsForValue().get(cacheKey);
                    if (cachedUser != null) {
                        logger.debug("用户从缓存获取：{}", username);
                        return cachedUser;
                    }
                    QueryWrapper<User> wrapper = new QueryWrapper<>();
                    wrapper.eq("username", username);
                    User user = getOne(wrapper);
                    if (user != null) {
                        cacheUser(user);
                    }
                    return user;
                } finally {
                    redisTemplate.delete(lockKey);
                }
            }
        } catch (Exception e) {
            logger.error("缓存获取失败：username={}, error={}", username, e.getMessage());
        }
        // 降级到数据库查询
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = getOne(wrapper);
        if (user != null) {
            cacheUser(user);
        }
        return user;
    }

    /**
     * 检查邮箱是否已存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    @Override
    public boolean existsByEmail(String email) {
        if (email == null) {
            logger.warn("检查邮箱失败：邮箱为空");
            return false;
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        boolean exists = count(wrapper) > 0;
        logger.debug("邮箱检查结果：email={}, exists={}", email, exists);
        return exists;
    }

    /**
     * 清除用户缓存
     *
     * @param username 用户名
     */
    @Override
    public void evictUserCache(String username) {
        if (username == null) {
            logger.warn("清除缓存失败：用户名为空");
            return;
        }
        String cacheKey = USER_CACHE_KEY_PREFIX + username;
        try {
            redisTemplate.delete(cacheKey);
            logger.debug("用户缓存已清除：{}", username);
        } catch (Exception e) {
            logger.error("清除用户缓存失败：username={}, error={}", username, e.getMessage());
        }
    }

    /**
     * 缓存用户信息到 Redis
     *
     * @param user 用户对象
     */
    private void cacheUser(User user) {
        if (user == null || user.getUsername() == null) {
            logger.warn("缓存用户失败：用户数据无效");
            return;
        }
        String cacheKey = USER_CACHE_KEY_PREFIX + user.getUsername();
        try {
            redisTemplate.opsForValue().set(cacheKey, user, 1, TimeUnit.HOURS);
            logger.debug("用户已缓存：username={}, userId={}", user.getUsername(), user.getId());
        } catch (Exception e) {
            logger.error("缓存用户失败：username={}, userId={}, error={}",
                    user.getUsername(), user.getId(), e.getMessage());
        }
    }
}