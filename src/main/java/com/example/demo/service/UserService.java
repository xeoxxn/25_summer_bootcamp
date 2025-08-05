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

@Service // 이 클래스가 Service 계층임을 나타내고, Bean(스프링이 관리하는 객체)으로 등록됨
@RequiredArgsConstructor // final로 선언한 필드(userRepository)를 자동으로 생성자 주입함 (DI, 의존성 주입)
public class UserService {
    private final UserRepository userRepository; // DB와 통신하기 위한 JPA 인터페이스
    private final JwtUtil jwtUtil; // JWT 토큰을 생성 / 검증하기 위한 유틸 클래스
    // private final PasswordEncoder passwordEncoder; // 비밀번호 암호화

    public UserJoinResponse register(UserJoinRequest request) {
        // [1] userId 중복 확인
        if (userRepository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // [2] User 엔티티 객체 생성 (Builder 패턴)
        User user = User.builder()
                .userId(request.getUserId())
                .password(request.getPassword())
                .name(request.getName())
                .address(request.getAddress())
                .build();
        // [3] DB에 저장
        userRepository.save(user);

        // [4] 저장된 유저 정보를 응답 DTO로 반환
        return new UserJoinResponse(
                user.getId(), // 자동 생성된 DB ID
                user.getUserId(), // 사용자 ID
                user.getName() // 사용자 이름
        );
    }

    public UserLoginResponse login(UserLoginRequest request) {

        // [1] 아이디로 유저 조회 (없으면 예외 발생)
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(()-> new IllegalArgumentException("아이디가 존재하지 않습니다."));

        // [2] 비밀번호 일치 여부 확인
        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        //[3] JWT 토큰 생성
        // jwtUtil.createToken() 메서드를 통해 사용자 ID 기반의 토큰을 만듦
        String token = jwtUtil.createToken(user.getUserId());

        // [4] 로그인 응답 객체 생성 후 반환
        return new UserLoginResponse(token);
    }
}
