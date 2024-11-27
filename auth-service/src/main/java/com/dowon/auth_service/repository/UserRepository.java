package com.dowon.auth_service.repository;

import com.dowon.auth_service.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 데이터를 관리하는 리포지토리 인터페이스입니다.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자 이름으로 사용자 정보를 조회합니다.
     *
     * @param username 사용자 이름
     * @return 사용자 객체 (Optional)
     */
    Optional<User> findByUsername(String username);

    /**
     * 사용자 이름이 이미 존재하는지 확인합니다.
     *
     * @param username 사용자 이름
     * @return 존재 여부
     */
    boolean existsByUsername(String username);
}
