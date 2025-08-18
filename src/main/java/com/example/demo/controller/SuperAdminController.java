package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/super")
public class SuperAdminController {

    @GetMapping("/settings")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String getSuperSettings() {
        return "최고 관리자 설정 페이지";
    }
}