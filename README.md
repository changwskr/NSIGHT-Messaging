# NSIGHT-Messaging

NSIGHT 정보계 기준 **메시징 관리 서비스** 샘플 프로젝트입니다.

- **Java 17** · **Spring Boot 3.3.5** · **MyBatis** · H2(in-memory)
- 빌드: **Maven** (`pom.xml`) · **Gradle** (`build.gradle`) 병행

## 1. 목적

단순 CRUD가 아니라 NSIGHT 환경설정 기준안과 Spring Boot 계층구조 기준을 반영하여 다음을 포함합니다.

- 메시지·파일·트랜잭션 등록/조회 API 및 화면
- 레거시: Controller → Facade → Service → Rule → Thing → DAO/Mapper
- **Xpilot 모듈**: AC(Application Controller) → AS(Application Service) → DC(Data Component), 공통 `util` 패키지
- 표준 응답 전문: Header / Body / Control / Security / Error
- GUID / TraceId / MDC 기반 로그 추적
- GlobalExceptionHandler 기반 표준 오류응답
- HikariCP + MyBatis, Tomcat, Session, Transaction, Timeout 환경설정
- H2 Local DB (`schema.sql` / `data.sql` 자동 로드), 운영 전환용 profile 구조

## 2. 패키지 구조 (Xpilot / capacitymgr)

| 모듈                   | 패키지                      | 역할                                             |
| ---------------------- | --------------------------- | ------------------------------------------------ |
| `capacitymgr`          | `ac` / `as` / `dc` / `util` | 계정 관리 샘플 (기준 모듈)                       |
| `xpilotmessaging`      | 동일                        | 메시지 CRUD (AC/AS/DC)                           |
| `xpilotfile`           | 동일                        | 파일 업·다운로드                                 |
| `xpilottransactionmgr` | 동일                        | 트랜잭션 전문 이력                               |
| `xpilotstyleguide`     | 동일                        | 사용자 프로필 CRUD (스타일 가이드 샘플)          |
| `xpcapacitymgr`        | 동일                        | 용량산정 CAP-010~050 (TPS·AP·WAS·DB Pool)        |
| `xpilot`               | 동일                        | traceenvironment 구조 전환 Pilot                 |
| `xpilottpcclient`      | `util`                      | HTTP JSON → xpilotmessaging API 호출 (`TpcUtil`) |

공통 유틸은 **`zcommonutil` → `util`** 로 통일되어 있습니다. (폴더 경로와 `package` 선언이 일치해야 합니다.)

## 3. 실행 방법

### 3-1. 서버 기동

**Maven**

```bash
cd nsight-message-mgmt-service
mvn spring-boot:run
```

**Gradle**

```bash
cd nsight-message-mgmt-service
./gradlew bootRun          # Windows: gradlew.bat bootRun
./gradlew build            # JAR: build/libs/nsight-message-mgmt-service-1.0.0.jar
```

**빌드 스크립트**

| 스크립트                   | 설명                   |
| -------------------------- | ---------------------- |
| `bin/build-jar.bat`        | Maven JAR 패키징       |
| `bin/build-jar-gradle.bat` | Gradle JAR 패키징      |
| `bin/pull-and-build.bat`   | develop 동기화 후 빌드 |

**접속 URL**

```text
http://localhost:8080/home/login   (admin / 1234)
http://localhost:8080/home         # 서비스 카드 포털
http://localhost:8080/h2-console
```

**H2 접속**

```text
JDBC URL: jdbc:h2:mem:nsightmsg
User: sa
Password: (비어 있음)
```

### 3-2. TPC 클라이언트 (`TpcUtil`)

서버 기동 후 xpilotmessaging API를 HTTP로 호출하는 독립 실행 클라이언트입니다.

```bash
# Maven (PowerShell에서는 bin 스크립트 권장)
mvn -q exec:java -Dexec.args="list"

# Windows 배치 (괄호 경로 대응)
bin\run-tpc-util.bat list
bin\run-tpc-util.bat get MSG001
```

