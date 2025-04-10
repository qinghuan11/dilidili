package com.dilidili.api.service;

import com.dilidili.api.dto.UserRegisterRequest;
import com.dilidili.common.JwtUtil;
import com.dilidili.dao.domain.User;
import com.dilidili.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserApiService {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public void register(UserRegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        userService.register(user);
    }

    public String logout(String token) {
        String username = jwtUtil.verifyToken(token);
        userService.evictUserCache(username);
        return username;
    }

    public User getProfile(String token) {
        String username = jwtUtil.verifyToken(token);
        return userService.findByUsername(username);
    }

    // 提取 token 的通用方法
    public String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.replace("Bearer ", "");
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }
}
