package com.example.demo.controller;

import com.example.demo.dto.request.UserJoinRequest;
import com.example.demo.dto.request.UserLoginRequest;
import com.example.demo.dto.response.UserJoinResponse;
import com.example.demo.dto.response.UserLoginResponse;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
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
