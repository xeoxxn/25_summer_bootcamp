package com.example.demo.dto.response;

import com.example.demo.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

// 회원가입 후 사용자 정보를 응답할 DTO ( 서버 -> 클라이언트로 보내는 데이터 담음 )
@Getter
@AllArgsConstructor // 생성자 하나로 값들을 다 전달할 수 있도록 함
public class UserJoinResponse {
    private Long id;
    private String userId;
    private String name;
    private Role role;
}
