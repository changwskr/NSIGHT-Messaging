#!/usr/bin/env bash
curl -X POST http://localhost:8080/api/v1/messages \
  -H "Content-Type: application/json" \
  -H "X-GUID: 20260530-MSG-000001" \
  -H "X-USER-ID: ARCHITECT" \
  -H "X-BRANCH-ID: 360001" \
  -d '{
    "messageCode": "MSG_NOTICE_001",
    "messageName": "공지 메시지",
    "messageType": "NOTICE",
    "channelCode": "WEBTOPSUITE",
    "locale": "ko_KR",
    "messageContent": "NSIGHT 메시징 관리 서비스 등록 테스트입니다.",
    "displayStartAt": "2026-05-30T09:00:00",
    "displayEndAt": "2026-12-31T18:00:00",
    "useYn": "Y"
  }'
