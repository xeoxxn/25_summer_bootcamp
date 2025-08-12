package com.example.demo.jwt;

import com.example.demo.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 3; // 토큰 유효 시간 : 3H

    public String generateToken(String userId, Role role) {
        return Jwts.builder()
                // claim과 subject는 하나만 있어도 되지만 sub는 인증/인가용으로 의미 있게 쓰이기 때문에 둘 다 써도 괜찮다.
                .claim("userId", userId) // payload에 userId 넣음
                .claim("role", role.name()) // payload에 role 넣음 (문자열로 변환)
                .setSubject(userId) // subject는 일반적으로 고유 식별자용
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }
    // 사용자 ID 추출 메서드
    public String getUserIdFromToken(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    // 역할 추출 메서드
    public String getRoleFromToken(String token) {
        return parseClaims(token).getBody().get("role", String.class);
    }

    // JWT 유효성 검사 메서드
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰 파싱하여 Claims 반환하는 내부 메서드
    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}
