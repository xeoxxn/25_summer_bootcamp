package com.example.demo.controller;

import com.example.demo.dto.request.UserJoinRequest;
import com.example.demo.dto.request.UserLoginRequest;
import com.example.demo.dto.response.UserJoinResponse;
import com.example.demo.dto.response.UserLoginResponse;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    @GetMapping("/admin/dashboard") // HTTP GET 요청 중에서 "/admin/dashboard"로 들어오는 요청을 처리하는 메서드
    @PreAuthorize("hasRole('ADMIN')") // 이 메서드는 'ADMIN' 권한을 가진 사용자만 접근 가능하도록 제한
    public String adminOnlyPage() {
        return "접근 성공!";
    }
    private final UserService userService;

    @PostMapping("/join")
    public UserJoinResponse join(@RequestBody UserJoinRequest request){
        return userService.register(request);
    }

    @PostMapping("/login")
    public UserLoginResponse login(@RequestBody UserLoginRequest request){
        return userService.login(request);
    }
}
