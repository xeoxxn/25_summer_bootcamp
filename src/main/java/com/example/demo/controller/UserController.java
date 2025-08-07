package com.example.demo.controller;

import com.example.demo.dto.request.UserJoinRequest;
import com.example.demo.dto.request.UserLoginRequest;
import com.example.demo.dto.response.UserJoinResponse;
import com.example.demo.dto.response.UserLoginResponse;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users") // 모든 요청 앞에 /api/users 공통으로 붙음
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입 API
    @PostMapping("/join")
    public ResponseEntity<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        UserJoinResponse response = userService.register(request);
        return ResponseEntity.status(201).body(response); // HTTP 201 Created
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        UserLoginResponse response = userService.login(request);
        return ResponseEntity.ok(response); // HTTP 200 OK
    }
}