#!/usr/bin/env bash
# 지정 브랜치로 체크아웃 후 origin ff-only pull
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
GIT_REMOTE="${GIT_REMOTE:-origin}"
GIT_BRANCH="${GIT_BRANCH:-develop}"

cd "${PROJECT_ROOT}"

echo "==> Project: ${PROJECT_ROOT}"
echo "==> git fetch ${GIT_REMOTE} ${GIT_BRANCH}"
git fetch "${GIT_REMOTE}" "${GIT_BRANCH}"

if git rev-parse --verify "${GIT_BRANCH}" >/dev/null 2>&1; then
  git checkout "${GIT_BRANCH}"
else
  git checkout -b "${GIT_BRANCH}" "${GIT_REMOTE}/${GIT_BRANCH}"
fi

echo "==> git pull --ff-only ${GIT_REMOTE} ${GIT_BRANCH}"
git pull --ff-only "${GIT_REMOTE}" "${GIT_BRANCH}"
