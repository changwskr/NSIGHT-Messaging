-- =============================================================================
-- NSIGHT 샘플 데이터 (schema.sql 기준 · 테이블당 10건)
-- Spring sql.init: schema.sql 실행 후 본 스크립트 적용
-- =============================================================================

-- TB_MSG_MESSAGE (10)
INSERT INTO TB_MSG_MESSAGE
(MESSAGE_CODE, MESSAGE_NAME, MESSAGE_TYPE, CHANNEL_CODE, LOCALE, MESSAGE_CONTENT,
 DISPLAY_START_AT, DISPLAY_END_AT, USE_YN, CREATED_BY, UPDATED_BY)
VALUES
('MSG_NOTICE_001', '시스템 점검 안내', 'NOTICE', 'WEBTOPSUITE', 'ko_KR',
 '정기 점검으로 22:00~24:00 일부 서비스가 제한됩니다.', CURRENT_TIMESTAMP, NULL, 'Y', 'SYSTEM', 'SYSTEM'),
('MSG_NOTICE_002', '로그인 실패 안내', 'ALERT', 'MOBILE', 'ko_KR',
 '비밀번호 5회 오류 시 계정이 잠깁니다. 고객센터로 문의하세요.', CURRENT_TIMESTAMP, NULL, 'Y', 'ADMIN', 'ADMIN'),
('MSG_INFO_001', '조회 완료', 'INFO', 'WEBTOPSUITE', 'ko_KR',
 '고객 정보 조회가 완료되었습니다.', CURRENT_TIMESTAMP, NULL, 'Y', 'SYSTEM', 'SYSTEM'),
('MSG_INFO_002', 'Query completed', 'INFO', 'API', 'en_US',
 'Customer inquiry completed successfully.', CURRENT_TIMESTAMP, NULL, 'Y', 'SYSTEM', 'SYSTEM'),
('MSG_WARN_001', '한도 초과 경고', 'WARN', 'BRANCH', 'ko_KR',
 '일일 이체 한도를 초과했습니다. 잔여 한도를 확인하세요.', CURRENT_TIMESTAMP, NULL, 'Y', 'OPS01', 'OPS01'),
('MSG_ERR_001', '거래 처리 오류', 'ERROR', 'WEBTOPSUITE', 'ko_KR',
 '일시적 오류로 거래를 완료하지 못했습니다. 잠시 후 다시 시도하세요.', CURRENT_TIMESTAMP, NULL, 'Y', 'SYSTEM', 'SYSTEM'),
('MSG_PROMO_001', '이벤트 안내', 'NOTICE', 'MOBILE', 'ko_KR',
 '신규 가입 고객 대상 우대 금리 이벤트 진행 중입니다.', CURRENT_TIMESTAMP, TIMESTAMP '2026-12-31 23:59:59', 'Y', 'MKT01', 'MKT01'),
('MSG_OTP_001', 'OTP 발송', 'INFO', 'MOBILE', 'ko_KR',
 '[NH] 인증번호 ###### 을 입력하세요. (3분 유효)', CURRENT_TIMESTAMP, NULL, 'Y', 'SYSTEM', 'SYSTEM'),
('MSG_AUDIT_001', '감사 로그 알림', 'NOTICE', 'API', 'ko_KR',
 '민감 정보 조회가 기록되었습니다.', CURRENT_TIMESTAMP, NULL, 'Y', 'SEC01', 'SEC01'),
('MSG_LEGACY_001', '구버전 메시지', 'INFO', 'WEBTOPSUITE', 'ko_KR',
 '지원 종료 예정 메시지입니다.', TIMESTAMP '2024-01-01 00:00:00', TIMESTAMP '2025-06-30 23:59:59', 'N', 'SYSTEM', 'ADMIN');

-- TB_MSG_FILE (10)
INSERT INTO TB_MSG_FILE
(ORIGINAL_NAME, STORED_NAME, CONTENT_TYPE, FILE_SIZE, STORAGE_PATH, BIZ_CATEGORY, DESCRIPTION,
 USE_YN, CREATED_BY, UPDATED_BY)
VALUES
('notice_banner.png', '20260603_notice_banner.png', 'image/png', 245760,
 '/data/nsight/files/notice/20260603_notice_banner.png', 'NOTICE', '메인 공지 배너', 'Y', 'ADMIN', 'ADMIN'),
('terms_v2.pdf', '20260603_terms_v2.pdf', 'application/pdf', 1048576,
 '/data/nsight/files/doc/terms_v2.pdf', 'DOCUMENT', '이용약관 v2', 'Y', 'LEGAL01', 'LEGAL01'),
