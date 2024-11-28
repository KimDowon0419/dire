package com.dowon.auth_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * JWT 토큰 응답을 위한 DTO 클래스입니다.
 */
@Getter
@AllArgsConstructor
public class JwtAuthenticationResponse {

    private String accessToken;
    private String tokenType = "Bearer";

    public JwtAuthenticationResponse(String token) {
        this.accessToken = token;
        this.tokenType = "Bearer";
    }
}
