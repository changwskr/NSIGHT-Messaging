# NSIGHT 통합 환경설정 점검 (Trace Environment)

## 패키지
`com.nh.nsight.messaging.traceenvironment`

## 설계서 반영 (ENV-002~004)

`NSIGHT_환경설정_산정점검_화면_서비스_설계서.docx` 기준 화면·API:

| 화면 | 경로 | API |
|------|------|-----|
| ENV-001 대시 | 상단 요약(시나리오 ID·위험 집계) | `POST /capacity-design/analyze` |
| ENV-002 조건 입력 | 지점×사용자·VM·TPMC·%·Timeout·Session·A-A/DR | `GET /capacity-design/defaults` |
| ENV-003 TPS·VM 결과 | 요청률/초·실요청·TPS·TPMC·필요VM·A-A | 동일 |
| ENV-004 계층 점검 | UI~MyBatis Grid + **JVM 사이징**(Heap·G1GC·Metaspace·기동 옵션 예시) | 동일 |

산정: `실요청자=전체×%` · `목표TPS=실요청자÷Timeout` · `TPMC=목표TPS×TPMC/TPS` · `Core TPS=(35×3000)÷TPMC/TPS` · `VM TPS=Core×Core TPS`

## Phase A (구현 완료)

| 설계 항목 | 구현 |
|-----------|------|
| FR-001 프로젝트 기준 | `GET /api/v1/trace-environment/projects/baseline` · `nsight.env-check` |
| FR-003 설정 업로드·파싱 | `POST /api/v1/trace-environment/config-files/upload` (yml/yaml/properties/xml/conf…) |
| FR-002/005 Rule Engine | Threshold(가이드 카탈로그) + Relation `TIMEOUT-REL-001` |
| Assessment Run | `POST /api/v1/trace-environment/assessments` |
| SC-008 Timeout Map | `GET /assessments/{runId}/timeout-map` + 화면 체인 |
| SC-009 동시 요청자 Flow | `GET /assessments/{runId}/concurrent-flow-map` · `CONCURRENT-REL-001` |
| SC-007 설정 조회 | `GET /settings` + 카테고리 테이블 |

## API

| Method | URL |
|--------|-----|
| GET | `/api/v1/trace-environment/settings` |
| GET | `/api/v1/trace-environment/projects/baseline` |
| POST | `/api/v1/trace-environment/config-files/upload` |
| POST | `/api/v1/trace-environment/assessments` |
| GET | `/api/v1/trace-environment/assessments/{runId}` |
| GET | `/api/v1/trace-environment/assessments/{runId}/results` |
| GET | `/api/v1/trace-environment/assessments/{runId}/timeout-map` |
| GET | `/api/v1/trace-environment/assessments/{runId}/concurrent-flow-map` |
| GET | `/api/v1/trace-environment/dashboard/summary` |

## UI (화면 분리)

| 화면 | URL |
|------|-----|
| ENV-001 대시 | `/traceenvironment/env-001` |
| ENV-002 조건 | `/traceenvironment/env-002` |
| ENV-003 TPS·VM (VM당 Heap·Threads·DB Pool) | `/traceenvironment/env-003` |
| ENV-004 계층 (탭: UI~MyBatis) | `/traceenvironment/env-004` |
| 종합 보고서 (ENV-002~004 전체 스택) | `/traceenvironment/check` |
| Rule 점검 (SC-002/004/007/008/009) | `/traceenvironment/rule-check` |
| 진입 | `/traceenvironment` → ENV-001 리다이렉트 |

산정 결과는 브라우저 `sessionStorage`로 ENV-001·003·004·종합 보고서에 공유 (ENV-002에서 「산정 실행」 후). Rule 점검 결과(`nsight.env.lastAssessment`)는 종합 보고서 §6에 요약 반영.

### 종합 보고서 (`/traceenvironment/check`)
- §1~4: ENV-002 조건, ENV-003 TPS·VM, ENV-004 요약, Core TPS
- §5: **전체 스택** UI → GSLB → L4 → Apache → Tomcat → JVM(사이징+Grid) → Spring Boot → MyBatis — 계층별 권장·현재·위치·예시·조치 + 통합 Grid
- §6: Rule Engine 요약 (점검은 Rule 점검 탭에서 실행)

### Rule 점검 (`/traceenvironment/rule-check`)
- SC-002 프로젝트 기준정보 (ENV-002 산정 연동)
- SC-004 설정 업로드·파싱 · Assessment(Rule Engine)
- SC-008 Timeout Map · SC-009 동시 요청자 Flow
- 설계 원칙 · SC-007 카테고리(용량산정 TPS, WebTopSuite, Tomcat/WAS, Spring Boot, HikariCP/MyBatis, CruzAPIM, L4/GSLB/Proxy, 모니터링)

## ENV-004 계층 탭
- 상단: 점검 기준값(ENV-002 연동)
- 하단 탭: **UI · GSLB · L4 · Apache · Tomcat · JVM · Spring Boot · MyBatis** — 계층별 Grid(권장·현재·판정·조치), JVM 탭에 Heap·GC 사이징 패널 포함
- 마지막 선택 탭은 `sessionStorage` (`nsight.env004.activeTab`)에 유지

