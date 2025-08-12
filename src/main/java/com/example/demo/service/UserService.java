package com.example.demo.service;

import com.example.demo.dto.request.UserJoinRequest;
import com.example.demo.dto.request.UserLoginRequest;
import com.example.demo.dto.response.UserJoinResponse;
import com.example.demo.dto.response.UserLoginResponse;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.CustomException;
import com.example.demo.jwt.JwtUtil;
import com.example.demo.kafka.UserKafkaProducer;
import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service // 이 클래스가 Service 계층임을 나타내고, Bean(스프링이 관리하는 객체)으로 등록됨
@RequiredArgsConstructor // final로 선언한 필드(userRepository)를 자동으로 생성자 주입함 (DI, 의존성 주입)
public class UserService {
    private final UserRepository userRepository; // DB와 통신하기 위한 JPA 인터페이스
    private final JwtUtil jwtUtil; // JWT 토큰을 생성 / 검증하기 위한 유틸 클래스
    private final UserKafkaProducer userKafkaProducer; // 카프카 프로듀서 주입
    private final PasswordEncoder passwordEncoder;

    public UserJoinResponse register(UserJoinRequest request) {
        // [1] userId 중복 확인
        if (userRepository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        // 비밀번호 암호화

        // [2] User 엔티티 객체 생성 (Builder 패턴)
        User user = User.builder()
                .userId(request.getUserId())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .address(request.getAddress())
                .role(request.getRole()) // 역할 반영
                .build();
        // [3] DB에 저장
        userRepository.save(user);

        // Kafka 이벤트 전송
        userKafkaProducer.send("user-joined", user.getUserId()); // Kafka에 가입 메세지 전송

        // [4] 저장된 유저 정보를 응답 DTO로 반환
        return new UserJoinResponse(
                user.getId(), // 자동 생성된 DB ID
                user.getUserId(), // 사용자 ID
                user.getName(), // 사용자 이름
                user.getRole()
        );
    }

    public String login(String userId, String password) {

        // 사용자 존재 여부 확인
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException("아이디가 존재하지 않습니다.", HttpStatus.NOT_FOUND.value()));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED.value());
        }

        userKafkaProducer.send("user-logged-in", user.getUserId()); // Kafka에 로그인 성공 메세지 전송

        return jwtUtil.generateToken(user.getUserId(), user.getRole());  // 로그인 성공 시 JWT 토큰 반환
    }
}
