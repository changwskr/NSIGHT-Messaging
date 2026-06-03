const API_BASE = '/api/xpilotstyleguide/users';

const userForm = document.getElementById('userForm');
const userIdInput = document.getElementById('userId');
const userNameInput = document.getElementById('userName');
const emailInput = document.getElementById('email');
const phoneNumberInput = document.getElementById('phoneNumber');
const roleCodeInput = document.getElementById('roleCode');
const statusInput = document.getElementById('status');
const saveBtn = document.getElementById('saveBtn');
const resetBtn = document.getElementById('resetBtn');
const cancelEditBtn = document.getElementById('cancelEditBtn');
const formModeBadge = document.getElementById('formModeBadge');
const userTableBody = document.getElementById('userTableBody');
const resultBox = document.getElementById('resultBox');
const listSummary = document.getElementById('listSummary');
const statusBar = document.getElementById('statusBar');
const userPagination = document.getElementById('userPagination');
const filterUserId = document.getElementById('filterUserId');
const filterUserName = document.getElementById('filterUserName');
const filterRoleCode = document.getElementById('filterRoleCode');
const filterStatus = document.getElementById('filterStatus');
const reloadBtn = document.getElementById('reloadBtn');
const deleteModal = document.getElementById('deleteModal');
const deleteModalText = document.getElementById('deleteModalText');
const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');

let editMode = false;
let pendingDeleteUserId = null;
let currentRows = [];
let currentPage = 1;
let totalCount = 0;
const PAGE_SIZE = 3;

function showStatus(message, type = 'info') {
    statusBar.textContent = message;
    statusBar.className = `status-bar ${type}`;
    statusBar.classList.remove('hidden');
}

function isSuccess(data) {
    return !!data?.success;
}

function errorMessage(data) {
    return data?.message || '처리 중 오류가 발생했습니다.';
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text ?? '';
    return div.innerHTML;
}

function displayValue(value) {
    if (value === null || value === undefined || value === '') return '-';
    return String(value);
}

function formatDateTime(value) {
    if (!value) return '-';
    return String(value).replace('T', ' ').replace('Z', '').slice(0, 19);
}

function apiFetch(url, options = {}) {
    return fetch(url, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        }
    }).then(async (res) => {
        const json = await res.json().catch(() => ({}));
        return { ok: res.ok, body: json };
    });
}

function getFormPayload() {
    return {
        userId: userIdInput.value.trim(),
        userName: userNameInput.value.trim(),
        email: emailInput.value.trim(),
        phoneNumber: phoneNumberInput.value.trim(),
        roleCode: roleCodeInput.value.trim(),
        status: statusInput.value
    };
}

function resetForm() {
    userForm.reset();
    userIdInput.readOnly = false;
    editMode = false;
    formModeBadge.textContent = 'CREATE';
    formModeBadge.className = 'mode-badge create';
    saveBtn.textContent = '저장';
    cancelEditBtn.classList.add('hidden');
}

function applyEdit(row) {
    userIdInput.value = row.userId || '';
    userNameInput.value = row.userName || '';
    emailInput.value = row.email || '';
    phoneNumberInput.value = row.phoneNumber || '';
    roleCodeInput.value = row.roleCode || '';
    statusInput.value = row.status || '';

    userIdInput.readOnly = true;
    editMode = true;
    formModeBadge.textContent = 'EDIT';
    formModeBadge.className = 'mode-badge edit';
    saveBtn.textContent = '수정';
    cancelEditBtn.classList.remove('hidden');
}

function renderDetail(row) {
    resultBox.innerHTML = [
        '<div class="detail-summary">',
        `<h3>사용자 ${escapeHtml(displayValue(row.userId))}</h3>`,
        '<dl>',
        `<dt>사용자명</dt><dd>${escapeHtml(displayValue(row.userName))}</dd>`,
        `<dt>이메일</dt><dd>${escapeHtml(displayValue(row.email))}</dd>`,
        `<dt>전화번호</dt><dd>${escapeHtml(displayValue(row.phoneNumber))}</dd>`,
        `<dt>역할코드</dt><dd>${escapeHtml(displayValue(row.roleCode))}</dd>`,
        `<dt>상태</dt><dd>${escapeHtml(displayValue(row.status))}</dd>`,
        `<dt>생성일시</dt><dd>${escapeHtml(formatDateTime(row.createdDate))}</dd>`,
        `<dt>수정일시</dt><dd>${escapeHtml(formatDateTime(row.updatedDate))}</dd>`,
        '</dl>',
        '</div>',
        `<pre class="result-json">${escapeHtml(JSON.stringify(row, null, 2))}</pre>`
    ].join('');
}

