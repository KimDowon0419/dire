package com.dowon.auth_service.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원가입 요청을 위한 DTO 클래스입니다.
 */
@Getter
@Setter
public class SignUpRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    // 추가적인 회원 정보 필드들
}
