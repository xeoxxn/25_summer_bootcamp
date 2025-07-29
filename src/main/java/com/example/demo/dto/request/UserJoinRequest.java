package com.example.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserJoinRequest {
    private String userId;
    private String password;
    private String name;
    private String address;
}