function openDeleteModal(userId) {
    pendingDeleteUserId = userId;
    deleteModalText.textContent = `사용자ID ${userId}를 삭제하시겠습니까?`;
    deleteModal.classList.remove('hidden');
    deleteModal.setAttribute('aria-hidden', 'false');
}

function closeDeleteModal() {
    pendingDeleteUserId = null;
    deleteModal.classList.add('hidden');
    deleteModal.setAttribute('aria-hidden', 'true');
}

function buildQuery() {
    const params = new URLSearchParams();
    if (filterUserId.value.trim()) params.append('userId', filterUserId.value.trim());
    if (filterUserName.value.trim()) params.append('userName', filterUserName.value.trim());
    if (filterRoleCode.value.trim()) params.append('roleCode', filterRoleCode.value.trim());
    if (filterStatus.value) params.append('status', filterStatus.value);
    params.append('pageNo', String(currentPage));
    params.append('pageSize', String(PAGE_SIZE));
    const query = params.toString();
    return query ? `?${query}` : '';
}

function getTotalPages() {
    return Math.max(1, Math.ceil(totalCount / PAGE_SIZE));
}

function buildPageNumbers(current, totalPages) {
    if (totalPages <= 7) {
        return Array.from({ length: totalPages }, (_, i) => i + 1);
    }
    const pages = [1];
    if (current > 3) pages.push('...');
    for (let p = Math.max(2, current - 1); p <= Math.min(totalPages - 1, current + 1); p++) {
        pages.push(p);
    }
    if (current < totalPages - 2) pages.push('...');
    pages.push(totalPages);
    return pages;
}

function renderPagination() {
    const totalPages = getTotalPages();
    const current = Math.min(Math.max(1, currentPage), totalPages);
    const pages = buildPageNumbers(current, totalPages);
    let html = `<button type="button" class="page-btn" data-page="prev" ${current <= 1 ? 'disabled' : ''}>이전</button>`;
    pages.forEach((item) => {
        if (item === '...') {
            html += '<span class="page-ellipsis">...</span>';
            return;
        }
        html += `<button type="button" class="page-btn ${item === current ? 'active' : ''}" data-page="${item}">${item}</button>`;
    });
    html += `<button type="button" class="page-btn" data-page="next" ${current >= totalPages ? 'disabled' : ''}>다음</button>`;
    html += `<span class="page-info">${current} / ${totalPages} 페이지</span>`;
    userPagination.innerHTML = html;
}

async function loadUsers(page = currentPage) {
    currentPage = page < 1 ? 1 : page;
    try {
        const res = await apiFetch(`${API_BASE}${buildQuery()}`, { method: 'GET' });
        if (!res.ok || !isSuccess(res.body)) {
            showStatus(errorMessage(res.body), 'error');
            return;
        }

        currentRows = res.body.data || [];
        totalCount = res.body.count ?? currentRows.length;
        currentPage = res.body.pageNo ?? currentPage;
        listSummary.textContent = `전체 ${totalCount}건 · 페이지당 ${PAGE_SIZE}건`;

        if (currentRows.length === 0) {
            userTableBody.innerHTML = '<tr><td colspan="8" class="empty-row">조회 결과가 없습니다.</td></tr>';
            renderPagination();
            return;
        }

        userTableBody.innerHTML = currentRows.map((row) => `
            <tr>
                <td>${escapeHtml(displayValue(row.userId))}</td>
                <td>${escapeHtml(displayValue(row.userName))}</td>
                <td>${escapeHtml(displayValue(row.email))}</td>
                <td>${escapeHtml(displayValue(row.phoneNumber))}</td>
                <td>${escapeHtml(displayValue(row.roleCode))}</td>
                <td><span class="pill ${row.status === 'ACTIVE' ? 'ok' : 'no'}">${escapeHtml(displayValue(row.status))}</span></td>
                <td>${escapeHtml(formatDateTime(row.updatedDate))}</td>
                <td class="col-actions">
                    <button type="button" class="btn-link" data-action="detail" data-user-id="${escapeHtml(row.userId)}">상세</button>
                    <button type="button" class="btn-link" data-action="edit" data-user-id="${escapeHtml(row.userId)}">수정</button>
                    <button type="button" class="btn-link danger-text" data-action="delete" data-user-id="${escapeHtml(row.userId)}">삭제</button>
                </td>
            </tr>
        `).join('');
        renderPagination();
    } catch (err) {
        showStatus(`목록 조회 실패: ${err.message}`, 'error');
    }
}

