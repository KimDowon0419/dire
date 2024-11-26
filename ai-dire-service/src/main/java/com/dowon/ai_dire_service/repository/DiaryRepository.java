package com.dowon.ai_dire_service.repository;

import com.dowon.ai_dire_service.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 일기 데이터를 관리하는 리포지토리 인터페이스입니다.
 */
@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    // 추가적인 쿼리 메서드를 정의할 수 있습니다.
}
