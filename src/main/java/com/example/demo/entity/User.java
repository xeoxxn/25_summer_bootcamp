package com.example.demo.entity;

import jakarta.persistence.*; // JPA 관련 어노테이션을 사용하기 위해 import
import lombok.*; // Lombok으로 생성자, getter/ setter 등을 자동 생성
// Entity : 이 클래스가 JPA의 엔티티임을 나타냄. 데이터베이스 테이블에 매핑됨
@Entity
// Lombok 어노테이션들
@Getter @Setter // 모든 필드에 대한 getter/setter 생성
// 둘 다 생성자를 자동으로 생성
@NoArgsConstructor // 파라미터 없는 생성자(기본 생성자) 생성
@AllArgsConstructor // 모든 필드 값을 받는 생성자 생성
@Builder // 빌더 패턴을 통해 객체 생성 가능하게 함
public class User {
    // 이 필드가 PK임을 의미
    @Id
    // 자동으로 ID를 생성해주는 전략 설정 (IDENTITY는 DB가 기본 키를 자동 증가(auto increment) 하도록 위임)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // DB 컬럼과 매핑 / unique = true로 중복 방지, nullable = false로 not null 처리
    @Column(unique = true, nullable = false)
    private String userId;

    @Column(nullable = false)
    private String password; // 비밀번호 => 중복 상관 없음

    private String name; // 이름 (null 허용)
    private String address; // 주소 (null 허용)

    @Enumerated(EnumType.STRING) // enum을 문자열로 DB에 저장
    @Column(nullable = false)
    private Role role; // USER 또는 ADMIN
}
