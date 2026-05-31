const API_BASE = '/api/v1/trace-dump';
const analyzeForm = document.getElementById('analyzeForm');
const statusBar = document.getElementById('statusBar');
const summaryBox = document.getElementById('summaryBox');
const findingList = document.getElementById('findingList');
const reportBox = document.getElementById('reportBox');

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

function renderFindings(findings) {
    if (!findings || findings.length === 0) {
        findingList.innerHTML = '<p class="tx-detail-placeholder">Finding 없음</p>';
        return;
    }
    findingList.innerHTML = findings.map(f => `
        <section class="envelope-card ${f.severity === 'HIGH' ? 'error fail' : 'header'}">
            <h3>[${escapeHtml(f.severity)}] ${escapeHtml(f.title)}</h3>
            <dl>
                <dt>분류</dt><dd>${escapeHtml(f.category)}</dd>
                <dt>가이드</dt><dd>§${escapeHtml(f.guideSection)}</dd>
                <dt>설명</dt><dd>${escapeHtml(f.description)}</dd>
                <dt>근거</dt><dd>${escapeHtml(f.evidence)}</dd>
            </dl>
        </section>
    `).join('');
}

function renderResult(data) {
    const body = data?.body?.response;
    if (!body) return;
    const summary = body.summary || {};
    summaryBox.innerHTML = `
        <strong>OOM 분류:</strong> ${escapeHtml(body.oomCategory)}<br/>
        <strong>증거 경로:</strong> <code>${escapeHtml(body.evidencePath)}</code><br/>
        <strong>파일 수:</strong> ${summary.evidenceFileCount ?? 0} ·
        <strong>Finding:</strong> ${summary.findingCount ?? 0}
        (HIGH ${summary.highSeverityCount ?? 0})
    `;
    renderFindings(body.findings);
    reportBox.textContent = body.markdownReport || '';
}

analyzeForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const path = document.getElementById('evidencePath').value.trim();
    const zipInput = document.getElementById('evidenceZip');
    const formData = new FormData();
    if (path) formData.append('evidencePath', path);
    if (zipInput.files.length > 0) formData.append('evidenceZip', zipInput.files[0]);

    try {
        showStatus('분석 중...', 'info');
        const res = await fetch(`${API_BASE}/analyze`, {
            method: 'POST',
            headers: { 'X-GUID': crypto.randomUUID(), 'X-USER-ID': 'ARCHITECT' },
            body: formData
        });
        const data = await res.json();
        if (!isSuccess(data)) {
            showStatus(errorMessage(data), 'error');
            return;
        }
        renderResult(data);
        showStatus('분석이 완료되었습니다.', 'success');
    } catch (err) {
        showStatus('분석 실패: ' + err.message, 'error');
    }
});

document.getElementById('loadSampleBtn').addEventListener('click', async () => {
    try {
        const res = await fetch(`${API_BASE}/storage-location`, {
            headers: { 'X-GUID': crypto.randomUUID(), 'X-USER-ID': 'ARCHITECT' }
        });
        const data = await res.json();
        const base = data?.body?.response?.storagePath;
        if (base) {
            document.getElementById('evidencePath').value = base + '/sample';
            return;
        }
    } catch (e) { /* fallback below */ }
    const input = document.getElementById('evidencePath');
    const val = input.value.trim().replace(/\\/g, '/').replace(/\/$/, '');
    input.value = val.includes('sample') ? val : val + '/sample';
});
