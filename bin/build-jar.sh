#!/usr/bin/env bash
# NSIGHT Message Management Service — Maven compile + executable JAR package
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# true(기본): 테스트 생략. SKIP_TESTS=false 이면 테스트 포함
SKIP_TESTS="${SKIP_TESTS:-true}"
# true: mvn clean 포함
CLEAN="${CLEAN:-false}"

cd "${PROJECT_ROOT}"

MVN_ARGS=(package -B)
if [[ "${CLEAN}" == "true" ]]; then
  MVN_ARGS=(clean "${MVN_ARGS[@]}")
fi
if [[ "${SKIP_TESTS}" == "true" ]]; then
  MVN_ARGS+=(-DskipTests)
fi

export MAVEN_OPTS="${MAVEN_OPTS:-} -Dfile.encoding=UTF-8"

echo "==> Project: ${PROJECT_ROOT}"
echo "==> mvn ${MVN_ARGS[*]}"
mvn "${MVN_ARGS[@]}"

JAR="${PROJECT_ROOT}/target/nsight-message-mgmt-service-1.0.0.jar"
if [[ ! -f "${JAR}" ]]; then
  JAR="$(find "${PROJECT_ROOT}/target" -maxdepth 1 -name 'nsight-message-mgmt-service-*.jar' ! -name '*.original' -print -quit 2>/dev/null || true)"
fi

if [[ -z "${JAR}" || ! -f "${JAR}" ]]; then
  echo "ERROR: executable JAR not found under target/" >&2
  exit 1
fi

echo "==> Built: ${JAR}"
ls -la "${JAR}"
