package com.dowon.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 보안 설정을 위한 클래스입니다.
 */
@Configuration
public class SecurityConfig {

    /**
     * 비밀번호 암호화를 위한 PasswordEncoder 빈을 생성합니다.
     *
     * @return PasswordEncoder 객체
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * HTTP 보안 설정을 구성합니다.
     *
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain 객체
     * @throws Exception 예외
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/signup", "/api/auth/login", "/api/auth/refresh-token", "/api/auth/logout", "/api/auth/.well-known/jwks.json").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
