const API_BASE = '/api/v1/transaction-logs';
const PAGE_SIZE = 3;

const searchForm = document.getElementById('searchForm');
const txTableBody = document.getElementById('txTableBody');
const listSummary = document.getElementById('listSummary');
const detailBox = document.getElementById('detailBox');
const statusBar = document.getElementById('statusBar');
const txPagination = document.getElementById('txPagination');
const deleteModal = document.getElementById('deleteModal');
const deleteModalText = document.getElementById('deleteModalText');
const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
const deleteSearchBtn = document.getElementById('deleteSearchBtn');

let pendingDeleteMode = null;
let pendingDeleteId = null;

const HEADER_FIELDS = [
    ['guid', 'GUID'],
    ['traceId', 'Trace ID'],
    ['spanId', 'Span ID'],
    ['transactionId', 'Transaction ID'],
    ['interfaceId', 'Interface ID'],
    ['serviceId', 'Service ID'],
    ['requestDateTime', '요청일시'],
    ['responseDateTime', '응답일시'],
    ['sourceSystemId', 'Source System'],
    ['targetSystemId', 'Target System'],
    ['channelId', 'Channel ID'],
    ['terminalId', 'Terminal ID'],
    ['userId', 'User ID'],
    ['branchId', 'Branch ID'],
    ['centerId', 'Center ID'],
    ['apId', 'AP ID'],
    ['requestType', 'Request Type'],
    ['messageType', 'Message Type'],
    ['version', 'Version'],
    ['clientIp', 'Client IP']
];

const CONTROL_FIELDS = [
    ['timeout', 'Timeout(ms)'],
    ['retryYn', 'Retry Y/N'],
    ['retryCount', 'Retry Count'],
    ['pageNo', 'Page No'],
    ['pageSize', 'Page Size'],
    ['totalCount', 'Total Count']
];

const SECURITY_FIELDS = [
    ['maskingLevel', 'Masking Level'],
    ['dataGrade', 'Data Grade'],
    ['accessPurpose', 'Access Purpose'],
    ['auditRequiredYn', 'Audit Required']
];

const ERROR_FIELDS = [
    ['resultCode', 'Result Code'],
    ['resultMessage', 'Result Message'],
    ['errorCode', 'Error Code'],
    ['errorMessage', 'Error Message'],
    ['errorDetail', 'Error Detail'],
    ['errorSystemId', 'Error System ID'],
    ['errorDateTime', 'Error DateTime']
];

let currentPage = 1;
let totalCount = 0;
let selectedTxLogId = null;

function apiHeaders() {
    return {
        'X-GUID': crypto.randomUUID(),
        'X-TRACE-ID': crypto.randomUUID(),
        'X-USER-ID': 'ARCHITECT',
        'X-BRANCH-ID': '360001'
    };
}

function showStatus(message, type = 'info') {
    statusBar.textContent = message;
    statusBar.className = `status-bar ${type}`;
    statusBar.classList.remove('hidden');
}

function isSuccess(data) {
    return data?.error?.resultCode === 'SUCCESS';
}

