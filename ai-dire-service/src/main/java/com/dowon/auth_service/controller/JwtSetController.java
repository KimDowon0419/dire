package com.dowon.auth_service.controller;

import com.nimbusds.jose.jwk.*;
import com.dowon.auth_service.security.JwtTokenProvider;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * JWK Set을 제공하는 컨트롤러입니다.
 */
@RestController
public class JwtSetController {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtSetController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 공개 키를 JWK Set 형식으로 반환합니다.
     *
     * @return 공개 키 정보
     */
    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJwks() {
        RSAKey publicKey = jwtTokenProvider.getPublicKey();
        return new JWKSet(publicKey).toJSONObject();
    }
}