async function createUser(payload) {
    const res = await apiFetch(API_BASE, {
        method: 'POST',
        body: JSON.stringify(payload)
    });
    if (!res.ok || !isSuccess(res.body)) {
        showStatus(errorMessage(res.body), 'error');
        return false;
    }
    showStatus('사용자정보가 생성되었습니다.', 'success');
    renderDetail(res.body.data || payload);
    resetForm();
    await loadUsers();
    return true;
}

async function updateUser(payload) {
    const res = await apiFetch(`${API_BASE}/${encodeURIComponent(payload.userId)}`, {
        method: 'PUT',
        body: JSON.stringify(payload)
    });
    if (!res.ok || !isSuccess(res.body)) {
        showStatus(errorMessage(res.body), 'error');
        return false;
    }
    showStatus('사용자정보가 수정되었습니다.', 'success');
    renderDetail(res.body.data || payload);
    resetForm();
    await loadUsers();
    return true;
}

async function deleteUser(userId) {
    const res = await apiFetch(`${API_BASE}/${encodeURIComponent(userId)}`, {
        method: 'DELETE'
    });
    if (!res.ok || !isSuccess(res.body)) {
        showStatus(errorMessage(res.body), 'error');
        return false;
    }
    showStatus('사용자정보가 삭제되었습니다.', 'success');
    resultBox.textContent = `삭제 완료: ${userId}`;
    await loadUsers();
    return true;
}

userForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    const payload = getFormPayload();
    if (!payload.userId) {
        showStatus('사용자ID는 필수입니다.', 'error');
        return;
    }
    if (editMode) {
        await updateUser(payload);
        return;
    }
    await createUser(payload);
});

resetBtn.addEventListener('click', () => {
    resetForm();
    showStatus('입력값을 초기화했습니다.', 'info');
});

cancelEditBtn.addEventListener('click', () => {
    resetForm();
    showStatus('수정을 취소했습니다.', 'info');
});

reloadBtn.addEventListener('click', () => {
    currentPage = 1;
    loadUsers(1);
});

userPagination.addEventListener('click', (event) => {
    const btn = event.target.closest('button[data-page]');
    if (!btn || btn.disabled) return;
    const value = btn.dataset.page;
    if (value === 'prev') {
        loadUsers(currentPage - 1);
        return;
    }
    if (value === 'next') {
        loadUsers(currentPage + 1);
        return;
    }
    loadUsers(Number(value));
});

userTableBody.addEventListener('click', async (event) => {
    const btn = event.target.closest('button[data-action]');
    if (!btn) return;
    const userId = btn.dataset.userId;
    const action = btn.dataset.action;
    const row = currentRows.find((item) => String(item.userId) === String(userId));
    if (!row) return;

    if (action === 'detail') {
        renderDetail(row);
        showStatus(`상세 조회: ${userId}`, 'info');
        return;
    }
    if (action === 'edit') {
        applyEdit(row);
        showStatus(`수정 모드: ${userId}`, 'info');
        return;
    }
    if (action === 'delete') {
        openDeleteModal(userId);
    }
});

confirmDeleteBtn.addEventListener('click', async () => {
    const userId = pendingDeleteUserId;
    closeDeleteModal();
    if (!userId) return;
    await deleteUser(userId);
});

deleteModal.querySelectorAll('[data-close="true"]').forEach((el) => {
    el.addEventListener('click', closeDeleteModal);
});

loadUsers(1);
