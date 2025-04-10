package com.dilidili.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dilidili.dao.domain.User;
import com.dilidili.dao.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        setFieldInSuperclass(userService, "baseMapper", userMapper);
        setField(userService, "redisTemplate", redisTemplate);
        setField(userService, "passwordEncoder", passwordEncoder);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));
    }

    @Test
    void testRegister() {
        User user = new User();
        user.setUsername("testuser6");
        user.setPassword("password123");
        user.setEmail("test6@example.com");

        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        userService.register(user);

        User foundUser = new User();
        foundUser.setUsername("testuser6");
        // 修正存根，匹配 selectOne 的两个参数
        when(userMapper.selectOne(any(QueryWrapper.class), eq(true))).thenReturn(foundUser);
        when(redisTemplate.opsForValue().get(anyString())).thenReturn(null);

        User result = userService.findByUsername("testuser6");
        assertNotNull(result);
        assertEquals("testuser6", result.getUsername());
    }

    @Test
    void testRegisterDuplicateUsername() {
        User user = new User();
        user.setUsername("testuser6");
        user.setPassword("password123");
        user.setEmail("test6@example.com");

        // 使用 any() 匹配任意 QueryWrapper
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L).thenReturn(1L);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        userService.register(user);
        assertThrows(IllegalArgumentException.class, () -> userService.register(user));
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }

    private void setFieldInSuperclass(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getSuperclass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set superclass field: " + fieldName, e);
        }
    }
}