('product_guide.pdf', '20260603_product_guide.pdf', 'application/pdf', 2097152,
 '/data/nsight/files/doc/product_guide.pdf', 'DOCUMENT', '상품 안내서', 'Y', 'MKT01', 'MKT01'),
('branch_map.jpg', '20260603_branch_map.jpg', 'image/jpeg', 512000,
 '/data/nsight/files/img/branch_map.jpg', 'REFERENCE', '지점 안내 지도', 'Y', 'OPS01', 'OPS01'),
('export_sample.xlsx', '20260603_export_sample.xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 89088,
 '/data/nsight/files/export/export_sample.xlsx', 'EXPORT', '메시지 목록 샘플', 'Y', 'SYSTEM', 'SYSTEM'),
('icon_set.zip', '20260603_icon_set.zip', 'application/zip', 524288,
 '/data/nsight/files/asset/icon_set.zip', 'ASSET', 'UI 아이콘 묶음', 'Y', 'DEV01', 'DEV01'),
('audit_template.csv', '20260603_audit_template.csv', 'text/csv', 4096,
 '/data/nsight/files/template/audit_template.csv', 'TEMPLATE', '감사 로그 업로드 템플릿', 'Y', 'SEC01', 'SEC01'),
('mobile_splash.png', '20260603_mobile_splash.png', 'image/png', 153600,
 '/data/nsight/files/mobile/mobile_splash.png', 'MOBILE', '모바일 스플래시', 'Y', 'MKT01', 'MKT01'),
('faq_attach.docx', '20260603_faq_attach.docx', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 32768,
 '/data/nsight/files/doc/faq_attach.docx', 'DOCUMENT', 'FAQ 첨부', 'Y', 'CS01', 'CS01'),
('deprecated_old.pdf', '20250101_deprecated_old.pdf', 'application/pdf', 65536,
 '/data/nsight/files/archive/deprecated_old.pdf', 'ARCHIVE', '폐기 예정 문서', 'N', 'SYSTEM', 'ADMIN');

-- TB_TX_TRANSACTION_LOG (10)
INSERT INTO TB_TX_TRANSACTION_LOG
(REQUEST_URI, HTTP_METHOD, GUID, TRACE_ID, SPAN_ID, TRANSACTION_ID, INTERFACE_ID, SERVICE_ID,
 REQUEST_DATE_TIME, RESPONSE_DATE_TIME, SOURCE_SYSTEM_ID, TARGET_SYSTEM_ID, CHANNEL_ID, TERMINAL_ID,
 USER_ID, BRANCH_ID, CENTER_ID, AP_ID, REQUEST_TYPE, MESSAGE_TYPE, VERSION, CLIENT_IP,
 CTRL_TIMEOUT, CTRL_RETRY_YN, CTRL_RETRY_COUNT, CTRL_PAGE_NO, CTRL_PAGE_SIZE, CTRL_TOTAL_COUNT,
 SEC_MASKING_LEVEL, SEC_DATA_GRADE, SEC_ACCESS_PURPOSE, SEC_AUDIT_REQUIRED_YN,
 ERR_RESULT_CODE, ERR_RESULT_MESSAGE, ERR_ERROR_CODE, ERR_ERROR_MESSAGE, ERR_ERROR_DETAIL,
 ERR_ERROR_SYSTEM_ID, ERR_ERROR_DATE_TIME)
VALUES
('/api/v1/messages', 'GET', 'guid-20260603-0001', 'trace-20260603-0001', 'span-01', 'TX-000001', 'IF-MSG-001', 'MSG-SVC',
 '2026-06-03T09:00:01', '2026-06-03T09:00:01', 'WEB', 'MSG-CORE', 'WEB', 'T001', 'user01', '1001', 'CTR-SEOUL', 'AP-01',
 'SYNC', 'JSON', '1.0', '10.0.0.11', 3000, 'N', 0, 1, 20, 10, 'PARTIAL', 'INTERNAL', 'INQUIRY', 'Y',
 '0000', '정상', NULL, NULL, NULL, NULL, NULL),
('/api/v1/messages/MSG_NOTICE_001', 'GET', 'guid-20260603-0002', 'trace-20260603-0002', 'span-02', 'TX-000002', 'IF-MSG-002', 'MSG-SVC',
 '2026-06-03T09:01:15', '2026-06-03T09:01:15', 'MOBILE', 'MSG-CORE', 'MOB', 'M002', 'user02', '2002', 'CTR-BUSAN', 'AP-02',
 'SYNC', 'JSON', '1.0', '10.0.0.22', 3000, 'N', 0, 1, 10, 1, 'FULL', 'CONFIDENTIAL', 'INQUIRY', 'Y',
 '0000', '정상', NULL, NULL, NULL, NULL, NULL),
