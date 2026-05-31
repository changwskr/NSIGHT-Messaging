#!/usr/bin/env bash
# 원격 develop 최신 반영 후 build-jar.sh 실행
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
GIT_BRANCH="${GIT_BRANCH:-develop}"

cd "${PROJECT_ROOT}"

echo "==> Project: ${PROJECT_ROOT}"
echo "==> git fetch origin ${GIT_BRANCH}"
git fetch origin "${GIT_BRANCH}"

if git rev-parse --verify "${GIT_BRANCH}" >/dev/null 2>&1; then
  git checkout "${GIT_BRANCH}"
else
  git checkout -b "${GIT_BRANCH}" "origin/${GIT_BRANCH}"
fi

echo "==> git pull --ff-only origin ${GIT_BRANCH}"
git pull --ff-only origin "${GIT_BRANCH}"

echo "==> build-jar.sh"
"${SCRIPT_DIR}/build-jar.sh"
