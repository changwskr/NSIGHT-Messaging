# NSIGHT 메시징 관리 서비스 환경설정 기준

## 1. 기준선

| 항목 | 기준 |
|---|---:|
| VM 기준 (일반) | 8 vCPU / 32GB |
| VM 기준 (특수 AP) | 32 vCPU / 256GB — `NSIGHT_용량산정_세션60분_32core_256G_기준.docx` |
| VM당 처리량 (32C) | 1,000 TPS 상한 · 운영 피크 권장 600 TPS |
| 낮은 부하 TPS (3%) | 360 TPS |
| 운영 피크 TPS (5%) | 600 TPS |
| 고피크 TPS (10%) | 1,200 TPS |
| 스트레스 TPS (15%) | 1,800 TPS |
| 세션 설계 | 36,000 (여유 43,000~47,000) · 60분 · 2KB 목표 / 5KB 이내 |
| 목표 응답시간 | p95 3초 이하 |
| 세션 Idle Timeout | 60분 |
| Absolute Session Timeout | 8시간, 업무 공통 모듈 구현 대상 |
| GC | G1GC |

## 2. Timeout 계층

```text
DB Query Timeout < Spring Transaction Timeout < Proxy/WebTopSuite Timeout < L4 Timeout
```

| 영역 | 권장값 | 프로젝트 반영 |
|---|---:|---|
| MyBatis Statement Timeout | 3초 | `default-statement-timeout: 3` |
| Hikari Connection Timeout | 3초 | `connection-timeout: 3000` |
| Spring Transaction Timeout | 5초 | `nsight.transaction.default-timeout-seconds: 5` |
| Tomcat Connection Timeout | 8초 | `server.tomcat.connection-timeout: 8s` |
| WebTopSuite Request Timeout | 15초 | `nsight.webtop.request-timeout-ms: 15000` |

## 3. Tomcat

| 설정 | 권장값 | application.yml |
|---|---:|---|
| maxThreads | 500 | `server.tomcat.threads.max` |
| minSpareThreads | 100 | `server.tomcat.threads.min-spare` |
| acceptCount | 500 | `server.tomcat.accept-count` |
| maxConnections | 10000 | `server.tomcat.max-connections` |
| connectionTimeout | 8s | `server.tomcat.connection-timeout` |

## 4. Spring Session / Cookie

| 설정 | 권장값 |
|---|---:|
| session timeout | 60m |
| http-only | true |
| secure | true, local은 false |
| same-site | Lax |

## 5. HikariCP

| 설정 | 일반 AP 기준 |
|---|---:|
| maximumPoolSize | 50 |
| minimumIdle | 10 |
| connectionTimeout | 3000ms |
| validationTimeout | 3000ms |
| idleTimeout | 600000ms |
| maxLifetime | 1800000ms |
| keepaliveTime | 300000ms |
| autoCommit | false |

## 6. MyBatis

| 설정 | 기준 | 파일 |
|---|---:|---|
| config-location | 전역 설정 | `mybatis/mybatis-config.xml` |
| mapper-locations | SQL XML | `mapper/**/*.xml` |
| @MapperScan | 인터페이스 스캔 | `MybatisConfig.java` |
| default-statement-timeout | 3초 | `mybatis-config.xml` |
| default-fetch-size | 300 | `mybatis-config.xml` |
| mapUnderscoreToCamelCase | true | `mybatis-config.xml` |

상세: [MYBATIS.md](./MYBATIS.md)

## 7. 로그 파일

| 프로파일 | 경로 | 롤링 |
|---|---|---|
| local | `./logs/nsight-message-mgmt-service.log` | 일별 + 100MB, 30일, 최대 3GB |
| prd | `/logs/msg-ap/nsight-message-mgmt-service.log` | 동일 |

설정: `logging.file.path`, `logback-spring.xml` (CONSOLE + FILE)

## 8. JVM 옵션 예시

```bash
-Xms12g
-Xmx12g
-Xss512k
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/logs/dump
-Xlog:gc*:file=/logs/gc/msg-ap-gc-%t.log:time,uptime,level,tags:filecount=10,filesize=100M
-Dfile.encoding=UTF-8
-Duser.timezone=Asia/Seoul
```
