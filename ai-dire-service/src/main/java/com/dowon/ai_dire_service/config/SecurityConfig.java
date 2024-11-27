package com.dowon.ai_dire_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Spring Security 설정 클래스입니다.
 */
@Configuration
@Profile("!test") // 'test' 프로파일이 아닐 때만 활성화
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/diaries/**").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(withDefaults()); // 기본 설정 사용
        return http.build();
    }

    /**
     * JwtDecoder 빈을 정의합니다.
     *
     * @return JwtDecoder 객체
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri("http://localhost:8080/.well-known/jwks.json").build();
    }
}
