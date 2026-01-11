# Krocs

<div align="center">
<img width="2605" height="1563" alt="Banner" src="https://github.com/user-attachments/assets/77223f94-473b-4844-838b-d958b2989f6b" />

</div>

# Krocs: 계획의 루프를 완성하는 시간 코치
목표 설정부터 실행, 회고까지 **계획의 전체 루프**를 지원하는 시간 관리 서비스
> **팀 핫팩** <br/> **v1 개발기간: 2025.07 ~ 2025.12** <br/> **2025 Seeds 최우수상**

## 배포 주소

> **서비스 페이지** : [https://www.krocs.life/](https://www.krocs.life/) <br>
> **API 명세서** : [https://api.krocs.life/swagger-ui/index.html](https://api.krocs.life/swagger-ui/index.html) <br>


## 팀 소개

| <img src="https://github.com/Cycrypto.png" width="120" /> | <img src="https://github.com/yeo-li.png" width="120" /> | <img src="https://github.com/soheeGit.png" width="120" /> | <img src="https://github.com/gawoooon.png" width="120" /> | <img src="https://github.com/qorgkfbs123.png" width="120" /> |
| :---: | :---: | :---: | :---: | :---: |
| [박준하](https://github.com/Cycrypto) | [박성열](https://github.com/yeo-li) | [진소희](https://github.com/soheeGit) | [이가원](https://github.com/gawoooon) | [백하현](https://github.com/qorgkfbs123) |
| 팀장, PM | 백엔드 | 백엔드, 인프라 | 백엔드 | 프론트엔드 |


## 주요 기능 ✨

### 1) 계층적 목표 관리
- 대목표 + 세부목표의 **2단계 계층 구조**로 목표를 체계적으로 관리
- **실시간 진행률 자동 계산**
- 키워드 검색 / 날짜 범위 / 상태별 필터링 제공

### 2) 캘린더 기반 일정 관리
- 일/월 단위 일정 조회 및 직관적인 캘린더 뷰 제공
- 종일 일정과 시간 지정 일정을 구분
- 세부목표와 연동하여 계획 수립 가능

### 3) 회고 시스템
- 목표 완료 후 성공/실패 요인을 분석하고 기록
- 과거 회고를 누적 저장하여 성장 과정을 추적

### 4) 템플릿
- 자주 사용하는 목표를 템플릿으로 저장/재사용하여 시간 절약


## 기능별 화면 구성 📺

| 1) 계층적 목표 관리 | 2) 캘린더 기반 일정 관리 |
| :---: | :---: |
| <img alt="goal-3" src="https://github.com/user-attachments/assets/07df14fd-3cb7-4acb-b84d-b09679519b26" width="460" /> | <img alt="goal-4" src="https://github.com/user-attachments/assets/0e0e0e63-7fcf-4513-b0dc-f5cd9a7b8be2" width="460" /> |

| 3) 회고 시스템 | 4) 템플릿 |
| :---: | :---: |
| <img alt="goal-6" src="https://github.com/user-attachments/assets/72c5a698-35b6-4a9d-909e-6b4eba11093e" width="460" /> | <img alt="goal-5" src="https://github.com/user-attachments/assets/a79a773e-e2f3-430c-a429-d47756ac0e1f" width="460" /> |

---

## 기술 아키텍처 🏗️

- FE: GitHub Actions → Vercel 배포
- BE: GitHub Actions → Docker build/push → GCP Compute Engine 배포
- Reverse Proxy: Nginx + Let’s Encrypt(HTTPS)
- DB: Cloud SQL(PostgreSQL), Redis
<img width="1157" height="575" alt="스크린샷 2026-01-11 오후 2 29 15" src="https://github.com/user-attachments/assets/58219fb6-9b50-4fb0-84c9-ce6c8e228d3f" />

---

## 기술 스택 🧰

