# TASKS 프로젝트 기술 명세서 (v1.1 기준)

---

## 1. 프로젝트 개요
- **프로젝트명**: TASKS
- **버전**: v1.1
- **목적**: 사내 프로젝트 및 리소스(IP, DNS, 내부 서비스 등)를 한눈에 확인하고 관리할 수 있는 대시보드 웹 시스템 구축

---

## 2. 기술 스택
- **백엔드**: Spring Boot 3.2.0
- **언어**: Java 17
- **템플릿 엔진**: Thymeleaf
- **ORM**: Spring Data JPA (with Hibernate)
- **DB**: MariaDB 10.5
- **빌드 툴**: Gradle
- **프론트**: Tailwind CSS 기반 반응형 구조
- **보안**: Spring Security (세션 로그인 방식)

---

## 3. 개발/운영 환경
- **운영체제**: Rocky Linux 9.x
- **JDK**: OpenJDK 17
- **데이터베이스**: MariaDB 10.5 (로컬 or 내부망 연결)
- **포트 구성**: 기본 8080포트
- **디렉토리 예시**:
  - `/app/tasks/dev-tasks/`: 프로젝트 루트
  - `/app/tasks/releases/`: 빌드 파일 릴리즈 경로
  - `/app/tasks/current/`: 심볼릭 링크 운영 디렉토리

---

## 4. 구현 기능 현황

| 기능 이름             | 상태   | 설명                                                |
|----------------------|--------|-----------------------------------------------------|
| 로그인/회원가입      | ✅ 완료 | 기본 인증 구현 (Spring Security, 세션 기반)         |
| 대시보드             | ✅ 완료 | 프로젝트/IP/DNS/서비스/근태 현황 카드형 표시        |
| 프로젝트 관리        | ✅ 완료 | CRUD 기능 구현                                      |
| IP 관리              | ✅ 완료 | CRUD 기능 구현                                      |
| DNS 도메인 관리      | ✅ 완료 | CRUD 기능 구현                                      |
| 내부 서비스 관리     | ✅ 완료 | CRUD 기능 구현                                      |
| 관리자 페이지        | ❌ 예정 | 회원 관리, 삭제/검색 기능 추가 예정                 |
| 사용자 권한 관리     | ❌ 예정 | ROLE_ADMIN, ROLE_USER 기반 기능 분리 예정           |
| 엑셀 다운로드 기능   | ❌ 예정 | 각 관리 항목별 Excel 파일 다운로드 기능             |

---

## 5. DB 테이블 구조 요약

- **users**
  - id, username, password, role 등

- **projects**
  - id, name, start_date, status

- **ips**
  - id, address, description, status

- **dns**
  - id, name, created_at

- **services**
  - id, name, url, description, status

- **attendances**
  - id, name, work_date, check_in, check_out

---

## 6. 디렉토리 구조 요약

```
tasks/
 ┣ controller/       → 컨트롤러 계층
 ┣ service/          → 서비스 계층
 ┣ entity/           → JPA 엔티티
 ┣ model/            → DTO/화면표시용 클래스
 ┣ repository/       → JPA 인터페이스
 ┣ config/           → 시큐리티 및 설정
 ┗ resources/
    ┣ templates/     → HTML 템플릿
    ┗ static/        → CSS, JS
```

---

## 7. 빌드 및 실행

```bash
./gradlew clean bootJar
java -jar build/libs/dev-tasks-1.1.0.jar
```

---

## 8. 주요 라우팅 경로

| 경로            | 설명                          |
|-----------------|-------------------------------|
| `/login`        | 로그인 페이지                 |
| `/signup`       | 회원가입                      |
| `/dashboard`    | 대시보드 메인                 |
| `/projects`     | 프로젝트 관리 페이지          |
| `/ips`          | IP 관리 페이지                |
| `/dns`          | DNS 도메인 관리 페이지        |
| `/services`     | 내부 서비스 관리 페이지       |

---

## 9. 이후 추가될 기능 (v1.2~ 기획)

- [ ] 관리자 전용 회원 관리 페이지
- [ ] 무한 스크롤 적용 (대시보드, 목록 페이지)
- [ ] 엑셀 다운로드 기능
- [ ] 사용자 권한 (Admin/사용자)
- [ ] 이메일 인증 기반 회원가입
- [ ] UI 공통 모듈화 (sidebar/header/footer 분리)

---

(이 문서는 추후 기능이 추가될 때마다 함께 업데이트할 것)