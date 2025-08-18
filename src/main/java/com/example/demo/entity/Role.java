package com.example.demo.entity;

public enum Role {
    GUEST, // 로그인 안 한 사용자 (비회원)
    USER, // 로그인 한 일반 회원
    MANAGER, // 특정 기능 담당 (예: 글 관리, 상품 관리)
    ADMIN, // 전체 관리자
    SUPER_ADMIN // 시스템 최고 관리자
}
