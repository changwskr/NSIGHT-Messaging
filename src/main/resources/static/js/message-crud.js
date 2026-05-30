const API_BASE = '/api/v1/messages';
const form = document.getElementById('messageForm');
const resultBox = document.getElementById('resultBox');
const tableBody = document.getElementById('messageTableBody');
const listSummary = document.getElementById('listSummary');
const statusBar = document.getElementById('statusBar');
const formTitle = document.getElementById('formTitle');
const formDesc = document.getElementById('formDesc');
const formModeBadge = document.getElementById('formModeBadge');
const submitBtn = document.getElementById('submitBtn');
const editFromViewBtn = document.getElementById('editFromViewBtn');
const messageIdInput = document.getElementById('messageId');
const messageCodeInput = document.getElementById('messageCode');
const detailMeta = document.getElementById('detailMeta');
const filterType = document.getElementById('filterType');
const filterChannel = document.getElementById('filterChannel');
const filterUseYn = document.getElementById('filterUseYn');
const deleteModal = document.getElementById('deleteModal');
const deleteModalText = document.getElementById('deleteModalText');
const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
const messagePagination = document.getElementById('messagePagination');
const PAGE_SIZE = 3;

const formFields = () => form.querySelectorAll('input:not([type=hidden]), select, textarea');

let pendingDeleteId = null;
let formMode = 'create';
let selectedMessageId = null;
let currentPage = 1;
let totalCount = 0;

function guid() {
    const now = new Date();
    const ymd = now.toISOString().replace(/[-:TZ.]/g, '').slice(0, 14);
    return `${ymd}-MSG-${crypto.randomUUID().slice(0, 8).toUpperCase()}`;
}

function apiHeaders() {
    return {
        'Content-Type': 'application/json',
        'X-GUID': guid(),
        'X-TRACE-ID': crypto.randomUUID(),
        'X-USER-ID': 'ARCHITECT',
        'X-BRANCH-ID': '360001',
        'X-CENTER-ID': 'CENTER_1',
        'X-TERMINAL-ID': 'TERM-360001-001'
    };
}

function toDatetimeLocal(value) {
    if (!value) return '';
    return String(value).replace(' ', 'T').slice(0, 16);
}

function formatDateTime(value) {
    if (!value) return '-';
    return String(value).replace('T', ' ').slice(0, 19);
}

function showStatus(message, type = 'info') {
    statusBar.textContent = message;
    statusBar.className = `status-bar ${type}`;
    statusBar.classList.remove('hidden');
}

function hideStatus() {
    statusBar.classList.add('hidden');
}

function printResult(data) {
    resultBox.textContent = JSON.stringify(data, null, 2);
}

function printDetailPanel(row, apiData) {
    const activeLabel = row.activeNow ? 'ACTIVE (표시 가능)' : 'INACTIVE (비활성)';
    resultBox.innerHTML = `
        <div class="detail-summary">
            <h3>${escapeHtml(row.messageName)} <span class="pill">${escapeHtml(row.messageType)}</span></h3>
            <dl>
                <dt>메시지 ID</dt><dd>${row.messageId}</dd>
                <dt>메시지 코드</dt><dd>${escapeHtml(row.messageCode)}</dd>
                <dt>채널</dt><dd>${escapeHtml(row.channelCode)}</dd>
                <dt>Locale</dt><dd>${escapeHtml(row.locale)}</dd>
                <dt>사용 여부</dt><dd>${row.useYn}</dd>
                <dt>활성 상태</dt><dd>${activeLabel}</dd>
                <dt>표시 기간</dt><dd>${formatDateTime(row.displayStartAt)} ~ ${formatDateTime(row.displayEndAt)}</dd>
                <dt>등록/수정</dt><dd>${escapeHtml(row.createdBy || '-')} / ${escapeHtml(row.updatedBy || '-')}</dd>
                <dt>메시지 내용</dt><dd>${escapeHtml(row.messageContent)}</dd>
            </dl>
        </div>
        <pre class="result-json">${escapeHtml(JSON.stringify(apiData, null, 2))}</pre>
    `;
}

function isSuccess(data) {
    return data?.error?.resultCode === 'SUCCESS';
}

