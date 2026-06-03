#!/usr/bin/env bash
# TpcUtil.main() — xpilotmessaging HTTP 클라이언트 (앱 8080 기동 필요)
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
cd "${PROJECT_ROOT}"

if [[ "${1:-}" == "-h" || "${1:-}" == "--help" || "${1:-}" == "help" ]]; then
  echo "Usage: run-tpc-util.sh [list|get ID|create|demo]"
  echo "  Server first: mvn spring-boot:run"
  exit 0
fi

echo "==> TpcUtil  project: ${PROJECT_ROOT}"
if [[ $# -eq 0 ]]; then
  mvn -q exec:java
else
  mvn -q exec:java "-Dexec.args=$*"
fi
