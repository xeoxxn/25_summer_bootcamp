package com.example.demo.jwt;

import com.example.demo.entity.Role;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 1시간

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
    public String getUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
