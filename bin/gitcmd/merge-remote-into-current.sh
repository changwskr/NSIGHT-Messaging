#!/usr/bin/env bash
# 현재 브랜치에 origin/<GIT_BRANCH> merge
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
GIT_REMOTE="${GIT_REMOTE:-origin}"
GIT_BRANCH="${GIT_BRANCH:-master}"

cd "${PROJECT_ROOT}"

CURRENT="$(git branch --show-current)"
echo "==> Project: ${PROJECT_ROOT}"
echo "==> Current branch: ${CURRENT}"
echo "==> Merge: ${GIT_REMOTE}/${GIT_BRANCH} -> ${CURRENT}"
echo

echo "==> git fetch ${GIT_REMOTE} ${GIT_BRANCH}"
git fetch "${GIT_REMOTE}" "${GIT_BRANCH}"

read -r _ BEHIND < <(git rev-list --left-right --count "HEAD...${GIT_REMOTE}/${GIT_BRANCH}")
if [[ "${BEHIND}" -eq 0 ]]; then
  echo "이미 ${GIT_REMOTE}/${GIT_BRANCH} 내용이 반영되어 있습니다."
  exit 0
fi

"${SCRIPT_DIR}/compare-remote.sh"
echo
read -r -p "위 내용으로 merge 하시겠습니까? (Y/N): " CONFIRM
if [[ ! "${CONFIRM}" =~ ^[Yy]$ ]]; then
  echo "취소되었습니다."
  exit 0
fi

echo "==> git merge ${GIT_REMOTE}/${GIT_BRANCH}"
git merge "${GIT_REMOTE}/${GIT_BRANCH}"