('/api/v1/files/upload', 'POST', 'guid-20260603-0003', 'trace-20260603-0003', 'span-03', 'TX-000003', 'IF-FILE-001', 'FILE-SVC',
 '2026-06-03T09:05:00', '2026-06-03T09:05:02', 'WEB', 'FILE-CORE', 'WEB', 'T003', 'admin', '1001', 'CTR-SEOUL', 'AP-01',
 'SYNC', 'MULTIPART', '1.0', '10.0.0.33', 10000, 'N', 0, 0, 0, 0, 'NONE', 'INTERNAL', 'UPLOAD', 'Y',
 '0000', '정상', NULL, NULL, NULL, NULL, NULL),
('/api/v1/transactions/search', 'POST', 'guid-20260603-0004', 'trace-20260603-0004', 'span-04', 'TX-000004', 'IF-TX-001', 'TX-SVC',
 '2026-06-03T10:00:00', '2026-06-03T10:00:01', 'BRANCH', 'TX-CORE', 'BRN', 'B101', 'teller01', '3003', 'CTR-DAEGU', 'AP-03',
 'SYNC', 'JSON', '1.0', '10.0.1.10', 5000, 'N', 0, 1, 50, 128, 'PARTIAL', 'CONFIDENTIAL', 'AUDIT', 'Y',
 '0000', '정상', NULL, NULL, NULL, NULL, NULL),
('/api/v1/messages', 'POST', 'guid-20260603-0005', 'trace-20260603-0005', 'span-05', 'TX-000005', 'IF-MSG-003', 'MSG-SVC',
 '2026-06-03T10:15:30', '2026-06-03T10:15:30', 'API', 'MSG-CORE', 'API', 'A001', 'batch01', '0000', 'CTR-HQ', 'AP-BATCH',
 'ASYNC', 'JSON', '1.0', '10.0.2.5', 30000, 'Y', 2, 0, 0, 0, 'NONE', 'INTERNAL', 'REGISTER', 'N',
 '0000', '정상', NULL, NULL, NULL, NULL, NULL),
('/api/v1/accounts/1000000001', 'GET', 'guid-20260603-0006', 'trace-20260603-0006', 'span-06', 'TX-000006', 'IF-ACC-001', 'CAP-SVC',
 '2026-06-03T11:00:00', '2026-06-03T11:00:01', 'WEB', 'CAP-CORE', 'WEB', 'T010', 'user03', '1001', 'CTR-SEOUL', 'AP-01',
 'SYNC', 'JSON', '1.0', '10.0.0.44', 3000, 'N', 0, 1, 1, 1, 'FULL', 'CONFIDENTIAL', 'INQUIRY', 'Y',
 '0000', '정상', NULL, NULL, NULL, NULL, NULL),
('/api/v1/messages/INVALID', 'GET', 'guid-20260603-0007', 'trace-20260603-0007', 'span-07', 'TX-000007', 'IF-MSG-002', 'MSG-SVC',
 '2026-06-03T11:30:00', '2026-06-03T11:30:00', 'MOBILE', 'MSG-CORE', 'MOB', 'M005', 'user04', '2002', 'CTR-BUSAN', 'AP-02',
 'SYNC', 'JSON', '1.0', '10.0.0.55', 3000, 'N', 0, 0, 0, 0, 'NONE', 'PUBLIC', 'INQUIRY', 'N',
 'E404', '리소스 없음', 'MSG_NOT_FOUND', '메시지 코드를 찾을 수 없습니다', 'MESSAGE_CODE=INVALID', 'MSG-CORE', '2026-06-03T11:30:00'),
('/api/v1/files/99', 'DELETE', 'guid-20260603-0008', 'trace-20260603-0008', 'span-08', 'TX-000008', 'IF-FILE-002', 'FILE-SVC',
 '2026-06-03T12:00:00', '2026-06-03T12:00:01', 'WEB', 'FILE-CORE', 'WEB', 'T020', 'admin', '1001', 'CTR-SEOUL', 'AP-01',
 'SYNC', 'JSON', '1.0', '10.0.0.66', 3000, 'N', 0, 0, 0, 0, 'NONE', 'INTERNAL', 'DELETE', 'Y',
 '0000', '정상', NULL, NULL, NULL, NULL, NULL),