기본 URL: `http://localhost:8080` · API: `/api/xpilotmessaging/messages`

## 4. 주요 화면·API URL

### 4-1. 레거시·공통

| URL                            | 설명                         |
| ------------------------------ | ---------------------------- |
| `/home/login`                  | 로그인 (ID: admin, PW: 1234) |
| `/home`                        | 메인 포털                    |
| `/messages`                    | 메시지 등록 화면             |
| `/files`                       | 파일 관리 화면               |
| `/transactionmgr`              | 트랜잭션 전문 이력 조회      |
| `/api/v1/messages`             | 메시지 목록·등록 API         |
| `/api/v1/messages/{messageId}` | 메시지 상세·수정·삭제        |
| `/api/v1/files`                | 파일 업로드·목록·다운로드    |
| `/api/v1/transaction-logs`     | 트랜잭션 로그 API            |
| `/tracedump`                   | JVM 덤프·로그 증거 분석      |
| `/oominspector`                | OOM Risk Inspector           |
| `/traceenvironment`            | 통합 환경설정 비교           |
| `/operations`                  | 운영·연계·Health Check       |
| `/actuator/health`             | Health Check                 |

로컬 파일 저장: `./data/nsight-files/yyyy/MM/dd/{uuid}.{ext}`  
입·출력 전문 파일: `./data/nsight-messages/{yyyyMMdd}/{GUID}/...`

### 4-2. Xpilot 모듈 (AC/AS/DC)

| 화면                        | API (예)                                                               |
| --------------------------- | ---------------------------------------------------------------------- |
| `/xpilotmessaging/messages` | `POST/GET /api/xpilotmessaging/messages`                               |
| `/xpilotfile/files`         | `/api/xpilotfile/files`                                                |
| `/xpilottransactionmgr`     | `/api/xpilottransactionmgr/transaction-logs`                           |
| `/xpilotstyleguide/users`   | `/api/xpilotstyleguide/users`                                          |
| `/xpcapacitymgr/plan`       | `GET /api/xpcapacitymgr/defaults`, `POST /api/xpcapacitymgr/calculate` |
| `/xpilot`                   | `/api/xpilot/pilot`, `/api/xpilot/environment`                         |

계정 샘플: `/api/capacitymgr/account`

## 5. JVM 덤프 분석 (`tracedump`)

**읽기 → 증거 → 판정 → 조치** 4단계 보고서 + 개발 명세(`docs/trace-dump-analysis-spec.md`) 연동.

```text
/tracedump
POST /api/v1/trace-dump/analyze
./data/trace-dump-evidence/sample/
```

패키지: `tracedump` — collector → parser → analyzer(Rule THR/GC/HEAP) → report

## 6. OOM 유발 점검 (`traceoompgm`)

```text
/oominspector
POST /api/oom-inspector/scans
POST /api/oom-inspector/gate?failOn=CRITICAL
```

패키지: `traceoompgm` · 명세: `docs/oom-inspector-spec.md`

## 7. JDBC 트랜잭션 샘플 (참고용)

운영 코드는 Spring `@Transactional` + MyBatis를 사용합니다.  
수동 `ThreadLocal` 트랜잭션 패턴 참고:

```text
src/main/java/com/nh/nsight/messaging/framework/transaction/txmanager/
  TransactionManager.java
  DatabaseConnection.java
  TransactionManagerSample.java
```

## 8. API 예시 (레거시 메시지)

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

## 9. 컴파일

```bash
mvn compile -DskipTests
# 또는
./gradlew compileJava
```

`.gitignore`에 Gradle/Maven 산출물(`.gradle/`, `build/`, `target/`)이 포함되어 있습니다.

## 10. 운영 전환 시 보완 사항

- H2 DB를 Oracle/RDW 업무 DB로 전환
- 화면을 WebTopSuite 표준으로 변환
- SSO/권한/감사/마스킹 공통모듈 연계
- CruzAPIM 연계(메시지 배포/공지 전파)
- 로그 수집, APM, 운영 대시보드 연계
- Message 승인/배포/폐기 Life-cycle 추가
