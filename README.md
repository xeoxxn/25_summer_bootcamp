# 여름방학 부트캠프 (백엔드)

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
