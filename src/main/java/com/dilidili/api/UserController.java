package com.dilidili.api;

import com.dilidili.common.JwtUtil;
import com.dilidili.common.Result;
import com.dilidili.dao.domain.User;
import com.dilidili.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<Result<String>> register(@Validated @RequestBody UserRegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        userService.register(user);
        return ResponseEntity.ok(Result.success("Registration successful"));
    }

    @PostMapping("/login")
    public void login() {
        // 占位，实际由 AuthenticationFilter 处理
    }

    @PostMapping("/logout")
    public ResponseEntity<Result<String>> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        String username = jwtUtil.verifyToken(token);
        userService.evictUserCache(username);
        return ResponseEntity.ok(Result.success("Logout successful"));
    }

    @GetMapping("/profile")
    public ResponseEntity<Result<User>> getProfile(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        String username = jwtUtil.verifyToken(token);
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(Result.success(user));
    }
}

class UserRegisterRequest {
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    @Email(message = "Email must be valid")
    private String email;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}