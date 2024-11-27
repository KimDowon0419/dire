package com.dowon.auth_service.controller;

import com.dowon.auth_service.security.JwtTokenProvider;
import com.nimbusds.jose.jwk.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * JWK Set을 제공하는 컨트롤러입니다.
 */
@RestController
public class JwkSetController {

    private final JwtTokenProvider jwtTokenProvider;

    public JwkSetController(JwtTokenProvider jwtTokenProvider) {
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