function errorMessage(data) {
    return data?.error?.resultMessage || data?.error?.detailMessage || '처리 중 오류가 발생했습니다.';
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

function normalizeRow(row) {
    return {
        txLogId: row.txLogId,
        requestUri: row.requestUri,
        httpMethod: row.httpMethod,
        createdAt: row.createdAt,
        header: row.header || {},
        control: row.control || {},
        security: row.security || {},
        error: row.error || {}
    };
}

function renderFieldList(sectionObj, fields) {
    return fields.map(([key, label]) => {
        const val = displayValue(sectionObj?.[key]);
        return `<dt>${escapeHtml(label)}</dt><dd>${escapeHtml(val)}</dd>`;
    }).join('');
}

function renderEnvelopeCard(title, cssClass, sectionObj, fields) {
    const failClass = cssClass === 'error' && sectionObj?.resultCode === 'FAIL' ? ' fail' : '';
    return `
        <section class="envelope-card ${cssClass}${failClass}">
            <h3>${escapeHtml(title)}</h3>
            <dl>${renderFieldList(sectionObj, fields)}</dl>
        </section>
    `;
}

function printDetail(row) {
    const tx = normalizeRow(row);
    const h = tx.header;
    const e = tx.error;
    detailBox.innerHTML = `
        <div class="tx-detail-actions">
            <button type="button" class="danger" data-action="delete" data-id="${tx.txLogId}">이 건 삭제 (DB+전문파일)</button>
        </div>
        <div class="tx-detail-meta">
            <strong>로그 ID ${tx.txLogId}</strong>
            · ${escapeHtml(tx.httpMethod)} ${escapeHtml(tx.requestUri)}
            · 등록 ${formatDateTime(tx.createdAt)}
            · GUID ${escapeHtml(displayValue(h.guid))}
            · <span class="pill ${e.resultCode === 'SUCCESS' ? 'ok' : 'no'}">${escapeHtml(displayValue(e.resultCode))}</span>
        </div>
        <div class="tx-envelope-grid">
            ${renderEnvelopeCard('Header', 'header', h, HEADER_FIELDS)}
            ${renderEnvelopeCard('Control', 'control', tx.control, CONTROL_FIELDS)}
            ${renderEnvelopeCard('Security', 'security', tx.security, SECURITY_FIELDS)}
            ${renderEnvelopeCard('Error', 'error', e, ERROR_FIELDS)}
        </div>
    `;
}

function renderListCellHeader(h) {
    return `
        <div class="tx-list-cell">
            <strong>${escapeHtml(displayValue(h.transactionId))}</strong>
            <span>${escapeHtml(displayValue(h.serviceId))}</span>
            <span>${escapeHtml(displayValue(h.userId))}</span>
        </div>
    `;
}

function renderListCellControl(c) {
    const page = c.pageNo != null ? `P${c.pageNo}/${displayValue(c.pageSize)}` : '-';
    return `
        <div class="tx-list-cell">
            <strong>timeout ${displayValue(c.timeout)}</strong>
            <span>retry ${displayValue(c.retryYn)} (${displayValue(c.retryCount)})</span>
            <span>page ${page}</span>
            <span>total ${displayValue(c.totalCount)}</span>
        </div>
    `;
}

function renderListCellSecurity(s) {
    return `
        <div class="tx-list-cell">
            <strong>${escapeHtml(displayValue(s.maskingLevel))}</strong>
            <span>${escapeHtml(displayValue(s.dataGrade))}</span>
            <span>${escapeHtml(displayValue(s.accessPurpose))}</span>
            <span>audit ${escapeHtml(displayValue(s.auditRequiredYn))}</span>
        </div>
    `;
}

function renderListCellError(e) {
    return `
        <div class="tx-list-cell">
            <strong><span class="pill ${e.resultCode === 'SUCCESS' ? 'ok' : 'no'}">${escapeHtml(displayValue(e.resultCode))}</span></strong>
            <span>${escapeHtml(displayValue(e.resultMessage))}</span>
            <span>${escapeHtml(displayValue(e.errorCode))}</span>
            <span>${escapeHtml(displayValue(e.errorMessage))}</span>
        </div>
    `;
}

function formatDateTime(value) {
    if (!value) return '';
    return String(value).replace('T', ' ').slice(0, 19);
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

function renderPagination(pageNo) {
    const totalPages = getTotalPages();
    const current = Math.min(Math.max(1, pageNo), totalPages);
    const pages = buildPageNumbers(current, totalPages);
    let html = `<button type="button" class="page-btn" data-page="prev" ${current <= 1 ? 'disabled' : ''}>이전</button>`;
    pages.forEach(item => {
        if (item === '...') {
            html += '<span class="page-ellipsis">...</span>';
            return;
        }
        html += `<button type="button" class="page-btn ${item === current ? 'active' : ''}" data-page="${item}">${item}</button>`;
    });
    html += `<button type="button" class="page-btn" data-page="next" ${current >= totalPages ? 'disabled' : ''}>다음</button>`;
    html += `<span class="page-info">${current} / ${totalPages} 페이지</span>`;
    txPagination.innerHTML = html;
}

function buildSearchParams(page, forDelete = false) {
    const params = new URLSearchParams();
    const guid = document.getElementById('filterGuid').value.trim();
    const traceId = document.getElementById('filterTraceId').value.trim();
    const transactionId = document.getElementById('filterTransactionId').value.trim();
    const serviceId = document.getElementById('filterServiceId').value.trim();
    const resultCode = document.getElementById('filterResultCode').value;
    const userId = document.getElementById('filterUserId').value.trim();
    if (guid) params.append('guid', guid);
    if (traceId) params.append('traceId', traceId);
    if (transactionId) params.append('transactionId', transactionId);
    if (serviceId) params.append('serviceId', serviceId);
    if (resultCode) params.append('resultCode', resultCode);
    if (userId) params.append('userId', userId);
    if (!forDelete) {
        params.append('pageNo', String(page));
        params.append('pageSize', String(PAGE_SIZE));
    }
    return params;
}

function renderDeletedFiles(result) {
    const paths = result?.deletedFilePaths || [];
    if (paths.length === 0) {
        return '<p class="tx-deleted-files">삭제된 전문 파일이 없습니다. (이미 삭제되었거나 경로 불일치)</p>';
    }
    const items = paths.map(p => `<li>${escapeHtml(p)}</li>`).join('');
    return `<div class="tx-deleted-files"><strong>삭제된 전문 파일 ${paths.length}건</strong><ul>${items}</ul></div>`;
}

async function apiFetch(url, options = {}) {
    return fetch(url, {
        ...options,
        headers: { ...apiHeaders(), ...(options.headers || {}) }
    }).then(res => res.json());
}

function openDeleteModal(mode, txLogId) {
    pendingDeleteMode = mode;
    pendingDeleteId = txLogId;
    if (mode === 'single') {
        deleteModalText.textContent = `트랜잭션 로그 ID ${txLogId}와 연관 REQ/RES 전문 파일을 삭제하시겠습니까?`;
    } else {
        deleteModalText.textContent = `현재 조회 조건에 해당하는 전체 ${totalCount}건과 연관 전문 파일을 삭제하시겠습니까?`;
    }
    deleteModal.classList.remove('hidden');
    deleteModal.setAttribute('aria-hidden', 'false');
}

function closeDeleteModal() {
    deleteModal.classList.add('hidden');
    deleteModal.setAttribute('aria-hidden', 'true');
    pendingDeleteMode = null;
    pendingDeleteId = null;
}

async function executeDelete() {
    const mode = pendingDeleteMode;
    const txLogId = pendingDeleteId;
    closeDeleteModal();
    try {
        let data;
        if (mode === 'single') {
            data = await apiFetch(`${API_BASE}/${txLogId}`, { method: 'DELETE' });
        } else {
            data = await apiFetch(`${API_BASE}?${buildSearchParams(1, true).toString()}`, { method: 'DELETE' });
        }
        if (!isSuccess(data)) {
            showStatus(errorMessage(data), 'error');
            return;
        }
        const result = data?.body?.response || {};
        if (mode === 'single' && String(selectedTxLogId) === String(txLogId)) {
            selectedTxLogId = null;
            clearDetail();
        }
        detailBox.insertAdjacentHTML('beforeend', renderDeletedFiles(result));
        showStatus(
            `삭제 완료: 로그 ${result.deletedLogCount ?? 0}건, 전문 파일 ${result.deletedFileCount ?? 0}건`,
            'success'
        );
        await loadLogs(currentPage);
    } catch (err) {
        showStatus('삭제 실패: ' + err.message, 'error');
    }
}

async function loadLogs(page = currentPage) {
    if (page > getTotalPages() && totalCount > 0) page = getTotalPages();
    if (page < 1) page = 1;
    currentPage = page;

    try {
        const data = await apiFetch(`${API_BASE}?${buildSearchParams(currentPage).toString()}`, { method: 'GET' });
        if (!isSuccess(data)) {
            showStatus(errorMessage(data), 'error');
            return;
        }
        const rows = (data?.body?.response || []).map(normalizeRow);
        totalCount = data?.control?.totalCount ?? 0;
        currentPage = data?.control?.pageNo ?? currentPage;
        listSummary.textContent = `전체 ${totalCount}건 · 페이지당 ${PAGE_SIZE}건`;

        if (rows.length === 0) {
            if (totalCount > 0 && currentPage > 1) {
                await loadLogs(currentPage - 1);
                return;
            }
            txTableBody.innerHTML = '<tr><td colspan="9" class="empty-row">조회 결과가 없습니다.</td></tr>';
            renderPagination(1);
            return;
        }

        txTableBody.innerHTML = rows.map(row => `
            <tr data-id="${row.txLogId}" class="${selectedTxLogId == row.txLogId ? 'selected' : ''}">
                <td>${row.txLogId}</td>
                <td class="col-guid">${escapeHtml(displayValue(row.header.guid))}</td>
                <td>${renderListCellHeader(row.header)}</td>
                <td>${renderListCellControl(row.control)}</td>
                <td>${renderListCellSecurity(row.security)}</td>
                <td>${renderListCellError(row.error)}</td>
                <td class="col-path">${escapeHtml(row.requestUri)}</td>
                <td>${formatDateTime(row.createdAt)}</td>
                <td class="col-actions">
                    <button type="button" class="btn-link" data-action="detail" data-id="${row.txLogId}">상세</button>
                    <button type="button" class="btn-link danger-text" data-action="delete" data-id="${row.txLogId}">삭제</button>
                </td>
            </tr>
        `).join('');
        renderPagination(currentPage);

        const selectedStillVisible = rows.some(r => r.txLogId == selectedTxLogId);
        if (selectedStillVisible) {
            printDetail(rows.find(r => r.txLogId == selectedTxLogId));
        }
    } catch (err) {
        showStatus('목록 조회 실패: ' + err.message, 'error');
    }
}

async function loadDetail(txLogId) {
    const data = await apiFetch(`${API_BASE}/${txLogId}`, { method: 'GET' });
    if (!isSuccess(data) || !data?.body?.response) {
        showStatus(errorMessage(data), 'error');
        return;
    }
    selectedTxLogId = Number(txLogId);
    printDetail(data.body.response);
    showStatus(`상세 조회: ID ${txLogId}`, 'info');
    await loadLogs(currentPage);
}

function clearDetail() {
    detailBox.innerHTML = '<p class="tx-detail-placeholder">조회 결과에서 항목을 선택하세요.</p>';
}

searchForm.addEventListener('submit', (e) => {
    e.preventDefault();
    selectedTxLogId = null;
    clearDetail();
    loadLogs(1);
});

document.getElementById('resetBtn').addEventListener('click', () => {
    searchForm.reset();
    selectedTxLogId = null;
    clearDetail();
    loadLogs(1);
});

txPagination.addEventListener('click', (event) => {
    const btn = event.target.closest('button[data-page]');
    if (!btn || btn.disabled) return;
    const value = btn.dataset.page;
    if (value === 'prev') loadLogs(currentPage - 1);
    else if (value === 'next') loadLogs(currentPage + 1);
    else loadLogs(Number(value));
});

txTableBody.addEventListener('click', async (event) => {
    const btn = event.target.closest('button[data-action]');
    if (!btn) return;
    const id = btn.dataset.id;
    const action = btn.dataset.action;
    if (action === 'delete') {
        openDeleteModal('single', id);
        return;
    }
    if (action === 'detail') {
        await loadDetail(id);
        return;
    }
    const row = event.target.closest('tr[data-id]');
    if (row?.dataset?.id) {
        await loadDetail(row.dataset.id);
    }
});

detailBox.addEventListener('click', (event) => {
    const btn = event.target.closest('button[data-action="delete"]');
    if (!btn) return;
    openDeleteModal('single', btn.dataset.id);
});

deleteSearchBtn.addEventListener('click', () => {
    if (totalCount === 0) {
        showStatus('삭제할 조회 결과가 없습니다.', 'error');
        return;
    }
    openDeleteModal('search', null);
});

confirmDeleteBtn.addEventListener('click', () => executeDelete());

deleteModal.querySelectorAll('[data-close="true"]').forEach(el => {
    el.addEventListener('click', closeDeleteModal);
});

loadLogs(1);