('/api/v1/trace-dump/analyze', 'POST', 'guid-20260603-0009', 'trace-20260603-0009', 'span-09', 'TX-000009', 'IF-TRC-001', 'TRACE-SVC',
 '2026-06-03T13:00:00', '2026-06-03T13:00:05', 'OPS', 'TRACE-CORE', 'OPS', 'O001', 'ops01', '0000', 'CTR-HQ', 'AP-OPS',
 'SYNC', 'JSON', '1.0', '10.0.3.1', 60000, 'N', 0, 0, 0, 0, 'NONE', 'INTERNAL', 'ANALYZE', 'N',
 '0000', '정상', NULL, NULL, NULL, NULL, NULL),
('/api/v1/messages', 'GET', 'guid-20260603-0010', 'trace-20260603-0010', 'span-10', 'TX-000010', 'IF-MSG-001', 'MSG-SVC',
 '2026-06-03T14:00:00', '2026-06-03T14:00:03', 'WEB', 'MSG-CORE', 'WEB', 'T099', 'user05', '4004', 'CTR-GWANGJU', 'AP-04',
 'SYNC', 'JSON', '1.0', '10.0.0.77', 3000, 'Y', 1, 2, 20, 45, 'PARTIAL', 'INTERNAL', 'INQUIRY', 'Y',
 'E500', '시스템 오류', 'DB_TIMEOUT', '조회 시간 초과', 'Hikari pool wait timeout', 'MSG-CORE', '2026-06-03T14:00:03');

-- TB_MSG_AUDIT_LOG (10)
INSERT INTO TB_MSG_AUDIT_LOG
(GUID, TRACE_ID, USER_ID, ACTION_TYPE, TARGET_ID, RESULT_CODE)
VALUES
('guid-audit-0001', 'trace-audit-0001', 'admin', 'LOGIN', NULL, '0000'),
('guid-audit-0002', 'trace-audit-0002', 'user01', 'VIEW', 'MSG_NOTICE_001', '0000'),
('guid-audit-0003', 'trace-audit-0003', 'user02', 'DOWNLOAD', 'FILE:3', '0000'),
('guid-audit-0004', 'trace-audit-0004', 'admin', 'CREATE', 'MSG_PROMO_001', '0000'),
('guid-audit-0005', 'trace-audit-0005', 'admin', 'UPDATE', 'MSG_NOTICE_002', '0000'),
('guid-audit-0006', 'trace-audit-0006', 'sec01', 'VIEW', 'MSG_AUDIT_001', '0000'),
('guid-audit-0007', 'trace-audit-0007', 'user03', 'DELETE', 'MSG_LEGACY_001', '0000'),
('guid-audit-0008', 'trace-audit-0008', 'batch01', 'EXPORT', 'TB_MSG_MESSAGE', '0000'),
('guid-audit-0009', 'trace-audit-0009', 'user04', 'VIEW', 'INVALID_CODE', 'E404'),
('guid-audit-0010', 'trace-audit-0010', 'ops01', 'ANALYZE', 'TRACE-DUMP-001', '0000');

-- TB_CAPACITY_ACCOUNT (10)
INSERT INTO TB_CAPACITY_ACCOUNT
(ACCOUNT_NUMBER, NAME, IDENTIFICATION_NUMBER, INTEREST_RATE, LAST_TRANSACTION, PASSWORD,
 NET_AMOUNT, ACCOUNT_TYPE, STATUS, CURRENCY)
VALUES
('1000000001', '김철수', '900101-1******', 2.35, CURRENT_TIMESTAMP, '****', 1500000.00, 'SAVINGS', 'ACTIVE', 'KRW'),
('1000000002', '이영희', '850315-2******', 2.10, CURRENT_TIMESTAMP, '****', 3200000.50, 'SAVINGS', 'ACTIVE', 'KRW'),
('1000000003', '박민수', '770820-1******', 1.85, TIMESTAMP '2026-06-02 15:30:00', '****', 850000.00, 'CHECKING', 'ACTIVE', 'KRW'),
('1000000004', '최지연', '920505-2******', 3.20, CURRENT_TIMESTAMP, '****', 12500000.00, 'DEPOSIT', 'ACTIVE', 'KRW'),
('1000000005', '정우진', '880912-1******', 2.00, TIMESTAMP '2026-05-28 09:00:00', '****', 0.00, 'SAVINGS', 'DORMANT', 'KRW'),
('1000000006', '한소희', '950101-2******', 2.50, CURRENT_TIMESTAMP, '****', 540000.75, 'SAVINGS', 'ACTIVE', 'KRW'),
('1000000007', '오세훈', '830707-1******', 1.50, TIMESTAMP '2026-06-01 11:20:00', '****', 98000000.00, 'DEPOSIT', 'ACTIVE', 'KRW'),
('1000000008', '윤서준', '990303-1******', 2.80, CURRENT_TIMESTAMP, '****', 2100000.00, 'SAVINGS', 'ACTIVE', 'KRW'),
('1000000009', '임하늘', '860415-2******', 0.00, TIMESTAMP '2026-04-10 00:00:00', '****', 12000.00, 'CHECKING', 'CLOSED', 'KRW'),
('1000000010', '강도현', '910626-1******', 2.25, CURRENT_TIMESTAMP, '****', 4567890.12, 'SAVINGS', 'ACTIVE', 'USD');

