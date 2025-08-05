package com.example.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

// 엔티티와 직접 연결하지 않고, 중간 계층의 역할 수행
// 회원가입 요청을 처리할 DTO ( 클라이언트 -> 서버로 보내는 데이터를 담음 )
@Getter
@Setter

public class UserJoinRequest {
    private String userId;
    private String password;
    private String name;
    private String address;
}
