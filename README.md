# 🛠 TASKS 프로젝트

**TASKS**는 조직의 프로젝트, IP, DNS, 내부 서비스 등의 자산을 통합 관리하기 위한 웹 기반 시스템입니다.  
Spring Boot와 Gradle 기반으로 구축되었으며, 실무 환경에 적합한 직관적인 대시보드와 자산 CRUD 기능을 제공합니다.

---

## 🚀 기술 스택

- Java 17
- Spring Boot 3.x
- Thymeleaf
- Spring Security
- MySQL (MariaDB 호환)
- Gradle
- HTML/CSS/JS (Vanilla + Thymeleaf)
- [추가예정] GitHub Actions, Excel/PDF Export

---

## 📌 버전 히스토리

| 버전 | 주요 기능 |
|------|-----------|
| v1.0 | 사용자 로그인 / 회원가입 / 대시보드 레이아웃 구축 |
| v1.1 | 프로젝트 관리 기능 CRUD |
| v1.2 | IP 관리 / DNS 도메인 관리 / 내부 서비스 관리 (전체 리셋 후 다시시작)|
| v1.3 | 사용자 로그인 / 회원가입 / 대시보드 레이아웃 / 프로젝트 관리 구축 |
| v1.4 | IP 대역별 카드 시각화, 수정자/수정일 자동 기록, 대시보드에 IP 상태 요약 카드 추가 |

---

## 🔑 핵심 기능 요약

### 사용자 인증
- Spring Security 기반 로그인/로그아웃
- 역할: `ROLE_USER`, `ROLE_ADMIN`

### 프로젝트 관리
- 프로젝트 생성, 수정, 삭제, 진행상태별 구분(진행중/완료/보류)

### IP 관리
- CIDR 기반 IP 대역 생성
- IP 중복 방지
- IP 수정 시 `수정일` 자동 갱신, `작업자` 자동 입력
- 검색 시 카드 형태로 개별 IP 조회 가능

### DNS / 내부 서비스
- 일반 CRUD 방식으로 등록 및 수정 가능

### 대시보드
- 프로젝트 상태별 개수 요약
- IP 통계 요약 카드 (총 대역, 활성 호스트, 비활성 호스트)
- 향후 추가될 리소스들의 통계 카드 영역 확보

---

## 📂 데이터베이스 테이블 개요

- `user`, `project`, `ip_range`, `ip_address`
- 상태 구분 필드는 ENUM 사용
- IP 주소는 `range_id`로 연결되고, 중복 방지를 위한 유니크 제약 조건 포함

---

## 🧩 향후 계획

- Excel / PDF 다운로드 기능
- 로그 상세 보기 및 관리자 권한 UI 확장
- GitHub Actions 기반 CI/CD 배포 자동화
- Slack 알림 연동

