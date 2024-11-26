package com.dowon.ai_dire_service.controller;

import com.dowon.ai_dire_service.domain.Diary;
import com.dowon.ai_dire_service.service.DiaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * @return 생성된 일기 객체
     */
    @PostMapping
    public ResponseEntity<Diary> createDiary(@Validated @RequestBody Diary diary) {
        // TODO: 인증된 사용자 ID를 설정해야 합니다.
        // diary.setUserId(authenticatedUserId);

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

    /**
     * 일기를 수정합니다.
     *
     * @param id    수정할 일기의 ID
     * @param diary 수정할 내용이 담긴 일기 객체
     * @return 수정된 일기 객체
     */
    @PutMapping("/{id}")
    public ResponseEntity<Diary> updateDiary(@PathVariable Long id, @Validated @RequestBody Diary diary) {
        try {
            // TODO: 인증된 사용자 확인 로직 필요
            Diary updatedDiary = diaryService.updateDiary(id, diary);
            return ResponseEntity.ok(updatedDiary);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 일기를 삭제합니다.
     *
     * @param id 삭제할 일기의 ID
     * @return 응답 결과
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(@PathVariable Long id) {
        // TODO: 인증된 사용자 확인 로직 필요
        diaryService.deleteDiary(id);
        return ResponseEntity.noContent().build();
    }
}
