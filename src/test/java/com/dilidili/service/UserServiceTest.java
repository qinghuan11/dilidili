package com.dilidili.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dilidili.dao.domain.User;
import com.dilidili.dao.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户服务测试
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @BeforeEach
    void setUp() {
        // 模拟 RedisTemplate 的行为
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyBoolean(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(valueOperations.get(anyString())).thenReturn(null);
        doNothing().when(valueOperations).set(anyString(), any(), anyLong(), any(TimeUnit.class));

        // 模拟 PasswordEncoder
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        logger.debug("测试环境初始化完成");
    }

    /**
     * 测试用户注册
     */
    @Test
    void testRegister() {
        User user = new User();
        user.setUsername("testuser6");
        user.setPassword("password123");
        user.setEmail("test6@example.com");

        // 模拟数据库查询：用户名和邮箱都不存在
        when(userMapper.selectCount(argThat(wrapper -> wrapper.getSqlSegment().contains("username")))).thenReturn(0L);
        when(userMapper.selectCount(argThat(wrapper -> wrapper.getSqlSegment().contains("email")))).thenReturn(0L);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // 注册用户
        userService.register(user);

        // 验证密码是否被加密
        assertEquals("encodedPassword", user.getPassword(), "密码未正确加密");

        // 模拟查找用户
        User foundUser = new User();
        foundUser.setUsername("testuser6");
        when(userMapper.selectOne(argThat(wrapper -> wrapper.getSqlSegment().contains("username")), eq(true))).thenReturn(foundUser);

        // 查找用户
        User result = userService.findByUsername("testuser6");
        assertNotNull(result, "用户未找到");
        assertEquals("testuser6", result.getUsername(), "用户名不匹配");

        // 验证 Redis 缓存调用
        verify(valueOperations, times(1)).set(eq("user:username:testuser6"), any(User.class), eq(1L), eq(TimeUnit.HOURS));
    }

    /**
     * 测试重复用户名注册
     */
    @Test
    void testRegisterDuplicateUsername() {
        User user = new User();
        user.setUsername("testuser6");
        user.setPassword("password123");
        user.setEmail("test6@example.com");

        // 第一次注册：用户名和邮箱都不存在
        when(userMapper.selectCount(argThat(wrapper -> wrapper.getSqlSegment().contains("username")))).thenReturn(0L);
        when(userMapper.selectCount(argThat(wrapper -> wrapper.getSqlSegment().contains("email")))).thenReturn(0L);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        userService.register(user);

        // 第二次注册：用户名已存在
        when(userMapper.selectCount(argThat(wrapper -> wrapper.getSqlSegment().contains("username")))).thenReturn(1L);
        when(userMapper.selectCount(argThat(wrapper -> wrapper.getSqlSegment().contains("email")))).thenReturn(0L);

        // 验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.register(user), "未抛出重复用户名异常");
        assertEquals("用户名已存在：testuser6", exception.getMessage(), "异常消息不匹配");
    }
}