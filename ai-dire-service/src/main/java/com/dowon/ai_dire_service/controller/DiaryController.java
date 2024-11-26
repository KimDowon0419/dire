package com.dowon.ai_dire_service.controller;

import com.dowon.ai_dire_service.domain.Diary;
import com.dowon.ai_dire_service.service.DiaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 일기 관련 REST API를 제공하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/api/diaries")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    /**
     * 새로운 일기를 작성합니다.
     *
     * @param diary 작성할 일기 객체
     * @param jwt   인증된 사용자 정보가 담긴 JWT 객체
     * @return 생성된 일기 객체
     */
    @PostMapping
    public ResponseEntity<Diary> createDiary(@Validated @RequestBody Diary diary, @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        diary.setUserId(userId);

        Diary createdDiary = diaryService.createDiary(diary);
        return ResponseEntity.ok(createdDiary);
    }

    /**
     * 모든 일기 목록을 조회합니다.
     *
     * @return 일기 리스트
     */
    @GetMapping
    public ResponseEntity<List<Diary>> getAllDiaries() {
        List<Diary> diaries = diaryService.getAllDiaries();
        return ResponseEntity.ok(diaries);
    }

    /**
     * 특정 ID의 일기를 조회합니다.
     *
     * @param id 조회할 일기의 ID
     * @return 일기 객체
     */
    @GetMapping("/{id}")
    public ResponseEntity<Diary> getDiaryById(@PathVariable Long id) {
        return diaryService.getDiaryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Diary> updateDiary(
            @PathVariable Long id,
            @Validated @RequestBody Diary diary,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            // 인증된 사용자 ID 추출
            Long userId = Long.parseLong(jwt.getSubject());

            // 수정하려는 일기 가져오기
            Optional<Diary> existingDiaryOpt = diaryService.getDiaryById(id);
            if (existingDiaryOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Diary existingDiary = existingDiaryOpt.get();

            // 일기의 소유자인지 확인
            if (!existingDiary.getUserId().equals(userId)) {
                // 소유자가 아닐 경우 403 Forbidden 반환
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // 일기 내용 업데이트
            diary.setUserId(userId); // 사용자 ID 설정 (보안 상 중요)
            Diary updatedDiary = diaryService.updateDiary(id, diary);
            return ResponseEntity.ok(updatedDiary);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * 일기를 삭제합니다.
     *
     * @param id  삭제할 일기의 ID
     * @param jwt 인증된 사용자 정보가 담긴 JWT 객체
     * @return 응답 결과
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        // 인증된 사용자 ID 추출
        Long userId = Long.parseLong(jwt.getSubject());

        // 삭제하려는 일기 가져오기
        Optional<Diary> existingDiaryOpt = diaryService.getDiaryById(id);
        if (existingDiaryOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Diary existingDiary = existingDiaryOpt.get();

        // 일기의 소유자인지 확인
        if (!existingDiary.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 일기 삭제
        diaryService.deleteDiary(id);
        return ResponseEntity.noContent().build();
    }
}
