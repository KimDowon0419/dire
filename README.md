# 개발 내용 정리

## 1. 프로젝트 개요
### 1.1. 프로젝트명: AI 일기 쓰기 서비스 + 예약, 예매 서비스
- **목적**:  
  사용자가 일기를 작성하고, AI 모델을 통해 일기를 분석하거나 생성하는 기능과 공연, 식당 등을 예매, 예약할 수 있는 기능을 제공합니다.

- **구성 요소 (개발 단계)**:
    - **auth-service**:  
      사용자 인증 및 JWT 토큰 발급을 담당하는 마이크로서비스.
    - **ai-diary-service**:  
      일기 관리 및 AI 모델과의 통신을 담당하는 마이크로서비스.

---

### 1.2. 기술 스택
- **언어 및 프레임워크**:  
  Java, Spring Boot

- **보안**:  
  Spring Security, JWT (Json Web Token)

- **데이터베이스**:  
  H2 (In-Memory Database) 사용 (개발 단계)

- **빌드 도구**:  
  Gradle
