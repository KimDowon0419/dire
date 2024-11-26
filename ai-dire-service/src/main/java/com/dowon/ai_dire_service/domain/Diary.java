package com.dowon.ai_dire_service.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 일기 정보를 나타내는 엔티티 클래스입니다.
 */
@Entity
@Table(name = "diaries")
@Getter
@Setter
@NoArgsConstructor
public class Diary {

    /**
     * 일기 ID (자동 생성)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 일기 제목
     */
    @NotBlank(message = "제목은 필수 입력 값입니다.")
    private String title;

    /**
     * 일기 내용
     */
    @Lob
    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;

    /**
     * 작성자 ID
     */
    private Long userId;

    /**
     * 생성 일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     */
    private LocalDateTime updatedAt;

    /**
     * 엔티티 저장 전 호출되는 콜백 메서드로 생성 일시와 수정 일시를 설정합니다.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * 엔티티 업데이트 전 호출되는 콜백 메서드로 수정 일시를 갱신합니다.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
