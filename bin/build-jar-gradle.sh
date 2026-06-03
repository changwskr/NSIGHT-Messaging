#!/usr/bin/env bash
# NSIGHT Message Management Service — Gradle bootJar
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

SKIP_TESTS="${SKIP_TESTS:-true}"
CLEAN="${CLEAN:-false}"

cd "${PROJECT_ROOT}"

GRADLE_CMD="./gradlew"
if [[ ! -x "${GRADLE_CMD}" ]]; then
  echo "ERROR: gradlew not found. Run: gradle wrapper" >&2
  exit 1
fi

TASK=(bootJar)
if [[ "${CLEAN}" == "true" ]]; then
  TASK=(clean "${TASK[@]}")
fi
if [[ "${SKIP_TESTS}" == "true" ]]; then
  "${GRADLE_CMD}" "${TASK[@]}" -x test --no-daemon
else
  "${GRADLE_CMD}" "${TASK[@]}" --no-daemon
fi

JAR="${PROJECT_ROOT}/build/libs/nsight-message-mgmt-service-1.0.0.jar"
if [[ ! -f "${JAR}" ]]; then
  JAR="$(find "${PROJECT_ROOT}/build/libs" -maxdepth 1 -name 'nsight-message-mgmt-service-*.jar' ! -name '*.original' -print -quit 2>/dev/null || true)"
fi

if [[ -z "${JAR}" || ! -f "${JAR}" ]]; then
  echo "ERROR: executable JAR not found under build/libs/" >&2
  exit 1
fi

echo "==> Built: ${JAR}"
ls -la "${JAR}"
