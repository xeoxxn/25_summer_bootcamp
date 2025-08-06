# 여름방학 부트캠프 (백엔드)

## 구조 설명
```text
src
└── main
    ├── java
    │   └── com.example.demo
    │       ├── config
    │       │   └── SecurityConfig.java           # Spring Security 설정
    │       │
            │       ├── controller
    │       │   └── UserController.java           # 회원가입, 로그인 API 컨트롤러
    │       │
            │       ├── dto
    │       │   ├── request
    │       │   │   ├── UserJoinRequest.java      # 회원가입 요청 DTO
    │       │   │   └── UserLoginRequest.java     # 로그인 요청 DTO
    │       │   │
            │       │   └── response
    │       │       ├── UserJoinResponse.java     # 회원가입 응답 DTO
    │       │       └── UserLoginResponse.java    # 로그인 응답 DTO
    │       │
            │       ├── entity
    │       │   ├── User.java                     # User 엔티티 (@Entity)
    │       │   └── Role.java                     # Role 열거형 (enum)
    │       │
            │       ├── jwt
    │       │   └── JwtUtil.java                  # JWT 생성/검증 유틸리티 클래스
    │       │
            │       ├── repository
    │       │   └── UserRepository.java           # JPA 레포지토리
    │       │
            │       └── service
    │           └── UserService.java              # 회원가입/로그인 로직 서비스
    │
            └── resources
        ├── application.properties                # DB 설정, JWT 시크릿 등 설정 파일
        └── static / templates (필요 시)
```
## 과제 1. 로그인 & 회원가입 기능 구현하기

### 단계 요약
1. 프로젝트 기본 설정
2. Entity 정의
3. DTO 생성
4. Repository 생성
5. 회원가입 로직 구현
6. 로그인 로직 구현
7. JWT 인증 준비


### 1. 프로젝트 기본 설정
- Spring Boot
- 빌드 도구: Gradle
- H2 Database 사용
- Spring Web, Spring Data JPA, Lombok 사용

### 2. Entity 정의
__user Entity 설계__
- DB 테이블 구조 정의한 클래스
- 필요한 컬럼: id, username, password, name, address
```java
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id @GeneratedValue
    private Long id;
    private String userId;
    private String password;
    private String name;
    private String address;
}
```

### 3. DTO (데이터 전송 객체) 생성
__요청과 응답을 명확하게 분리하기 위해 사용__

#### 3-1. 회원가입용
```java
// 요청
public class UserJoinRequest {
    private String userId;
    private String password;
    private String name;
    private String address;
}

// 응답
public class UserJoinResponse {
    private Long id;
    private String userId;
    private String name;
}
```
#### 3-2. 로그인용
```java
// 요청
public class LoginRequest {
    private String userId;
    private String password;
}

// 응답
public class LoginResponse {
    private String token; // JWT 토큰
}
```
### 4. Reposiory 생성
__JPA Repository (DB 접근 계층)__
- jpa : 데이터베이스와 객체를 매핑하기 위한 표준 인터페이스
- 객체를 DB에 자동으로 매핑해줌
```java
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUserId(String username);
    Optional<User> findByUserId(String userId);
}
```

### 5. 회원가입 로직 구현
- 컨트롤러 -> 서비스 -> DB -> 응답 DTO
```java
// UserController.java
@PostMapping("/join")
public UserJoinResponse join(@RequestBody @Valid UserJoinRequest request) {
    return userService.register(request);
}
```
```java
// UserService.java
public UserJoinResponse register(UserJoinRequest request) {
    if (userRepository.existsByUserId(request.getUserId())) {
        throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
    }

    User user = User.builder()
        .userId(request.getUserId())
        .password(request.getPassword()) // 아직 암호화 안 함
        .name(request.getName())
        .address(request.getAddress())
        .build();

    userRepository.save(user);

    return new UserJoinResponse(user.getId(), user.getUserId(), user.getName());
}
```
### 6. 로그인 로직 구현
- 컨트롤러 -> 서비스 -> 유저 조회 -> 비밀번호 비교 -> JWT 생성
```java
// UserController.java
@PostMapping("/login")
public LoginResponse login(@RequestBody LoginRequest request) {
    return userService.login(request);
}
```
```java
// UserService.java
public LoginResponse login(LoginRequest request) {
    User user = userRepository.findByUserId(request.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));

    if (!user.getPassword().equals(request.getPassword())) {
        throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
    }

    String token = jwtUtil.createToken(user.getUserId()); // JWT 생성
    return new LoginResponse(token);
}
```

### 7. JWT 인증 준비
- 목적: 로그인한 사용자 인증 유지 (세션X, 토큰O)
- JwtUtil 클래스 생성 -> createToken(userId) 메서드 작성
- 로그인 성공 시 클라이언트에 JWT 토큰을 응답

```java
@Component
public class JwtUtil {
    private final String secret = "secretkey1234567890";
    private final long expirationMs = 1000 * 60 * 60; // 1시간

    public String createToken(String userId) {
        return Jwts.builder()
            .setSubject(userId)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
            .compact();
    }
}
```

# 과제 2 : 역할 기반 권한 제어 (RBAC)

### 1. User 엔티티에 역할 필드 추가
1-1. Role 파일 추가
```java
public enum Role {
    USER, ADMIN
}
```
1-2. User Entity에 사용자 권한 추가
```java
@Entity
public class User {
    // ...기존 필드들...

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // 사용자 권한 (USER / ADMIN)
}
```

### 2. 회원가입 시 기본 권한 설정
```java
User user = User.builder()
    .userId(request.getUserId())
    .password(request.getPassword())
    .name(request.getName())
    .address(request.getAddress())
    .role(Role.USER) // 기본 권한 부여
    .build();
```

### 3. JWT에 역할 정보 명시
```java
public String createToken(String userId, Role role) {
    return Jwts.builder()
            // claim과 subject는 하나만 있어도 되지만 sub는 인증/인가용으로 의미 있게 쓰이기 때문에 둘 다 써도 괜찮다.
            .claim("userId", userId) // payload에 userId 넣음
            .claim("role", role.name()) // payload에 role 넣음 (문자열로 변환)
            .setSubject(userId) // subject는 일반적으로 고유 식별자용
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(key)
            .compact();
}
```

### 4. Spring Security 설정
```text
src/main/java/com/example/demo/config/SecurityConfig.java
```
```java
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity // 웹 보안 설정을 활성화
@EnableMethodSecurity(prePostEnabled = true) // @PreAuthorize, @PostAuthorize 활성화
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (테스트용)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/join", "/api/users/login").permitAll() // 회원가입/로그인은 모두 허용
                .requestMatchers("/admin/**").hasRole("ADMIN") // /admin으로 시작하는 건 ADMIN만 접근
                .anyRequest().authenticated() // 나머지는 인증된 사용자만
            );

        return http.build();
    }
}
```

### 5. UserController에서 권한별 분기 처리
```java
@GetMapping("/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public String adminOnlyPage() {
    return "접근 성공!";
}
```