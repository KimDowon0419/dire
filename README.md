# 개발 내용 정리

## 1. 프로젝트 개요

### 1.1. 프로젝트명: AI 일기 쓰기 서비스 + 예약, 예매 서비스
- **목적**:  
  사용자가 일기를 작성하고, AI 모델을 통해 일기를 분석하거나 생성하는 기능과 공연, 식당 등을 예매·예약할 수 있는 기능을 제공합니다.

- **구성 요소 (개발 단계)**:
    - **auth-service**  
      사용자 인증 및 JWT 토큰 발급을 담당.
    - **ai-diary-service**  
      일기 관리 및 AI 모델과의 통신을 담당.
    - **common**  
      글로벌 예외처리를 담당, 각 서비스에서 jar 형태로 사용.

### 1.2. 기술 스택
- **언어 및 프레임워크**: Java, Spring Boot
- **보안**: Spring Security, JWT
- **데이터베이스**: H2 (개발 단계)
- **빌드 도구**: Gradle

---

## 2. DevOps & 인프라

### 2.1. 로컬 쿠버네티스 클러스터
- **환경**: macOS + [Multipass] VM
- **구성**: Control Plane 1대, Worker Node 3대
- **런타임/CNI**: CRI-O, Flannel
- **특이사항**
    - macOS NAT/VPN 환경에서 DNS 이슈 발생 → CoreDNS 포워딩 설정(예: 8.8.8.8)
    - VM 중지 후 재시작해도 데이터를 보존하려면, 쿠버네티스에서 PVC를 사용 (Multipass 디스크 영속성)

### 2.2. Jenkins 배포 (Helm 이용)
- **Helm 차트**로 **Jenkins** 배포 (NodePort)
    - Jenkins init 시 외부 DNS(“updates.jenkins.io”) 접근에 유의 (CoreDNS 설정 필요)
- **주요 포인트**
    - **persistence.enabled** 설정 여부로 Jenkins 데이터(플러그인, 설정) 보존 가능
    - NodePort (예: 30080)로 호스트 브라우저에서 접근

---

> **참고**
> - 이 환경은 개발·테스트 용도로 구성하였으며, 실제 운영 단계에서는 보안/네트워크를 추가 고려해야 합니다.