
# TASKS 프로젝트 개발 가이드

## 1. 개요
- 시스템팀, IT운영팀의 업무 기록 및 협업을 위한 웹 서비스
- 폐쇄망 환경에서 동작하며, Docker 기반으로 배포
- 주요 기술스택: Spring Boot, Thymeleaf, JPA, MariaDB, Gradle

## 2. 주요 기능
- **작업(Task) 관리**: 작업 등록, 담당자 지정, 예정일 설정, 상태 변경
- **프로젝트 관리**: PM, 오픈일, 체크리스트 기반 진행률 관리
- **IP 관리**: 대역대 등록, IP별 설명 및 메타데이터 입력, 서버 상태 확인
- **DNS 관리**: 입력된 도메인에 대해 내부 DNS 조회 및 사용 여부 확인
- **웹 서비스 목록 관리**: 내부 관리 서비스 URL/담당자/설명 관리
- **근무표 및 연차 관리**: 엑셀 기반의 근무/연차 스케줄 관리
- **관리자 기능**: 변경 이력 열람, 사용자 관리, 히스토리 확인
- **알림 기능**: 메일 및 SMS 연동 가능 구조 포함

## 3. 시스템 아키텍처
```
사용자 → Spring Boot Web App → Docker Container → MariaDB
                             → Thymeleaf + HTML/CSS
```

## 4. DB 스키마
- DB명: `tasks`
- 테이블 예시:
  - `users`, `tasks`, `projects`, `project_tasks`
  - `ip_ranges`, `ip_addresses`, `dns_records`, `weblist`

## 5. 배포 방법
- Gradle 빌드: `./gradlew bootJar`
- 실행: `docker-compose up -d` 또는 `java -jar build/libs/tasks.jar`
- 배포 디렉토리 구조:
  - `/app/tasks/releases`, `/current`, `/backup`

## 6. 파일 구성
- `src/main`: Java 소스
- `templates/`: Thymeleaf 템플릿
- `static/`: 정적 파일 (JS/CSS/이미지)
- `schema.sql`: DB 초기 스크립트
- `README.md`: 프로젝트 설명
- `Dockerfile`, `application.yml`, `build.gradle`

## 7. 기타 사항
- 한국어 UI/UX 반영
- GitHub Actions 및 Slack 연동 기반 CI/CD
- 사용자 인증, 관리자 권한 구분, 세션 만료 처리 등 구현됨

## 8. 개발 및 유지보수
- Github: [https://github.com/wlsejrdyd/tasks](https://github.com/wlsejrdyd/tasks)
- 유지보수 방식: 기능 확장 시 Controller, Service, Repository 레이어 추가
