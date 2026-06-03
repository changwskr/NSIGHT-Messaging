#!/usr/bin/env bash
# 로컬 브랜치 vs origin 추적 상태 요약
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
GIT_REMOTE="${GIT_REMOTE:-origin}"

cd "${PROJECT_ROOT}"

echo "==> Project: ${PROJECT_ROOT}"
echo "==> Current: $(git branch --show-current)"
echo
echo "==> git fetch ${GIT_REMOTE}"
git fetch "${GIT_REMOTE}"
echo
echo "==> branch -vv"
git branch -vv
echo

for BR in develop master; do
  if git rev-parse --verify "${BR}" >/dev/null 2>&1; then
    echo "--- ${BR} vs ${GIT_REMOTE}/${BR} ---"
    read -r AHEAD BEHIND < <(git rev-list --left-right --count "${BR}...${GIT_REMOTE}/${BR}")
    echo "    ahead ${AHEAD} / behind ${BEHIND}"
    echo
  fi
done

echo "master 비교(HEAD): ./bin/gitcmd/compare-remote.sh"
echo "develop pull: GIT_BRANCH=develop ./bin/gitcmd/pull-remote.sh"
