package com.dowon.auth_service.controller;

import com.dowon.auth_service.domain.User;
import com.dowon.auth_service.model.JwtAuthenticationResponse;
import com.dowon.auth_service.model.LoginRequest;
import com.dowon.auth_service.model.SignUpRequest;
import com.dowon.auth_service.repository.UserRepository;
import com.dowon.auth_service.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * 인증 관련 API를 제공하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(JwtTokenProvider jwtTokenProvider,
                          UserRepository userRepository,
                          BCryptPasswordEncoder passwordEncoder) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 회원가입 처리 메서드입니다.
     *
     * @param signUpRequest 회원가입 요청 객체
     * @return 응답 엔티티
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        // 사용자 이름 중복 확인
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 사용 중인 아이디입니다.");
        }

        // 사용자 객체 생성 및 저장
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRoles(Collections.singletonList("ROLE_USER"));

        userRepository.save(user);

        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    /**
     * 로그인 처리 메서드입니다.
     *
     * @param loginRequest 로그인 요청 객체
     * @return JWT 토큰을 포함한 응답 엔티티
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            // 사용자 이름으로 사용자 조회
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

            // 비밀번호 확인
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 잘못되었습니다.");
            }

            // 사용자 ID와 역할 가져오기
            Long userId = user.getId();
            List<String> roles = user.getRoles();

            // JWT 토큰 생성
            String token = jwtTokenProvider.generateToken(userId, roles);

            return ResponseEntity.ok(new JwtAuthenticationResponse(token));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 잘못되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }
}
