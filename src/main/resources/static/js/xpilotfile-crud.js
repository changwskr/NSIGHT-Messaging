const API_BASE = '/api/xpilotfile/files';
const uploadForm = document.getElementById('uploadForm');
const resultBox = document.getElementById('resultBox');
const tableBody = document.getElementById('fileTableBody');
const listSummary = document.getElementById('listSummary');
const statusBar = document.getElementById('statusBar');
const detailMeta = document.getElementById('detailMeta');
const filterName = document.getElementById('filterName');
const filterCategory = document.getElementById('filterCategory');
const filterUseYn = document.getElementById('filterUseYn');
const deleteModal = document.getElementById('deleteModal');
const deleteModalText = document.getElementById('deleteModalText');
const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');

let pendingDeleteId = null;
let selectedFileId = null;

function guid() {
    const now = new Date();
    const ymd = now.toISOString().replace(/[-:TZ.]/g, '').slice(0, 14);
    return `${ymd}-FILE-${crypto.randomUUID().slice(0, 8).toUpperCase()}`;
}

function apiHeaders(includeJson) {
    const headers = {
        'X-GUID': guid(),
        'X-TRACE-ID': crypto.randomUUID(),
        'X-USER-ID': 'ARCHITECT',
        'X-BRANCH-ID': '360001',
        'X-CENTER-ID': 'CENTER_1',
        'X-TERMINAL-ID': 'TERM-360001-001'
    };
    if (includeJson) {
        headers['Content-Type'] = 'application/json';
    }
    return headers;
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

function formatDateTime(value) {
    if (!value) return '-';
    return String(value).replace('T', ' ').slice(0, 19);
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text ?? '';
    return div.innerHTML;
}

function escapeAttr(text) {
    return escapeHtml(text).replace(/"/g, '&quot;');
}

function printDetail(row, apiData) {
    document.getElementById('metaFileId').textContent = row.fileId;
    document.getElementById('metaStorageFull').textContent = row.storageFullPath || row.storagePath || '-';
    document.getElementById('metaStorageRelative').textContent = row.storageRelativePath || row.storagePath || '-';
    document.getElementById('metaFileSize').textContent = row.fileSizeLabel;
    document.getElementById('metaCreatedBy').textContent = row.createdBy || '-';
    document.getElementById('metaCreatedAt').textContent = formatDateTime(row.createdAt);
    document.getElementById('metaUseYn').textContent = row.useYn;
    const link = document.getElementById('metaDownloadLink');
    link.href = row.downloadUrl;
    link.textContent = row.originalName;
    detailMeta.classList.remove('hidden');

    resultBox.innerHTML = `
        <div class="detail-summary">
            <h3>${escapeHtml(row.originalName)}</h3>
            <dl>
                <dt>파일 ID</dt><dd>${row.fileId}</dd>
                <dt>업무 구분</dt><dd>${escapeHtml(row.bizCategory)}</dd>
                <dt>Content-Type</dt><dd>${escapeHtml(row.contentType)}</dd>
                <dt>크기</dt><dd>${row.fileSizeLabel}</dd>
                <dt>설명</dt><dd>${escapeHtml(row.description || '-')}</dd>
                <dt>저장 루트</dt><dd>${escapeHtml(row.storageBasePath || '-')}</dd>
                <dt>상대 경로</dt><dd>${escapeHtml(row.storageRelativePath || row.storagePath || '-')}</dd>
                <dt>전체 경로</dt><dd>${escapeHtml(row.storageFullPath || '-')}</dd>
            </dl>
        </div>
        <pre class="result-json">${escapeHtml(JSON.stringify(apiData, null, 2))}</pre>
    `;
}

async function apiJson(url, options = {}) {
    const res = await fetch(url, { ...options, headers: apiHeaders(!!options.body) });
    return res.json();
}

async function loadFiles() {
    const params = new URLSearchParams();
    if (filterName.value.trim()) params.append('originalName', filterName.value.trim());
    if (filterCategory.value) params.append('bizCategory', filterCategory.value);
    if (filterUseYn.value) params.append('useYn', filterUseYn.value);

    try {
        const data = await apiJson(`${API_BASE}?${params.toString()}`);
        if (!isSuccess(data)) {
            showStatus(errorMessage(data), 'error');
            return;
        }
        const rows = data?.body?.response || [];
        const total = data?.control?.totalCount ?? rows.length;
        listSummary.textContent = `전체 ${total}건`;

        if (rows.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="9" class="empty-row">조회 결과가 없습니다.</td></tr>';
            return;
        }

        tableBody.innerHTML = rows.map(row => `
            <tr data-id="${row.fileId}" class="${selectedFileId == row.fileId ? 'selected' : ''}">
                <td>${row.fileId}</td>
                <td><strong>${escapeHtml(row.originalName)}</strong></td>
                <td>${row.bizCategory}</td>
                <td>${row.fileSizeLabel}</td>
                <td>${escapeHtml(row.contentType)}</td>
                <td class="col-path" title="${escapeAttr(row.storageFullPath || '')}">${escapeHtml(row.storageFullPath || row.storagePath || '-')}</td>
                <td>${row.useYn}</td>
                <td>${formatDateTime(row.createdAt)}</td>
                <td class="col-actions">
                    <button type="button" class="btn-link" data-action="detail" data-id="${row.fileId}">상세</button>
                    <a class="btn-link" href="${row.downloadUrl}" data-action="download">다운로드</a>
                    <button type="button" class="btn-link" data-action="toggle" data-id="${row.fileId}" data-use="${row.useYn === 'Y' ? 'N' : 'Y'}">${row.useYn === 'Y' ? '비활성' : '활성'}</button>
                    <button type="button" class="btn-link danger-text" data-action="delete" data-id="${row.fileId}" data-name="${escapeAttr(row.originalName)}">삭제</button>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        showStatus('목록 조회 실패: ' + e.message, 'error');
    }
}

async function loadDetail(fileId) {
    const data = await apiJson(`${API_BASE}/${fileId}`);
    if (!isSuccess(data) || !data?.body?.response) {
        resultBox.textContent = JSON.stringify(data, null, 2);
        showStatus(errorMessage(data), 'error');
        return null;
    }
    const row = data.body.response;
    selectedFileId = row.fileId;
    printDetail(row, data);
    showStatus(`상세 조회: ${row.originalName}`, 'info');
    await loadFiles();
    return row;
}

uploadForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    const fileInput = document.getElementById('fileInput');
    if (!fileInput.files.length) {
        showStatus('파일을 선택해 주십시오.', 'error');
        return;
    }

    const formData = new FormData();
    formData.append('file', fileInput.files[0]);
    formData.append('bizCategory', uploadForm.bizCategory.value);
    if (uploadForm.description.value) {
        formData.append('description', uploadForm.description.value);
    }

    try {
        const res = await fetch(API_BASE, {
            method: 'POST',
            headers: apiHeaders(false),
            body: formData
        });
        const data = await res.json();
        if (!isSuccess(data)) {
            resultBox.textContent = JSON.stringify(data, null, 2);
            showStatus(errorMessage(data), 'error');
            return;
        }
        showStatus('파일이 업로드되었습니다.', 'success');
        uploadForm.reset();
        if (data?.body?.response) {
            printDetail(data.body.response, data);
            selectedFileId = data.body.response.fileId;
        }
        await loadFiles();
    } catch (e) {
        showStatus('업로드 실패: ' + e.message, 'error');
    }
});

tableBody.addEventListener('click', async (event) => {
    const btn = event.target.closest('button[data-action]');
    if (!btn) return;

    const fileId = btn.dataset.id;
    const action = btn.dataset.action;

    if (action === 'detail') {
        await loadDetail(fileId);
        return;
    }

    if (action === 'toggle') {
        const useYn = btn.dataset.use;
        const data = await apiJson(`${API_BASE}/${fileId}/use-yn?useYn=${useYn}`, { method: 'PUT' });
        if (!isSuccess(data)) {
            showStatus(errorMessage(data), 'error');
            return;
        }
        showStatus(useYn === 'Y' ? '파일이 활성화되었습니다.' : '파일이 비활성화되었습니다.', 'success');
        if (selectedFileId == fileId && data?.body?.response) {
            printDetail(data.body.response, data);
        }
        await loadFiles();
        return;
    }

    if (action === 'delete') {
        pendingDeleteId = fileId;
        deleteModalText.textContent = `파일 [${btn.dataset.name}] (ID: ${fileId})를 삭제하시겠습니까?`;
        deleteModal.classList.remove('hidden');
    }
});

confirmDeleteBtn.addEventListener('click', async () => {
    if (!pendingDeleteId) return;
    const data = await apiJson(`${API_BASE}/${pendingDeleteId}`, { method: 'DELETE' });
    deleteModal.classList.add('hidden');
    if (!isSuccess(data)) {
        showStatus(errorMessage(data), 'error');
        return;
    }
    showStatus('파일이 삭제되었습니다.', 'success');
    if (selectedFileId == pendingDeleteId) {
        selectedFileId = null;
        detailMeta.classList.add('hidden');
        resultBox.textContent = '파일이 삭제되었습니다.';
    }
    pendingDeleteId = null;
    await loadFiles();
});

deleteModal.querySelectorAll('[data-close="true"]').forEach(el => {
    el.addEventListener('click', () => deleteModal.classList.add('hidden'));
});

document.getElementById('resetUploadBtn').addEventListener('click', () => uploadForm.reset());
document.getElementById('reloadBtn').addEventListener('click', loadFiles);
filterCategory.addEventListener('change', loadFiles);
filterUseYn.addEventListener('change', loadFiles);
filterName.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') loadFiles();
});

loadFiles();
