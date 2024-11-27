package com.dowon.ai_dire_service.service;

import com.dowon.ai_dire_service.domain.Diary;
import com.dowon.ai_dire_service.repository.DiaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 일기와 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@Transactional
public class DiaryService {

    private final DiaryRepository diaryRepository;

    public DiaryService(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    /**
     * 새로운 일기를 작성합니다.
     *
     * @param diary 작성할 일기 객체
     * @return 저장된 일기 객체
     */
    public Diary createDiary(Diary diary) {
        return diaryRepository.save(diary);
    }

    /**
     * 특정 사용자의 모든 일기 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 일기 리스트
     */
    @Transactional(readOnly = true)
    public List<Diary> getDiariesByUserId(Long userId) {
        return diaryRepository.findByUserId(userId);
    }

    /**
     * 특정 ID와 사용자 ID에 해당하는 일기를 조회합니다.
     *
     * @param id     일기 ID
     * @param userId 사용자 ID
     * @return 일기 객체
     * @throws IllegalArgumentException 일기가 존재하지 않거나 권한이 없는 경우
     */
    @Transactional(readOnly = true)
    public Diary getDiaryByIdAndUserId(Long id, Long userId) {
        return diaryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없거나 접근 권한이 없습니다."));
    }

    /**
     * 일기를 수정합니다.
     *
     * @param id     수정할 일기의 ID
     * @param diary  수정할 내용이 담긴 일기 객체
     * @param userId 사용자 ID
     * @return 수정된 일기 객체
     * @throws IllegalArgumentException 일기가 존재하지 않거나 권한이 없는 경우
     */
    public Diary updateDiary(Long id, Diary diary, Long userId) {
        Diary existingDiary = getDiaryByIdAndUserId(id, userId);

        existingDiary.setTitle(diary.getTitle());
        existingDiary.setContent(diary.getContent());
        // updatedAt은 자동으로 갱신됩니다.

        return existingDiary;
    }

    /**
     * 일기를 삭제합니다.
     *
     * @param id     삭제할 일기의 ID
     * @param userId 사용자 ID
     * @throws IllegalArgumentException 일기가 존재하지 않거나 권한이 없는 경우
     */
    public void deleteDiary(Long id, Long userId) {
        Diary existingDiary = getDiaryByIdAndUserId(id, userId);
        diaryRepository.delete(existingDiary);
    }
}
