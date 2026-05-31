# NSIGHT JVM Dump 분석 — 프로그램 개발 명세

> 참조: `NSIGHT_JVM_Dump_분석_보고서_프로그램개발용.docx`

## 1. 분석 목표

증거(Heap/Thread/GC/NMT/Histogram/Console)를 읽고 → 지표를 추출하고 → 규칙으로 판정하며 → 조치를 권고한다.

## 2. 사람용 보고서 4단계

| 단계 | 보고서 위치 | 프로그램 |
|------|-------------|----------|
| ① 무엇을 읽었는가 | 4단계 요약 박스 `step1Read`, §2 수집 증거 | `EvidenceLoader` |
| ② 어떤 증거를 찾았는가 | 4단계 `step2Evidence`, 핵심 지표, §4·5·GC | `*Parser`, `DumpIndicatorsBuilder` |
| ③ 어떤 원인으로 판정하는가 | 4단계 `step3Judgment`, 원인 가설, §3·6, OOM 연계 | `TraceDumpRuleEngine`, `OomCorrelationAnalyzer` |
| ④ 어떤 조치를 추천하는가 | 4단계 `step4Action`, §7, Finding | `ActionPlanSection`, Finding.description |

## 3. 프로그램 파이프라인 (7단계)

1. 증거 수집 — ZIP/폴더 (`EvidenceLoader`)
2. 파일 유형 식별 — `EvidenceType`
3. 지표 추출 — `ThreadDumpParser`, `GcLogParser`, `NmtParser`, `ClassHistogramParser`, `HsErrParser`
4. 분석 규칙 — `TraceDumpRuleEngine`
5. 원인 가설 — `TraceDumpReportViewBuilder.buildCauseHypotheses`
6. 심각도 — `Severity`: CRITICAL > HIGH > MEDIUM > LOW > INFO
7. 조치 권고 — `ActionPlanSection`

## 4. 규칙 카탈로그 (§13)

| Rule ID | 조건 | 심각도 | 가이드 |
|---------|------|--------|--------|
| HEAP-001 | console/hs_err `java heap space` 또는 HEAP_OOM | HIGH | §4 |
| HEAP-002 | Full GC 후 Heap/Old Region 미감소 | HIGH | §6 |
| HEAP-003 | SESSION_CACHE / QUERY_RESULT_CACHE 문자열 | HIGH | §4.2 |
| HEAP-004 | Histogram Top (참고, retained는 MAT) | INFO | §4 |
| THR-001 | `Found one Java-level deadlock` | CRITICAL | §5 |
| THR-002 | HikariPool 대기 Thread >= 5 | HIGH | §5.2 |
| THR-003 | CruzAPIM 대기 Thread >= 3 | MEDIUM | §5.2 |
| THR-004 | BLOCKED Thread >= 2 | HIGH | §5.2 |
| GC-001 | Full GC >= 5 | HIGH | §6 |
| GC-002 | Heap/Old `N->N` 패턴 | HIGH | §6.2 |
| GC-003 | soft references compaction | HIGH | §6 |
| GC-004 | Evacuation failure | HIGH | §6.2 |
| NMT-001 | Thread committed > 512MB | MEDIUM | §7 |
| NMT-002 | Metaspace committed > 100MB | MEDIUM | §7.2 |
| OOM-001 | OOM 유형 분류 | HIGH | §1.2 |

## 5. 핵심 지표 (`DumpAnalysisIndicators`)

JSON §12 형식과 동일 의미:

- `javaHeapOom`, `oomCategory`, `fullGcCount`, `oldRegionNotReduced`, `heapNotReducedAfterFullGc`
- `deadlockFound`, `hikariWaitingThreads`, `cruzApimWaitingThreads`
- `sessionCacheHint`, `queryResultCacheHint`, `heapDumpCollected`

## 6. 패키지 구조

```text
tracedump/
  collector/   EvidenceLoader
  parser/      Thread, Gc, Nmt, Histogram, HsErr
  analyzer/    TraceDumpRuleEngine, OomCorrelationAnalyzer, DumpIndicatorsBuilder
  report/      TraceDumpReportBuilder, TraceDumpReportViewBuilder
  service/     TraceDumpAnalysisService
```

## 7. 한계 (§15)

- Histogram: retained/GC Root 확정 불가 → MAT 필수
- HPROF: 바이너리 스킵, HEAP-010 안내
- 분석 결과는 **가설+신뢰도** — 운영 확정은 사람 검증
