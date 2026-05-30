# 메시징 관리 서비스 API

## 1. 메시지 등록

```http
POST /api/v1/messages
Content-Type: application/json
X-GUID: 20260530-MSG-000001
X-USER-ID: ARCHITECT
```

```json
{
  "messageCode": "MSG_NOTICE_001",
  "messageName": "공지 메시지",
  "messageType": "INFO",
  "channelCode": "WEBTOPSUITE",
  "locale": "ko_KR",
  "messageContent": "메시지 본문입니다.",
  "displayStartAt": "2026-05-30T09:00:00",
  "displayEndAt": "2026-12-31T18:00:00",
  "useYn": "Y"
}
```

## 2. 표준 응답 구조

```json
{
  "header": {
    "guid": "20260530-MSG-000001",
    "traceId": "TRC-...",
    "transactionId": "MSG-CREATE-001",
    "serviceId": "messageCreate",
    "sourceSystemId": "MSG-MGMT-SERVICE",
    "targetSystemId": "WEBTOPSUITE",
    "messageType": "RESPONSE",
    "version": "1.0"
  },
  "body": {
    "response": {}
  },
  "control": {},
  "security": {},
  "error": {
    "resultCode": "SUCCESS",
    "resultMessage": "정상 처리되었습니다."
  }
}
```