-- TB_XPILOT_SESSION (10)
INSERT INTO TB_XPILOT_SESSION
(PILOT_ID, PILOT_NAME, TARGET_MODULE, SOURCE_STRUCTURE, TARGET_STRUCTURE, STATUS, ENV_RUN_ID, NOTE)
VALUES
('PILOT-MSG-001', '메시징 AC/AS 마이그레이션', 'xpilotmessaging', 'legacy/msg/controller', 'ac/msgac + as/msgas', 'COMPLETED', 'RUN-20260601-001', '1차 파일럿 완료'),
('PILOT-MSG-002', '파일 모듈 구조 정렬', 'xpilotfile', 'legacy/file', 'ac/fileac + dc/filedc', 'IN_PROGRESS', 'RUN-20260602-002', 'Mapper 경로 통일 중'),
('PILOT-TX-001', '거래로그 조회 표준화', 'xpilottransactionmgr', 'transaction/legacy', 'ac/txac + mapper', 'PLANNED', NULL, '다음 스프린트'),
('PILOT-CAP-001', '용량산정 화면 파일럿', 'xpcapacitymgr', 'excel/manual', 'ac/capacityac + plan.html', 'IN_PROGRESS', 'RUN-20260603-003', 'CAP-050 Pool 산식 검증'),
('PILOT-STY-001', '스타일가이드 사용자 CRUD', 'xpilotstyleguide', 'sample/user', 'ac/userac + TB_ST_USER_PROFILE', 'COMPLETED', 'RUN-20260603-004', '10건 샘플 연동'),
('PILOT-ENV-001', '환경설정 업로드', 'traceenvironment', 'yaml/manual', 'guide + planner UI', 'COMPLETED', 'RUN-20260520-010', NULL),
('PILOT-DMP-001', 'JVM 덤프 분석 연동', 'tracedump', 'ops/script', '4단계 보고서 API', 'IN_PROGRESS', 'RUN-20260603-005', 'THR/GC Rule 보강'),
('PILOT-OOM-001', 'OOM Inspector 게이트', 'traceoompgm', 'static/rules', 'scan + gate API', 'PLANNED', NULL, 'CI 연동 예정'),
('PILOT-ACC-001', '계좌 샘플 DC', 'capacitymgr', 'demo/account', 'TB_CAPACITY_ACCOUNT', 'COMPLETED', 'RUN-20260603-006', '부하시험용'),
('PILOT-LEG-001', '레거시 메시지 이관', 'message', 'nsight/legacy', 'xpilotmessaging', 'ON_HOLD', NULL, '코드凍結 대기');

-- TB_ST_USER_PROFILE (10)
INSERT INTO TB_ST_USER_PROFILE
(USER_ID, USER_NAME, EMAIL, PHONE_NUMBER, ROLE_CODE, STATUS)
VALUES
('admin', '시스템 관리자', 'admin@nsight.local', '010-1000-0001', 'ADMIN', 'ACTIVE'),
('user01', '김철수', 'user01@example.com', '010-2000-0001', 'USER', 'ACTIVE'),
('user02', '이영희', 'user02@example.com', '010-2000-0002', 'USER', 'ACTIVE'),
('user03', '박민수', 'user03@example.com', '010-2000-0003', 'USER', 'ACTIVE'),
('user04', '최지연', 'user04@example.com', '010-2000-0004', 'USER', 'INACTIVE'),
('user05', '정우진', 'user05@example.com', '010-2000-0005', 'USER', 'ACTIVE'),
('ops01', '운영 담당', 'ops@nsight.local', '010-3000-0001', 'OPERATOR', 'ACTIVE'),
('sec01', '보안 담당', 'sec@nsight.local', '010-3000-0002', 'SECURITY', 'ACTIVE'),
('mkt01', '마케팅 담당', 'mkt@nsight.local', '010-4000-0001', 'MARKETING', 'ACTIVE'),
('batch01', '배치 계정', 'batch@nsight.internal', NULL, 'BATCH', 'ACTIVE');
