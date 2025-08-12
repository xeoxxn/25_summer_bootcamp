package com.example.demo.exception;

public class CustomException extends RuntimeException {
    private final int status; // 예외 발생 시 응답할 HTTP 상태 코드

    public CustomException(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}