function errorMessage(data) {
    return data?.error?.resultMessage || data?.error?.detailMessage || '처리 중 오류가 발생했습니다.';
}

function setFormReadonly(readonly) {
    formFields().forEach(el => {
        if (el.tagName === 'SELECT') {
            el.disabled = readonly;
        } else {
            el.readOnly = readonly;
        }
    });
}

function setFormMode(mode) {
    formMode = mode;
    const isCreate = mode === 'create';
    const isEdit = mode === 'edit';
    const isView = mode === 'view';

    if (isView) {
        formTitle.textContent = '메시지 상세';
        formDesc.textContent = '선택한 메시지를 조회합니다. 수정하려면 [수정하기]를 누르십시오.';
        formModeBadge.textContent = 'DETAIL';
        formModeBadge.className = 'mode-badge view';
    } else if (isEdit) {
        formTitle.textContent = '메시지 수정';
        formDesc.textContent = '선택한 메시지를 수정합니다. 메시지 코드는 변경할 수 없습니다.';
        formModeBadge.textContent = 'UPDATE';
        formModeBadge.className = 'mode-badge edit';
    } else {
        formTitle.textContent = '메시지 등록';
        formDesc.textContent = '신규 메시지를 등록합니다. 목록에서 [상세] 또는 [수정]을 누르면 해당 메시지가 표시됩니다.';
        formModeBadge.textContent = 'CREATE';
        formModeBadge.className = 'mode-badge create';
    }

    submitBtn.textContent = isEdit ? '수정 저장' : '등록';
    submitBtn.classList.toggle('hidden', !isCreate && !isEdit);
    editFromViewBtn.classList.toggle('hidden', !isView);
    detailMeta.classList.toggle('hidden', isCreate);
    setFormReadonly(isView);
    messageCodeInput.readOnly = isEdit || isView;
    if (isCreate) {
        formFields().forEach(el => {
            if (el.tagName === 'SELECT') el.disabled = false;
            else el.readOnly = false;
        });
    }
}

function resetForm() {
    form.reset();
    messageIdInput.value = '';
    selectedMessageId = null;
    form.locale.value = 'ko_KR';
    form.useYn.value = 'Y';
    setFormMode('create');
    hideStatus();
    resultBox.textContent = '목록에서 [상세]를 누르거나 저장하면 내용이 표시됩니다.';
    loadMessages(1);
}

function formToPayload() {
    const data = Object.fromEntries(new FormData(form).entries());
    delete data.messageId;
    data.displayStartAt = data.displayStartAt || null;
    data.displayEndAt = data.displayEndAt || null;
    return data;
}

function fillDetailMeta(row) {
    document.getElementById('metaMessageId').textContent = row.messageId;
    document.getElementById('metaActiveNow').textContent = row.activeNow ? 'ACTIVE' : 'INACTIVE';
    document.getElementById('metaCreatedBy').textContent = row.createdBy || '-';
    document.getElementById('metaCreatedAt').textContent = formatDateTime(row.createdAt);
    document.getElementById('metaUpdatedBy').textContent = row.updatedBy || '-';
    document.getElementById('metaUpdatedAt').textContent = formatDateTime(row.updatedAt);
}

