# NSIGHT-Messaging

NSIGHT 정보계 기준 **메시징 관리 서비스** 샘플 프로젝트입니다.

## 1. 목적

단순 CRUD가 아니라 NSIGHT 환경설정 기준안과 Spring Boot 계층구조 기준을 반영하여 다음을 포함합니다.

- 메시지 등록/조회 API
- 메시지 등록 화면 `/messages`
- Controller → Facade → Service → Rule → Thing → DAO/Mapper 구조
- 표준 응답 전문 구조: Header / Body / Control / Security / Error
- GUID / TraceId / MDC 기반 로그 추적
- GlobalExceptionHandler 기반 표준 오류응답
- HikariCP + **MyBatis** (Mapper XML / DAO), Tomcat, Session, Transaction, Timeout 환경설정
- H2 Local DB, 운영 전환용 profile 구조

## 2. JVM 덤프 분석 (`tracedump`)

**읽기 → 증거 → 판정 → 조치** 4단계 보고서 + 개발 명세(`docs/trace-dump-analysis-spec.md`) 연동.

```text
/tracedump                          # 분석 화면 (4단계 보고서)
POST /api/v1/trace-dump/analyze     # evidencePath 또는 evidenceZip
./data/trace-dump-evidence/sample/  # 샘플 증거
```

패키지: `tracedump` — collector → parser → analyzer(Rule THR/GC/HEAP) → report

## 2-1. OOM 유발 점검 (`traceoompgm`)

설계서 기반 **OOM Risk Inspector** — 소스·Mapper·설정 정적 Rule 스캔.

```text
/oominspector
POST /api/oom-inspector/scans
POST /api/oom-inspector/gate?failOn=CRITICAL
```

패키지: `com.traceoompgm` · 명세: `docs/oom-inspector-spec.md`

## 3. JDBC 트랜잭션 샘플 (참고용)

운영 코드는 Spring `@Transactional` + MyBatis를 사용합니다.  
수동 `ThreadLocal` 트랜잭션 패턴 참고:

```text
src/main/java/com/nh/nsight/messaging/framework/transaction/txmanager/
  TransactionManager.java      # 샘플 구현
  DatabaseConnection.java      # 샘플 전용 H2 연결
  TransactionManagerSample.java  # 사용 예시 main
```

## 4. 실행 방법

```bash
cd nsight-message-mgmt-service
mvn spring-boot:run
```

접속 URL:

```text
http://localhost:8080/home/login   (admin / 1234)
http://localhost:8080/home
http://localhost:8080/messages
http://localhost:8080/h2-console
```

H2 접속 정보:

```text
JDBC URL: jdbc:h2:mem:nsightmsg
User: sa
Password: 
```

## 5. API 예시

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

## 6. 운영 전환 시 보완 사항

- H2 DB를 Oracle/RDW 업무 DB로 전환
- `/messages` 화면을 WebTopSuite 화면 표준으로 변환
- SSO/권한/감사/마스킹 공통모듈 연계
- CruzAPIM 연계가 필요한 메시지 배포/공지 전파 기능 추가
- 로그 수집, APM, 운영 대시보드 연계
- Message 승인/배포/폐기 Life-cycle 추가

## 7. 주요 URL

| URL | 설명 |
|---|---|
| `/home/login` | 로그인 화면 (ID: admin, PW: 1234) |
| `/home` | 메인 화면 (서비스 카드, 로그인 필요) |
| `/` | 로그인 여부에 따라 `/home` 또는 `/home/login`으로 이동 |
| `/messages` | 메시지 등록 화면 |
| `/files` | 파일 업·다운로드 관리 화면 |
| `/tracedump` | JVM 덤프·로그 증거 분석 |
| `/api/v1/trace-dump/analyze` | 덤프 분석 API |
| `/transactionmgr` | 트랜잭션 전문(Header/Control/Security/Error) 이력 조회 |
| `/api/v1/transaction-logs` | 트랜잭션 로그 API (페이지당 3건) |
| `DELETE /api/v1/transaction-logs/{txLogId}` | 트랜잭션 1건 + 연관 전문 파일 삭제 |
| `DELETE /api/v1/transaction-logs?조건` | 조회 조건 일괄 삭제 (최대 500건) |
| `/api/v1/files` | 파일 업로드 / 목록 / 다운로드 API |
| `/api/v1/files/storage-location` | 파일 저장 루트·경로 규칙 조회 API |

로컬 파일 저장 경로: `./data/nsight-files/yyyy/MM/dd/{uuid}.{ext}`

입·출력 전문 파일 저장 (API 호출 시 자동):

```text
./data/nsight-messages/{yyyyMMdd}/{GUID}/{HHmmssSSS}_{METHOD}_{uri}-REQ.json   # 요청 전문
./data/nsight-messages/{yyyyMMdd}/{GUID}/{HHmmssSSS}_{METHOD}_{uri}-RES.json   # 응답 전문(표준 JSON)
```

저장 위치 조회: `GET /api/v1/message-logs/storage-location`
| `/api/v1/messages` | 메시지 목록 조회 / 등록 API |
| `/api/v1/messages/{messageId}` | 메시지 상세 조회 / 수정 / 삭제 API |
| `/actuator/health` | Health Check |
