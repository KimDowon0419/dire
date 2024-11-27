package com.dowon.ai_dire_service.repository;

import com.dowon.ai_dire_service.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 일기 데이터를 관리하는 리포지토리 인터페이스입니다.
 */
@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    /**
     * 특정 사용자의 모든 일기를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 일기 리스트
     */
    List<Diary> findByUserId(Long userId);

    /**
     * 특정 ID와 사용자 ID에 해당하는 일기를 조회합니다.
     *
     * @param id     일기 ID
     * @param userId 사용자 ID
     * @return 일기 객체 (Optional)
     */
    Optional<Diary> findByIdAndUserId(Long id, Long userId);
}
