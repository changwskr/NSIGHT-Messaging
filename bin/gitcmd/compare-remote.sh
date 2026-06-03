#!/usr/bin/env bash
# 현재 브랜치(HEAD) vs origin/<GIT_BRANCH> 차이 요약
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
GIT_REMOTE="${GIT_REMOTE:-origin}"
GIT_BRANCH="${GIT_BRANCH:-master}"

cd "${PROJECT_ROOT}"

echo "==> Project: ${PROJECT_ROOT}"
echo "==> Compare: HEAD vs ${GIT_REMOTE}/${GIT_BRANCH}"
echo

echo "==> git fetch ${GIT_REMOTE} ${GIT_BRANCH}"
git fetch "${GIT_REMOTE}" "${GIT_BRANCH}"

read -r AHEAD BEHIND < <(git rev-list --left-right --count "HEAD...${GIT_REMOTE}/${GIT_BRANCH}")

echo
echo "[커밋] HEAD에만 있음(앞섬): ${AHEAD}  /  ${GIT_REMOTE}/${GIT_BRANCH}에만 있음(뒤처짐): ${BEHIND}"
echo

if [[ "${BEHIND}" -eq 0 ]]; then
  echo "pull/merge로 받을 커밋이 없습니다."
  exit 0
fi

echo "==> 받아올 커밋 목록 (HEAD..${GIT_REMOTE}/${GIT_BRANCH})"
git log --oneline "HEAD..${GIT_REMOTE}/${GIT_BRANCH}"
echo

FILE_COUNT="$(git diff --name-only "HEAD...${GIT_REMOTE}/${GIT_BRANCH}" | wc -l | tr -d ' ')"
echo "[파일] 변경/추가 대상 파일 수: ${FILE_COUNT}"
echo
echo "==> diff --stat"
git diff --stat "HEAD...${GIT_REMOTE}/${GIT_BRANCH}"
echo
echo "==> 변경 파일 목록"
git diff --name-only "HEAD...${GIT_REMOTE}/${GIT_BRANCH}"
echo
echo "merge 예: ./bin/gitcmd/merge-remote-into-current.sh"
echo "pull 예: GIT_BRANCH=${GIT_BRANCH} ./bin/gitcmd/pull-remote.sh"