function fillForm(row, mode) {
    selectedMessageId = row.messageId;
    messageIdInput.value = row.messageId;
    messageCodeInput.value = row.messageCode;
    form.messageName.value = row.messageName;
    form.messageType.value = row.messageType;
    form.channelCode.value = row.channelCode;
    form.locale.value = row.locale;
    form.useYn.value = row.useYn;
    form.displayStartAt.value = toDatetimeLocal(row.displayStartAt);
    form.displayEndAt.value = toDatetimeLocal(row.displayEndAt);
    form.messageContent.value = row.messageContent;
    fillDetailMeta(row);
    setFormMode(mode);
    form.closest('.form-card')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

async function apiFetch(url, options = {}) {
    const headers = apiHeaders();
    if (!options.body) {
        delete headers['Content-Type'];
    }
    const res = await fetch(url, {
        ...options,
        headers: { ...headers, ...(options.headers || {}) }
    });
    return res.json();
}

function getTotalPages() {
    return Math.max(1, Math.ceil(totalCount / PAGE_SIZE));
}

function renderPagination(pageNo) {
    const totalPages = getTotalPages();
    const current = Math.min(Math.max(1, pageNo), totalPages);
    const pages = buildPageNumbers(current, totalPages);

    let html = `
        <button type="button" class="page-btn" data-page="prev" ${current <= 1 ? 'disabled' : ''}>이전</button>
    `;

    pages.forEach(item => {
        if (item === '...') {
            html += '<span class="page-ellipsis">...</span>';
            return;
        }
        html += `<button type="button" class="page-btn ${item === current ? 'active' : ''}" data-page="${item}">${item}</button>`;
    });

    html += `
        <button type="button" class="page-btn" data-page="next" ${current >= totalPages ? 'disabled' : ''}>다음</button>
        <span class="page-info">${current} / ${totalPages} 페이지</span>
    `;
    messagePagination.innerHTML = html;
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

messagePagination.addEventListener('click', (event) => {
    const btn = event.target.closest('button[data-page]');
    if (!btn || btn.disabled) return;

    const value = btn.dataset.page;
    if (value === 'prev') {
        loadMessages(currentPage - 1);
        return;
    }
    if (value === 'next') {
        loadMessages(currentPage + 1);
        return;
    }
    loadMessages(Number(value));
});

async function loadMessages(page = currentPage) {
    const totalPages = Math.max(1, Math.ceil(totalCount / PAGE_SIZE));
    if (page > totalPages && totalCount > 0) {
        page = totalPages;
    }
    if (page < 1) {
        page = 1;
    }
    currentPage = page;

    const params = new URLSearchParams();
    if (filterType.value) params.append('messageType', filterType.value);
    if (filterChannel.value) params.append('channelCode', filterChannel.value);
    if (filterUseYn.value) params.append('useYn', filterUseYn.value);
    params.append('pageNo', String(currentPage));
    params.append('pageSize', String(PAGE_SIZE));

    try {
        const data = await apiFetch(`${API_BASE}?${params.toString()}`, { method: 'GET' });
        if (!isSuccess(data)) {
            showStatus(errorMessage(data), 'error');
            return;
        }
        const rows = data?.body?.response || [];
        totalCount = data?.control?.totalCount ?? 0;
        const pageNo = data?.control?.pageNo ?? currentPage;
        const pageSize = data?.control?.pageSize ?? PAGE_SIZE;
        currentPage = pageNo;
        const pages = Math.max(1, Math.ceil(totalCount / pageSize));
        listSummary.textContent = `전체 ${totalCount}건 · 페이지당 ${pageSize}건`;

        if (rows.length === 0) {
            if (totalCount > 0 && currentPage > 1) {
                await loadMessages(currentPage - 1);
                return;
            }
            tableBody.innerHTML = '<tr><td colspan="9" class="empty-row">조회 결과가 없습니다.</td></tr>';
            renderPagination(1);
            return;
        }

        tableBody.innerHTML = rows.map(row => `
            <tr data-id="${row.messageId}" class="${selectedMessageId == row.messageId ? 'selected' : ''}">
                <td>${row.messageId}</td>
                <td><strong>${escapeHtml(row.messageCode)}</strong></td>
                <td>${escapeHtml(row.messageName)}</td>
                <td><span class="pill">${row.messageType}</span></td>
                <td>${row.channelCode}</td>
                <td>${row.useYn}</td>
                <td><span class="pill ${row.activeNow ? 'ok' : 'no'}">${row.activeNow ? 'ACTIVE' : 'INACTIVE'}</span></td>
                <td>${row.updatedAt ? formatDateTime(row.updatedAt) : ''}</td>
                <td class="col-actions">
                    <button type="button" class="btn-link" data-action="detail" data-id="${row.messageId}">상세</button>
                    <button type="button" class="btn-link" data-action="edit" data-id="${row.messageId}">수정</button>
                    <button type="button" class="btn-link danger-text" data-action="delete" data-id="${row.messageId}" data-code="${escapeAttr(row.messageCode)}">삭제</button>
                </td>
            </tr>
        `).join('');
        renderPagination(pageNo);
    } catch (e) {
        showStatus('목록 조회 실패: ' + e.message, 'error');
    }
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text ?? '';
    return div.innerHTML;
}

function escapeAttr(text) {
    return escapeHtml(text).replace(/"/g, '&quot;');
}

async function loadDetail(messageId, displayMode = 'view') {
    const data = await apiFetch(`${API_BASE}/${messageId}`, { method: 'GET' });
    if (!isSuccess(data) || !data?.body?.response) {
        printResult(data);
        showStatus(errorMessage(data), 'error');
        return null;
    }
    const row = data.body.response;
    fillForm(row, displayMode);
    printDetailPanel(row, data);
    showStatus(`상세 조회: ${row.messageCode}`, 'info');
    await loadMessages();
    return row;
}

form.addEventListener('submit', async (event) => {
    event.preventDefault();
    if (formMode === 'view') return;

    const payload = formToPayload();
    const isEdit = formMode === 'edit';
    const url = isEdit ? `${API_BASE}/${messageIdInput.value}` : API_BASE;
    const method = isEdit ? 'PUT' : 'POST';

    try {
        const data = await apiFetch(url, { method, body: JSON.stringify(payload) });
        printResult(data);
        if (!isSuccess(data)) {
            showStatus(errorMessage(data), 'error');
            return;
        }
        showStatus(isEdit ? '메시지가 수정되었습니다.' : '메시지가 등록되었습니다.', 'success');
        if (isEdit && data?.body?.response) {
            fillForm(data.body.response, 'view');
            printDetailPanel(data.body.response, data);
        } else {
            resetForm();
        }
        await loadMessages();
    } catch (e) {
        showStatus('저장 실패: ' + e.message, 'error');
    }
});

tableBody.addEventListener('click', async (event) => {
    const btn = event.target.closest('button[data-action]');
    if (!btn) return;

    const messageId = btn.dataset.id;
    const action = btn.dataset.action;

    if (action === 'detail') {
        await loadDetail(messageId, 'view');
        return;
    }

    if (action === 'edit') {
        await loadDetail(messageId, 'edit');
        return;
    }

    if (action === 'delete') {
        pendingDeleteId = messageId;
        deleteModalText.textContent = `메시지 [${btn.dataset.code}] (ID: ${messageId})를 삭제하시겠습니까?`;
        deleteModal.classList.remove('hidden');
        deleteModal.setAttribute('aria-hidden', 'false');
    }
});

editFromViewBtn.addEventListener('click', () => {
    if (formMode !== 'view' || !messageIdInput.value) return;
    setFormMode('edit');
    showStatus('수정 모드로 전환되었습니다.', 'info');
});

confirmDeleteBtn.addEventListener('click', async () => {
    if (!pendingDeleteId) return;
    try {
        const data = await apiFetch(`${API_BASE}/${pendingDeleteId}`, { method: 'DELETE' });
        printResult(data);
        closeDeleteModal();
        if (!isSuccess(data)) {
            showStatus(errorMessage(data), 'error');
            return;
        }
        showStatus('메시지가 삭제되었습니다.', 'success');
        if (String(messageIdInput.value) === String(pendingDeleteId)) resetForm();
        else await loadMessages(currentPage);
    } catch (e) {
        showStatus('삭제 실패: ' + e.message, 'error');
    }
});

function closeDeleteModal() {
    deleteModal.classList.add('hidden');
    deleteModal.setAttribute('aria-hidden', 'true');
    pendingDeleteId = null;
}

deleteModal.querySelectorAll('[data-close="true"]').forEach(el => {
    el.addEventListener('click', closeDeleteModal);
});

document.getElementById('resetBtn').addEventListener('click', resetForm);
document.getElementById('newBtn').addEventListener('click', resetForm);
document.getElementById('reloadBtn').addEventListener('click', () => loadMessages(1));
filterType.addEventListener('change', () => loadMessages(1));
filterChannel.addEventListener('change', () => loadMessages(1));
filterUseYn.addEventListener('change', () => loadMessages(1));

resetForm();
