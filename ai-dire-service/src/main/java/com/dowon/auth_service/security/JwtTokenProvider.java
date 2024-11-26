package com.dowon.auth_service.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * JWT 토큰 생성 및 공개 키 제공을 담당하는 클래스입니다.
 */
@Component
public class JwtTokenProvider {

    private final RSAKey rsaKey;

    public JwtTokenProvider() throws JOSEException {
        // RSA 키 페어 생성
        rsaKey = new RSAKeyGenerator(2048)
                .keyID("auth-server-key") // 키 ID 설정
                .generate();
    }

    /**
     * 사용자 정보를 기반으로 JWT 토큰을 생성합니다.
     *
     * @param userId 사용자 ID
     * @param roles  사용자 역할 목록
     * @return 생성된 JWT 토큰
     * @throws JOSEException 토큰 생성 중 오류 발생 시
     */
    public String generateToken(Long userId, List<String> roles) throws JOSEException  {
        Instant now = Instant.now();

        // JWT 클레임 설정
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(userId.toString())
                .issuer("auth-service")
                .claim("roles", roles)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(3600))) // 토큰 유효 기간: 1시간
                .build();

        // 헤더 설정 (RS256 알고리즘 사용)
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(rsaKey.getKeyID())
                .build();

        // JWT 객체 생성
        SignedJWT signedJWT = new SignedJWT(header, claims);

        // 개인 키로 서명
        JWSSigner signer = new RSASSASigner(rsaKey.toPrivateKey());
        signedJWT.sign(signer);

        // 직렬화하여 토큰 문자열 반환
        return signedJWT.serialize();
    }

    /**
     * 공개 키를 반환합니다.
     *
     * @return RSA 공개 키
     */
    public RSAKey getPublicKey() {
        return rsaKey.toPublicJWK();
    }
}