## GSLB / L4 / Load Balancer (`LoadBalancerSizingGuide`)
| 항목 | 32CORE/256GB 권장 |
|------|-------------------|
| GSLB DNS TTL | 30~60초 |
| L4 Health | Interval 5s · Timeout 2s · Fail 2~3회 |
| Sticky | 적용 · Timeout 70~80분 (세션 60분 기준) |
| Client-L4 Idle | 70~90초 |
| L4-WAS Idle | 70~90초 (> Tomcat keepAlive 60초) |
| LB 방식 | Least / Weighted Least Connection |
| Max Connection | ≥ 센터 AP대수 × VM maxConnections |

## ENV-003 Tomcat·Hikari (참고 · 산정 카드에 표시, 별도 §4 블록 없음)

Tomcat·Hikari·WAS 상세 표는 ENV-003 화면에서 제거. Tomcat/WAS/DB Pool 산식은 **표 항목 설명 카드**에만 표시.

## ENV-003 Tomcat·Hikari 기준표 (`TomcatHikariSizingGuide` / `VmProfile`)
| VM | 기준 TPS | maxThreads | Hikari 일반 | Hikari SV |
|----|---------:|------------|-------------|-----------|
| 16CORE/64·128GB | 500 | 800~1,000 | 80~100 | 70~80 |
| 32CORE/256GB | 1,000 | 1,200~1,500 (1차) | 120~150 | 150~180 |

**32CORE WAS/Tomcat (16CORE 단순 2배 아님)**  
`Busy ≈ TPS × 평균응답(1.0~1.2s) × 여유율(1.2)` → 1,000 TPS 기준 **1,200~1,440** Busy Thread → maxThreads **1,200~1,500** (성능시험 1,200 vs 1,500 비교).

| 항목 | 보수 | 권장 |
|------|-----:|------|
| maxThreads | 1,200 | 1,200~1,500 |
| minSpareThreads | 200 | 300 |
| acceptCount | 500 | 500~800 |
| maxConnections | 20,000 | 20,000~30,000 |
| connectionTimeout | 8초 | 8초 |
| keepAliveTimeout | 60초 | 60초 (L4 Idle 70~90초) |
| maxKeepAliveRequests | 100 | 100 |
| Xss | 512KB | 512KB |

- 운영 보정: Busy Thread·CPU·Hikari Pending·DB SQL Time
- DB Pool 합계 = 권장 VM × VM당 Pool(권장=범위 상한) · 32C는 DB Session 총량 검증 필수

**VM당 DB Pool 산출식** (`DbPoolSizingGuide` · ENV-003 카드)
1. 프로파일 §4 앵커 (16C: 80~100, 32C: 120~150)
2. `Pool ≤ Tomcat maxThreads`
3. 참고: `floor(maxThreads × 10%)` → §4 범위 `clamp`
4. 권장 = §4 범위 상한 (성능시험·DB Session 검증 후 조정)
5. 합계 = 권장 VM × VM당 Pool ≤ DB Session 한도

Spring Boot 권장표는 **ENV-004** 계층 Grid(Spring Boot 탭)에서 확인. ENV-003 화면 § 블록은 없음.

## ENV-004 JVM 사이징

- 입력 **코어·RAM(GB)** 기준 `JvmSizingGuide.recommend(cores, memoryGb)` · API `jvmSizing`
- **코어당 8GB RAM** (`memory ≈ cores × 8`): 일반 Heap **Xms≈1.5×Core**, **Xmx≈1.75×Core** (32C는 32~48GB 상한)
- **8CORE/32GB** Scale-Out 표준: 일반 12~14GB, SV 14GB (코어당 8GB 아님)
- 비표준 RAM 비율: 문서 앵커(32/64/128/256GB) 선형 보간
- VM 프로파일: `8CORE-32GB`, `8/16/32CORE`+`64/128/256GB` (`VmProfile`)

## Phase B (미구현)
DB Session 한도, JVM OOM Rule·업로드 파싱, 예외·승인, Word/PDF 보고서, DB 영속화, GitLab CI

## 하드웨어 프로파일 (기본)
- `nsight-32core-256gb` — 환경설정 작업가이드 (2026-05-29) + **용량산정** `NSIGHT_용량산정_세션60분_32core_256G_기준.docx` (2026-05-31)
- 상수: `Nsight32Core256GbGuide` · 카탈로그: `IntegratedEnvironmentGuideCatalog` (`GUIDE_VERSION` 2026-05-31)

### 용량산정 반영 수치 (`nsight.env-check`)
| 항목 | 값 |
|------|-----:|
| 지점 / 전체 사용자 / 실요청(5%) | 6,000 / 36,000 / 1,800 |
| 세션 설계 (60분) | 36,000 (여유 43,000~47,000) |
| TPS 3% / 5% / 10% / 15% | 360 / 600 / 1,200 / 1,800 |
| VM 1대 상한 | 1,000 TPS |
| 피크 동시요청자 (5%) | 1,800 |
| Tomcat maxThreads (32C) | 1,200~1,500 (Busy 1,200~1,440) |

동시 목표( SC-009 ): 실요청 **1,800명(전사, 5%)** → AP **2**대 시 AP당 **900** · TPS **600** = 1,800÷p95(3초). `CAPACITY-REL-002`로 실요청·peak-tps 정합 점검.

## 참고
- `NSIGHT_통합_환경설정_가이드.docx`
- `NSIGHT_통합환경설정_점검시스템_개발요구사항_화면설계_서비스설계서.docx`
