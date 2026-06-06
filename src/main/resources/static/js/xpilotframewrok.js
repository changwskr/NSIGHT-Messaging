const PERF_API = '/api/xpilotframewrok/performance-logs';
const AUDIT_API = '/api/xpilotframewrok/audit-logs';
const PROCESS_API = '/api/xpilotframewrok/process';
const PAGE_SIZE = 10;

const statusBar = document.getElementById('statusBar');
const perfSearchForm = document.getElementById('perfSearchForm');
const auditSearchForm = document.getElementById('auditSearchForm');
const processForm = document.getElementById('processForm');
const perfTableBody = document.getElementById('perfTableBody');
const auditTableBody = document.getElementById('auditTableBody');
const perfListSummary = document.getElementById('perfListSummary');
const auditListSummary = document.getElementById('auditListSummary');
const perfDetailBox = document.getElementById('perfDetailBox');
const perfPagination = document.getElementById('perfPagination');
const auditPagination = document.getElementById('auditPagination');
const processResult = document.getElementById('processResult');
const deleteModal = document.getElementById('deleteModal');
const deleteModalText = document.getElementById('deleteModalText');
const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');

let perfPage = 1;
let auditPage = 1;
let pendingDeleteId = null;

function apiHeaders() {
    return {
        'Content-Type': 'application/json',
        'X-GUID': crypto.randomUUID(),
        'X-TRACE-ID': crypto.randomUUID(),
        'X-USER-ID': 'ARCHITECT',
        'X-BRANCH-ID': '360001'
    };
}

function showStatus(message, type = 'info') {
    statusBar.textContent = message;
    statusBar.className = 'status-bar ' + type;
    statusBar.classList.remove('hidden');
}

function hideStatus() {
    statusBar.classList.add('hidden');
}

function formatDateTime(value) {
    if (!value) return '-';
    return String(value).replace('T', ' ').substring(0, 19);
}

function buildQuery(params) {
    const q = new URLSearchParams();
    Object.entries(params).forEach(([k, v]) => {
        if (v != null && String(v).trim() !== '') q.set(k, v);
    });
    return q.toString();
}

function renderPagination(nav, page, total, onPage) {
    const totalPages = Math.max(1, Math.ceil(total / PAGE_SIZE));
    nav.innerHTML = '';
    for (let p = 1; p <= totalPages; p++) {
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.textContent = String(p);
        btn.className = p === page ? 'active' : '';
        btn.addEventListener('click', () => onPage(p));
        nav.appendChild(btn);
    }
}

function renderPerfDetail(row) {
    if (!row) {
        perfDetailBox.innerHTML = '<p class="tx-detail-placeholder">목록에서 항목을 선택하세요.</p>';
        return;
    }
    const fields = [
        ['logId', 'Log ID'], ['guid', 'GUID'], ['traceId', 'Trace ID'],
        ['serviceId', 'Service ID'], ['userId', 'User ID'],
        ['requestUri', 'Request URI'], ['httpMethod', 'HTTP Method'],
        ['resultCode', 'Result Code'], ['errorCode', 'Error Code'],
        ['apId', 'AP ID'], ['dbTime', 'DB Time(ms)'],
        ['extTime', 'EXT Time(ms)'], ['totalTime', 'Total Time(ms)'],
        ['logTime', 'Log Time']
    ];
    perfDetailBox.innerHTML = fields.map(([k, label]) =>
        `<div class="tx-detail-row"><span>${label}</span><strong>${row[k] ?? '-'}</strong></div>`
    ).join('');
}

async function fetchPerfList(page = 1) {
    perfPage = page;
    const params = {
        guid: document.getElementById('perfGuid').value,
        traceId: document.getElementById('perfTraceId').value,
        serviceId: document.getElementById('perfServiceId').value,
        resultCode: document.getElementById('perfResultCode').value,
        userId: document.getElementById('perfUserId').value,
        pageNo: page,
        pageSize: PAGE_SIZE
    };
    const res = await fetch(`${PERF_API}?${buildQuery(params)}`, { headers: apiHeaders() });
    const json = await res.json();
    if (!res.ok || json.error?.resultCode === 'FAIL') {
        showStatus(json.error?.errorDetail ?? json.error?.errorMessage ?? '성능 로그 조회 실패', 'error');
        return;
    }
    const rows = json.body?.response ?? [];
    const total = json.control?.totalCount ?? 0;
    perfListSummary.textContent = `전체 ${total}건`;
    if (!rows.length) {
        perfTableBody.innerHTML = '<tr><td colspan="7" class="empty-row">조회 결과가 없습니다.</td></tr>';
        renderPagination(perfPagination, page, total, fetchPerfList);
        return;
    }
    perfTableBody.innerHTML = rows.map(row => `
        <tr data-id="${row.logId}">
            <td>${row.logId}</td>
            <td>${row.guid ?? '-'}</td>
            <td>${row.serviceId ?? '-'}</td>
            <td>${row.resultCode ?? '-'}</td>
            <td>${row.totalTime ?? '-'}</td>
            <td>${formatDateTime(row.logTime)}</td>
            <td class="col-actions">
                <button type="button" class="link-btn" data-detail="${row.logId}">상세</button>
                <button type="button" class="link-btn danger-text" data-delete="${row.logId}">삭제</button>
            </td>
        </tr>
    `).join('');
    renderPagination(perfPagination, page, total, fetchPerfList);
}

async function fetchPerfDetail(logId) {
    const res = await fetch(`${PERF_API}/${logId}`, { headers: apiHeaders() });
    const json = await res.json();
    renderPerfDetail(json.body?.response);
}