### Frontend
![Next JS](https://img.shields.io/badge/Next-black?style=for-the-badge&logo=next.js&logoColor=white)
![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![TypeScript](https://img.shields.io/badge/typescript-%23007ACC.svg?style=for-the-badge&logo=typescript&logoColor=white)
![ESLint](https://img.shields.io/badge/ESLint-4B3263?style=for-the-badge&logo=eslint&logoColor=white)
![TailwindCSS](https://img.shields.io/badge/tailwindcss-%2338B2AC.svg?style=for-the-badge&logo=tailwind-css&logoColor=white)
![NodeJS](https://img.shields.io/badge/node.js-6DA55F?style=for-the-badge&logo=node.js&logoColor=white)

### Backend
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring%20boot-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/spring%20security-%236DB33F.svg?style=for-the-badge&logo=springsecurity&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)

### Infrastructure
![Google Cloud](https://img.shields.io/badge/GoogleCloud-%234285F4.svg?style=for-the-badge&logo=google-cloud&logoColor=white)
![Compute Engine](https://img.shields.io/badge/Compute%20Engine-%234285F4.svg?style=for-the-badge&logo=google-cloud&logoColor=white)
![Cloud SQL](https://img.shields.io/badge/Cloud%20SQL-%234285F4.svg?style=for-the-badge&logo=google-cloud&logoColor=white)
![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)
![Nginx](https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white)
![Vercel](https://img.shields.io/badge/vercel-%23000000.svg?style=for-the-badge&logo=vercel&logoColor=white)

### External APIs
![OAuth2](https://img.shields.io/badge/OAuth2-3C3C3C?style=for-the-badge&logo=oauth&logoColor=white)
![Google](https://img.shields.io/badge/google-4285F4?style=for-the-badge&logo=google&logoColor=white)
![Kakao](https://img.shields.io/badge/Kakao-FFCD00?style=for-the-badge&logo=kakao&logoColor=000000)
![Naver](https://img.shields.io/badge/naver-%2303C75A.svg?style=for-the-badge&logo=naver&logoColor=white)

### Communication
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white)
![Discord](https://img.shields.io/badge/Discord-%235865F2.svg?style=for-the-badge&logo=discord&logoColor=white)

---

## 시작 가이드 🚀

### Requirements
- Next.js: 15.5.9
- React: 19.1.1
- TypeScript: 5.9.2
- ESLint: 9.x
- Tailwind CSS: 4.x
- Node.js (프론트 로컬 실행 시)
  
- Java 21
- Spring Boot 3.5.3
- Docker / Docker Compose
- PostgreSQL / Redis

### Installation
```bash
git clone https://github.com/seeds-hotpack/krocs-backend.git
cd https://github.com/seeds-hotpack/krocs-backend.git
```


### 디렉토리 구조

```
## Directory Structure (Backend)

```bash
krocs-backend/
├── backend/                         # Spring Boot 애플리케이션
│   ├── docker/                      # 로컬/개발용 컨테이너 설정
│   │   └── redis/
│   │       └── docker-compose.yml
│   ├── gradle/                      # Gradle Wrapper
│   │   └── wrapper/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/hotpack/krocs/
│   │   │   │   ├── domain/          # 도메인별 기능 모듈
│   │   │   │   │   ├── auth/        # 인증/세션/토큰
│   │   │   │   │   ├── goals/       # 목표/세부목표
│   │   │   │   │   ├── plans/       # 계획/세부계획
│   │   │   │   │   ├── retrospectives/ # 회고(요인/통계)
│   │   │   │   │   ├── stopwatch/   # 스톱워치 로그
│   │   │   │   │   ├── templates/   # 템플릿/서브템플릿
│   │   │   │   │   ├── timeline/    # 타임라인 조회
│   │   │   │   │   └── user/        # 유저 도메인
│   │   │   │   ├── global/          # 공통 설정/보안/응답/헬스체크 등
│   │   │   │   └── KrocsApplication.java
│   │   │   └── resources/
│   │   │       ├── db/              # Liquibase changelog
│   │   │       ├── krocs-config/    # 환경별 properties (dev/prod)
│   │   │       └── messages_ko.properties
│   │   └── test/                    # 도메인별 단위/통합 테스트
│   ├── Dockerfile                   # 백엔드 도커 이미지 빌드
│   ├── build.gradle
│   ├── settings.gradle
│   ├── gradlew
│   └── LIQUIBASE_README.md
├── deploy/                          # 배포/운영 구성
│   ├── nginx/                       # Reverse Proxy 설정
│   │   ├── conf.d/default.conf
│   │   └── nginx.conf
│   ├── scripts/                     # SSL 발급/갱신 스크립트
│   │   ├── setup-ssl.sh
│   │   └── renew-ssl.sh
│   ├── docker-compose.yml           # 로컬/기본 구성
│   └── docker-compose.prod.yml      # 운영 구성
├── scripts/                         # 서브모듈 세팅/업데이트 스크립트
│   ├── setup-submodules.sh
│   └── update-submodules.sh
├── makefile
└── ReadMe.md
```
