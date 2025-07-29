package com.example.demo.service;

import com.example.demo.dto.request.UserJoinRequest;
import com.example.demo.dto.request.UserLoginRequest;
import com.example.demo.dto.response.UserJoinResponse;
import com.example.demo.dto.response.UserLoginResponse;
import com.example.demo.entity.User;
import com.example.demo.jwt.JwtUtil;
import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    // private final PasswordEncoder passwordEncoder;

    public UserJoinResponse register(UserJoinRequest request) {
        if (userRepository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        User user = User.builder()
                .userId(request.getUserId())
                .password(request.getPassword())
                .name(request.getName())
                .address(request.getAddress())
                .build();

        userRepository.save(user);
        return new UserJoinResponse(
                user.getId(),
                user.getUserId(),
                user.getName()
        );
    }

    public UserLoginResponse login(UserLoginRequest request) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(()-> new IllegalArgumentException("아이디가 존재하지 않습니다."));
        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }
        String token = jwtUtil.createToken(user.getUserId());
        return new UserLoginResponse(token);
    }
}
