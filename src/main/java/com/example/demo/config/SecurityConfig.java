package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 웹 보안 설정을 활성화
@EnableMethodSecurity(prePostEnabled = true) // @PreAuthorize(메서드 실행 전 검사), @PostAuthorize(메서드 실행 후 검사) 활성화
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화 (테스트용) - 공격 방식 (테스트용이라 보호 비활성화)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/join", "api/user/login").permitAll() // 회원가입 / 로그인은 모두 허용
                        .requestMatchers("/admin/**").hasRole("ADMIN") // /admin으로 시작하는 건 ADMIN만 접근 가능하도록
                        .anyRequest().authenticated() // 나머지는 인증된 사용자만 접근 허용
                );
        return http.build();
    }
}
