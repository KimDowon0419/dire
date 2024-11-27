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
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDiary);
    }

    /**
     * 모든 일기 목록을 조회합니다.
     *
     * @param jwt 인증된 사용자 정보가 담긴 JWT 객체
     * @return 일기 리스트
     */
    @GetMapping
    public ResponseEntity<List<Diary>> getAllDiaries(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        List<Diary> diaries = diaryService.getDiariesByUserId(userId);
        return ResponseEntity.ok(diaries);
    }

    /**
     * 특정 ID의 일기를 조회합니다.
     *
     * @param id  조회할 일기의 ID
     * @param jwt 인증된 사용자 정보가 담긴 JWT 객체
     * @return 일기 객체
     */
    @GetMapping("/{id}")
    public ResponseEntity<Diary> getDiaryById(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        Diary diary = diaryService.getDiaryByIdAndUserId(id, userId);
        return ResponseEntity.ok(diary);
    }

    /**
     * 일기를 수정합니다.
     *
     * @param id    수정할 일기의 ID
     * @param diary 수정할 내용이 담긴 일기 객체
     * @param jwt   인증된 사용자 정보가 담긴 JWT 객체
     * @return 수정된 일기 객체
     */
    @PutMapping("/{id}")
    public ResponseEntity<Diary> updateDiary(
            @PathVariable Long id,
            @Validated @RequestBody Diary diary,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        diary.setUserId(userId);
        Diary updatedDiary = diaryService.updateDiary(id, diary, userId);
        return ResponseEntity.ok(updatedDiary);
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
        Long userId = Long.parseLong(jwt.getSubject());
        diaryService.deleteDiary(id, userId);
        return ResponseEntity.noContent().build();
    }
}
