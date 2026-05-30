# NSIGHT 메시징 관리 서비스 프로젝트

## 1. 목적

이 프로젝트는 NSIGHT 정보계 기준의 **메시징 관리 서비스** 샘플 프로젝트입니다. 단순 CRUD가 아니라 NSIGHT 환경설정 기준안과 Spring Boot 계층구조 기준을 반영하여 다음을 포함합니다.

- 메시지 등록/조회 API
- 메시지 등록 화면 `/messages`
- Controller → Facade → Service → Rule → Thing → DAO/Mapper 구조
- 표준 응답 전문 구조: Header / Body / Control / Security / Error
- GUID / TraceId / MDC 기반 로그 추적
- GlobalExceptionHandler 기반 표준 오류응답
- HikariCP, MyBatis, Tomcat, Session, Transaction, Timeout 환경설정
- H2 Local DB, 운영 전환용 profile 구조

## 2. 실행 방법

```bash
cd nsight-message-mgmt-service
mvn spring-boot:run
```

접속 URL:

```text
http://localhost:8080/messages
http://localhost:8080/h2-console
```

H2 접속 정보:

```text
JDBC URL: jdbc:h2:mem:nsightmsg
User: sa
Password: 
```

## 3. API 예시

```bash
curl -X POST http://localhost:8080/api/v1/messages \
  -H "Content-Type: application/json" \
  -H "X-GUID: 20260530-MSG-000001" \
  -H "X-USER-ID: ARCHITECT" \
  -d '{
    "messageCode": "MSG_NOTICE_001",
    "messageName": "공지 메시지",
    "messageType": "INFO",
    "channelCode": "WEBTOPSUITE",
    "locale": "ko_KR",
    "messageContent": "NSIGHT 메시징 관리 서비스 등록 테스트입니다.",
    "displayStartAt": "2026-05-30T09:00:00",
    "displayEndAt": "2026-12-31T18:00:00",
    "useYn": "Y"
  }'
```

## 4. 운영 전환 시 보완 사항

- H2 DB를 Oracle/RDW 업무 DB로 전환
- `/messages` 화면을 WebTopSuite 화면 표준으로 변환
- SSO/권한/감사/마스킹 공통모듈 연계
- CruzAPIM 연계가 필요한 메시지 배포/공지 전파 기능 추가
- 로그 수집, APM, 운영 대시보드 연계
- Message 승인/배포/폐기 Life-cycle 추가

## 5. 주요 URL

| URL | 설명 |
|---|---|
| `/messages` | 메시지 등록 화면 |
| `/api/v1/messages` | 메시지 목록 조회 / 등록 API |
| `/api/v1/messages/{messageId}` | 메시지 상세 조회 API |
| `/actuator/health` | Health Check |
