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
     * 특정 ID의 일기를 조회합니다.
     *
     * @param id 조회할 일기의 ID
     * @return 일기 객체
     */
    @Transactional(readOnly = true)
    public Optional<Diary> getDiaryById(Long id) {
        return diaryRepository.findById(id);
    }

    /**
     * 모든 일기 목록을 조회합니다.
     *
     * @return 일기 리스트
     */
    @Transactional(readOnly = true)
    public List<Diary> getAllDiaries() {
        return diaryRepository.findAll();
    }

    /**
     * 일기를 수정합니다.
     *
     * @param id    수정할 일기의 ID
     * @param diary 수정할 내용이 담긴 일기 객체
     * @return 수정된 일기 객체
     * @throws IllegalArgumentException 존재하지 않는 일기인 경우 예외 발생
     */
    public Diary updateDiary(Long id, Diary diary) {
        Diary existingDiary = diaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일기입니다."));

        existingDiary.setTitle(diary.getTitle());
        existingDiary.setContent(diary.getContent());
        // updatedAt은 자동으로 갱신됩니다.

        return existingDiary;
    }

    /**
     * 일기를 삭제합니다.
     *
     * @param id 삭제할 일기의 ID
     */
    public void deleteDiary(Long id) {
        diaryRepository.deleteById(id);
    }
}
