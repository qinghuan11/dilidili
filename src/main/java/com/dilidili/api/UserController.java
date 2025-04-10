package com.dilidili.api;

import com.dilidili.api.dto.UserRegisterRequest;
import com.dilidili.api.service.UserApiService;
import com.dilidili.common.Result;
import com.dilidili.dao.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserApiService userApiService;

    @PostMapping("/register")
    public ResponseEntity<Result<String>> register(@Validated @RequestBody UserRegisterRequest request) {
        userApiService.register(request);
        return ResponseEntity.ok(Result.success("Registration successful"));
    }

    @PostMapping("/login")
    public void login() {
        // 占位，实际由 AuthenticationFilter 处理
    }

    @PostMapping("/logout")
    public ResponseEntity<Result<String>> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = userApiService.extractToken(authorizationHeader);
        userApiService.logout(token);
        return ResponseEntity.ok(Result.success("Logout successful"));
    }

    @GetMapping("/profile")
    public ResponseEntity<Result<User>> getProfile(@RequestHeader("Authorization") String authorizationHeader) {
        String token = userApiService.extractToken(authorizationHeader);
        User user = userApiService.getProfile(token);
        return ResponseEntity.ok(Result.success(user));
    }
}