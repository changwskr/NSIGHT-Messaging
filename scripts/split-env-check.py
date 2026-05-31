# -*- coding: utf-8 -*-
from pathlib import Path

root = Path(__file__).resolve().parents[1]
check = root / "src/main/resources/templates/traceenvironment/check.html"
lines = check.read_text(encoding="utf-8").splitlines(keepends=True)

head = """<!DOCTYPE html>
<html lang="ko" xmlns:th="http://www.thymeleaf.org">
<head th:replace="~{traceenvironment/fragments/env-page-head :: head('Rule·점검')}"></head>
<body data-env-page="check">
<th:block th:replace="~{traceenvironment/fragments/env-page-head :: topbar('프로젝트 기준 · 설정 업로드 · Rule Engine 점검')}"></th:block>

<div id="statusBar" class="status-bar hidden" role="status"></div>

<main class="env-layout">
"""

start = next(i for i, l in enumerate(lines) if "프로젝트 기준정보" in l)
while start > 0 and "<section" not in lines[start - 1]:
    start -= 1
start -= 0
tail = "".join(lines[start:])
tail = tail.replace('<script src="/js/traceenvironment-planner.js"></script>\n', "")
out = head + tail
check.write_text(out, encoding="utf-8")
print("wrote check.html from line", start + 1)
