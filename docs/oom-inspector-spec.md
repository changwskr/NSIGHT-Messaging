# NSIGHT OOM Risk Inspector

참조: `NSIGHT_OOM_유발_프로그램_점검툴_요구사항_및_설계서.docx`

## 패키지

`com.nh.nsight.messaging.traceoompgm`

## 4단계

| 단계 | 구현 |
|------|------|
| 읽기 | `SourceFileCollector` — Java, Mapper XML, application.yml |
| 증거 | `JavaSourceRiskScanner`, `MapperXmlRiskScanner`, `YamlConfigRiskScanner` |
| 판정 | Rule ID (OOM-SESSION-001, OOM-SQL-001, …) + Severity |
| 조치 | Finding.recommendation + Gate |

## API

| Method | Path | 설명 |
|--------|------|------|
| POST | `/api/oom-inspector/scans` | 경로 지정 스캔 |
| POST | `/api/oom-inspector/scans/quick` | 기본 경로 스캔 |
| POST | `/api/oom-inspector/gate` | CI Gate (failOn=CRITICAL) |

## 화면

`/oominspector`

## MVP Rule (설계서 RB-001~012)

- OOM-SESSION-001: session.setAttribute 대용량
- OOM-CACHE-001: static Map/List
- OOM-TL-001/002: ThreadLocal
- OOM-SQL-001~003: Mapper 페이징/fetch/LOB
- OOM-FILE-001/002: getBytes/readAllBytes
- OOM-EXCEL-001, OOM-QUEUE-001, OOM-THREAD-001/002
- OOM-CONFIG-001~003: Hikari/Tomcat/multipart

## CLI (향후)

```bash
# 동등 API 호출 예
curl -X POST "http://localhost:8080/api/oom-inspector/scans/quick" -H "X-GUID: test" -H "X-USER-ID: ARCHITECT"
```
