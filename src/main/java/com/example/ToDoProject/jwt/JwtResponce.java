package com.example.ToDoProject.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class JwtResponce {
    private final String type = "Bearer";
    private final String accessToken;
    private final String refreshToken;
}