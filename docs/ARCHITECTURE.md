# 메시징 관리 서비스 아키텍처 설계

## 1. 설계 방향

메시징 관리 서비스는 단순 메시지 코드 테이블 관리가 아니라, 사용자 화면·업무 서비스·공통 오류처리·운영 공지·권한 안내·시스템 알림에서 공통으로 사용하는 메시지를 통제하는 서비스입니다.

NSIGHT 기준에서 메시징 관리는 다음 원칙을 따릅니다.

| 원칙 | 적용 방식 |
|---|---|
| 책임 경계 명확화 | 메시지 등록/조회/활성화 책임을 메시징 관리 서비스로 분리 |
| 계약 기반 연계 | API 응답은 표준 전문 구조로 반환 |
| 관측성 내재화 | 모든 요청에 GUID, TraceId, UserId를 MDC에 기록 |
| 오류처리 표준화 | GlobalExceptionHandler에서 오류코드를 표준 응답으로 변환 |
| 운영 가능성 | 메시지 활성기간, 채널, 사용여부, 감사정보를 관리 |

## 2. 컴포넌트 구조

```text
WebTopSuite / Browser
    ↓
MessagePageController       MessageController
    ↓                         ↓
MessageFacade  ── Transaction / Audit / Standard Response
    ↓
MessageService ── MessageRule
    ↓
MessageDao
    ↓
MessageMapper.xml
    ↓
TB_MSG_MESSAGE
```

## 3. 패키지 구조

```text
com.nh.nsight.messaging
 ├─ common
 │   ├─ context
 │   ├─ error
 │   ├─ log
 │   └─ response
 ├─ config
 │   ├─ WebMvcConfig
 │   └─ AsyncConfig
 └─ message
     ├─ controller
     ├─ facade
     ├─ service
     ├─ rule
     ├─ thing
     ├─ dao
     ├─ mapper
     └─ dto
```

## 4. 계층별 책임

| 계층 | 책임 | 금지사항 |
|---|---|---|
| Controller | 요청 수신, Validation, 응답 반환 | 업무 로직, SQL 호출 금지 |
| Facade | 유스케이스 조립, 트랜잭션 경계, 응답 전문 생성 | SQL 직접 호출 금지 |
| Service | 메시지 등록/조회 처리 | HTTP 의존 금지 |
| Rule | 메시지 코드, 기간, 유형 검증 | DB 직접 접근 금지 |
| Thing | 메시지 업무 개념 표현 | Framework 의존 최소화 |
| DAO/Mapper | DB 접근, SQL 실행 | 업무 판단 금지 |
| Common | GUID, 오류, 표준응답, 로그 | 특정 업무 로직 금지 |

## 5. 메시지 Life-cycle

```text
DRAFT → ACTIVE → EXPIRED → DELETED
```

현재 샘플은 등록과 조회 중심으로 구성했습니다. 운영 프로젝트에서는 승인, 배포, 폐기, 이력관리 기능을 추가하면 됩니다.