async function fetchAuditList(page = 1) {
    auditPage = page;
    const params = {
        guid: document.getElementById('auditGuid').value,
        userId: document.getElementById('auditUserId').value,
        actionType: document.getElementById('auditActionType').value,
        pageNo: page,
        pageSize: PAGE_SIZE
    };
    const res = await fetch(`${AUDIT_API}?${buildQuery(params)}`, { headers: apiHeaders() });
    const json = await res.json();
    if (!res.ok || json.error?.resultCode === 'FAIL') {
        showStatus(json.error?.errorDetail ?? json.error?.errorMessage ?? '감사 로그 조회 실패', 'error');
        return;
    }
    const rows = json.body?.response ?? [];
    const total = json.control?.totalCount ?? 0;
    auditListSummary.textContent = `전체 ${total}건`;
    if (!rows.length) {
        auditTableBody.innerHTML = '<tr><td colspan="7" class="empty-row">조회 결과가 없습니다.</td></tr>';
        renderPagination(auditPagination, page, total, fetchAuditList);
        return;
    }
    auditTableBody.innerHTML = rows.map(row => `
        <tr>
            <td>${row.auditId}</td>
            <td>${row.guid ?? '-'}</td>
            <td>${row.userId ?? '-'}</td>
            <td>${row.actionType ?? '-'}</td>
            <td>${row.functionId ?? '-'}</td>
            <td>${row.resultCode ?? '-'}</td>
            <td>${formatDateTime(row.auditTime)}</td>
        </tr>
    `).join('');
    renderPagination(auditPagination, page, total, fetchAuditList);
}

async function runProcess(payload) {
    const res = await fetch(PROCESS_API, {
        method: 'POST',
        headers: apiHeaders(),
        body: JSON.stringify(payload)
    });
    const json = await res.json();
    processResult.textContent = JSON.stringify(json, null, 2);
    if (json.error?.resultCode === 'SUCCESS') {
        showStatus('파이프라인 실행 완료', 'success');
        fetchPerfList(1);
        fetchAuditList(1);
    } else {
        showStatus(json.error?.errorMessage ?? '실행 실패', 'error');
    }
}

function switchTab(tab) {
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.toggle('active', btn.dataset.tab === tab);
    });
    const perfVisible = tab === 'perf';
    const auditVisible = tab === 'audit';
    const processVisible = tab === 'process';
    ['panel-perf', 'detail-perf', 'list-perf'].forEach(id => {
        document.getElementById(id).classList.toggle('hidden', !perfVisible);
    });
    ['panel-audit', 'list-audit'].forEach(id => {
        document.getElementById(id).classList.toggle('hidden', !auditVisible);
    });
    document.getElementById('panel-process').classList.toggle('hidden', !processVisible);
}

document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', () => switchTab(btn.dataset.tab));
});

perfSearchForm.addEventListener('submit', e => {
    e.preventDefault();
    hideStatus();
    fetchPerfList(1);
});

auditSearchForm.addEventListener('submit', e => {
    e.preventDefault();
    hideStatus();
    fetchAuditList(1);
});

processForm.addEventListener('submit', e => {
    e.preventDefault();
    hideStatus();
    const payload = {
        transactionId: document.getElementById('procTxId').value,
        serviceId: document.getElementById('procServiceId').value,
        idempotencyKey: document.getElementById('procIdemKey').value || null,
        actionType: document.getElementById('procActionType').value,
        menuId: document.getElementById('procMenuId').value,
        functionId: document.getElementById('procFuncId').value,
        requestUri: document.getElementById('procUri').value,
        httpMethod: document.getElementById('procMethod').value
    };
    runProcess(payload);
});

document.getElementById('perfResetBtn').addEventListener('click', () => {
    perfSearchForm.reset();
    perfTableBody.innerHTML = '<tr><td colspan="7" class="empty-row">조회 버튼을 눌러 주세요.</td></tr>';
    perfListSummary.textContent = '전체 0건';
    renderPerfDetail(null);
});

document.getElementById('auditResetBtn').addEventListener('click', () => {
    auditSearchForm.reset();
    auditTableBody.innerHTML = '<tr><td colspan="7" class="empty-row">조회 버튼을 눌러 주세요.</td></tr>';
    auditListSummary.textContent = '전체 0건';
});

perfTableBody.addEventListener('click', e => {
    const detailBtn = e.target.closest('[data-detail]');
    const deleteBtn = e.target.closest('[data-delete]');
    if (detailBtn) {
        fetchPerfDetail(detailBtn.dataset.detail);
        return;
    }
    if (deleteBtn) {
        pendingDeleteId = deleteBtn.dataset.delete;
        deleteModalText.textContent = `logId=${pendingDeleteId} 성능 로그를 삭제하시겠습니까?`;
        deleteModal.classList.remove('hidden');
    }
});

confirmDeleteBtn.addEventListener('click', async () => {
    if (!pendingDeleteId) return;
    const res = await fetch(`${PERF_API}/${pendingDeleteId}`, {
        method: 'DELETE',
        headers: apiHeaders()
    });
    deleteModal.classList.add('hidden');
    if (res.ok) {
        showStatus('삭제 완료', 'success');
        fetchPerfList(perfPage);
        renderPerfDetail(null);
    } else {
        showStatus('삭제 실패', 'error');
    }
    pendingDeleteId = null;
});

deleteModal.querySelectorAll('[data-close]').forEach(el => {
    el.addEventListener('click', () => deleteModal.classList.add('hidden'));
});

fetchPerfList(1);
fetchAuditList(1);
