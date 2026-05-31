const API = '/api/oom-inspector';
const scanForm = document.getElementById('scanForm');
const statusBar = document.getElementById('statusBar');
const summaryBox = document.getElementById('summaryBox');
const reportPanel = document.getElementById('reportPanel');

let lastScanContext = null;

function showStatus(msg, type = 'info') {
    statusBar.textContent = msg;
    statusBar.className = `status-bar ${type}`;
    statusBar.classList.remove('hidden');
}

function escapeHtml(t) {
    const d = document.createElement('div');
    d.textContent = t ?? '';
    return d.innerHTML;
}

function isSuccess(data) {
    return data?.error?.resultCode === 'SUCCESS';
}

function errMsg(data) {
    return data?.error?.resultMessage || data?.error?.detailMessage || '오류';
}

function fileLinkHtml(filePath, lineNumber) {
    const line = lineNumber > 0 ? lineNumber : 0;
    const label = line > 0 ? `${filePath}:${line}` : filePath;
    return `<button type="button" class="finding-file-link" data-path="${escapeHtml(filePath)}" data-line="${line}" title="클릭하여 파일 내용 보기"><code>${escapeHtml(filePath)}</code>${line > 0 ? '<span class="finding-line">:' + line + '</span>' : ''}</button>`;
}

function openFileModal() {
    const modal = document.getElementById('filePreviewModal');
    modal.classList.remove('hidden');
    modal.setAttribute('aria-hidden', 'false');
}

function closeFileModal() {
    const modal = document.getElementById('filePreviewModal');
    modal.classList.add('hidden');
    modal.setAttribute('aria-hidden', 'true');
}

function setFilePreviewState({ loading, error, body }) {
    document.getElementById('filePreviewLoading').classList.toggle('hidden', !loading);
    const errEl = document.getElementById('filePreviewError');
    errEl.classList.toggle('hidden', !error);
    errEl.textContent = error || '';
    const bodyEl = document.getElementById('filePreviewBody');
    bodyEl.classList.toggle('hidden', !body);
    if (body) {
        bodyEl.innerHTML = body;
    }
}

function renderFileLines(content, highlightLine) {
    const lines = (content || '').split('\n');
    return lines.map((text, i) => {
        const n = i + 1;
        const cls = n === highlightLine ? 'file-line file-line--highlight' : 'file-line';
        return `<div class="${cls}" id="file-line-${n}"><span class="file-ln">${n}</span><span class="file-text">${escapeHtml(text)}</span></div>`;
    }).join('');
}

async function showFileContent(relativePath, lineNumber) {
    if (!lastScanContext) {
        showStatus('스캔 결과가 없습니다. 먼저 스캔을 실행하세요.', 'error');
        return;
    }

    openFileModal();
    document.getElementById('filePreviewTitle').textContent = relativePath;
    document.getElementById('filePreviewMeta').textContent = '불러오는 중…';
    setFilePreviewState({ loading: true, error: null, body: null });

    const params = new URLSearchParams({
        relativePath,
        sourceRoot: lastScanContext.sourceRoot || '',
        mapperRoot: lastScanContext.mapperRoot || '',
        configPath: lastScanContext.configPath || '',
        line: String(lineNumber > 0 ? lineNumber : 0)
    });

    try {
        const res = await fetch(`${API}/files/content?${params}`, {
            headers: { 'X-GUID': crypto.randomUUID(), 'X-USER-ID': 'ARCHITECT' }
        });
        const data = await res.json();
        if (!isSuccess(data)) {
            setFilePreviewState({ loading: false, error: errMsg(data), body: null });
            document.getElementById('filePreviewMeta').textContent = '';
            return;
        }

        const view = data.body?.response;
        const highlight = view.highlightLine > 0 ? view.highlightLine : lineNumber;
        let meta = view.resolvedPath || '';
        if (highlight > 0) {
            meta += ` · ${highlight}행 강조`;
        }
        if (view.truncated) {
            meta += ' · 512KB 초과로 일부만 표시';
        }
        document.getElementById('filePreviewMeta').textContent = meta;
        setFilePreviewState({
            loading: false,
            error: null,
            body: renderFileLines(view.content, highlight)
        });

        if (highlight > 0) {
            requestAnimationFrame(() => {
                const row = document.getElementById(`file-line-${highlight}`);
                row?.scrollIntoView({ block: 'center', behavior: 'smooth' });
            });
        }
    } catch (err) {
        setFilePreviewState({ loading: false, error: '파일 로드 실패: ' + err.message, body: null });
        document.getElementById('filePreviewMeta').textContent = '';
    }
}

function renderReport(report) {
    if (!report) return;
    reportPanel.classList.remove('hidden');

    lastScanContext = {
        sourceRoot: report.sourceRoot || '',
        mapperRoot: report.mapperRoot || '',
        configPath: report.configPath || ''
    };

    const gateBadge = document.getElementById('gateBadge');
    gateBadge.textContent = report.gatePassed ? 'GATE PASS' : 'GATE FAIL';
    gateBadge.className = 'dump-report__oom-badge' + (report.gatePassed ? ' ok' : '');

    document.getElementById('reportMeta').innerHTML = `
        Scan ID: <code>${escapeHtml(report.scanId)}</code><br/>
        경로: <code>${escapeHtml(report.sourceRoot)}</code><br/>
        파일 ${report.filesScanned}개 · Finding ${report.findings?.length ?? 0}건
    `;

    const sev = report.findingsBySeverity || {};
    document.getElementById('fourStepBox').innerHTML = `
        <div class="dump-step"><span class="dump-step__no">①</span><p><strong>읽기</strong> — Java/Mapper/설정 ${report.filesScanned}개 파일</p></div>
        <div class="dump-step"><span class="dump-step__no">②</span><p><strong>증거</strong> — 정적 패턴 매칭 (세션·SQL·파일·캐시·Thread)</p></div>
        <div class="dump-step"><span class="dump-step__no">③</span><p><strong>판정</strong> — CRITICAL ${sev.CRITICAL ?? 0}, HIGH ${sev.HIGH ?? 0}, MEDIUM ${sev.MEDIUM ?? 0}</p></div>
        <div class="dump-step"><span class="dump-step__no">④</span><p><strong>조치</strong> — ${escapeHtml(report.gateMessage)}</p></div>
    `;

    const tbody = document.querySelector('#findingTable tbody');
    const findings = report.findings || [];
    tbody.innerHTML = findings.length === 0
        ? '<tr><td colspan="6">Finding 없음 — 양호</td></tr>'
        : findings.map(f => `
            <tr class="finding-row finding-row--${(f.severity || 'info').toLowerCase()}">
                <td><code class="finding-rule">${escapeHtml(f.ruleId)}</code></td>
                <td><span class="pill ${f.severity === 'CRITICAL' || f.severity === 'HIGH' ? 'no' : ''}">${escapeHtml(f.severity)}</span></td>
                <td>${escapeHtml(f.category)}</td>
                <td class="finding-cell finding-cell--title">
                    <strong>${escapeHtml(f.title)}</strong>
                    <p class="finding-desc">${escapeHtml(f.description)}</p>
                </td>
                <td class="finding-cell finding-cell--file">${fileLinkHtml(f.filePath, f.lineNumber)}</td>
                <td class="finding-cell finding-cell--action">${escapeHtml(f.recommendation)}</td>
            </tr>
        `).join('');

    document.getElementById('reportMarkdown').textContent = report.summaryMarkdown || '';
    reportPanel.scrollIntoView({ behavior: 'smooth' });
}

function renderSummary(report) {
    const sev = report.findingsBySeverity || {};
    summaryBox.innerHTML = `
        <strong>${escapeHtml(report.gateMessage)}</strong><br/>
        CRITICAL ${sev.CRITICAL ?? 0} · HIGH ${sev.HIGH ?? 0} · MEDIUM ${sev.MEDIUM ?? 0} · LOW ${sev.LOW ?? 0}
    `;
}

async function runScan(url, params) {
    showStatus('스캔 중...', 'info');
    const qs = params ? '?' + new URLSearchParams(params).toString() : '';
    const res = await fetch(url + qs, {
        method: 'POST',
        headers: { 'X-GUID': crypto.randomUUID(), 'X-USER-ID': 'ARCHITECT' }
    });
    const data = await res.json();
    if (!isSuccess(data)) {
        showStatus(errMsg(data), 'error');
        return;
    }
    const report = data.body?.response;
    renderSummary(report);
    renderReport(report);
    showStatus('스캔 완료', 'success');
}

function formParams() {
    return {
        projectName: document.getElementById('projectName').value.trim(),
        sourceRoot: document.getElementById('sourceRoot').value.trim(),
        mapperRoot: document.getElementById('mapperRoot').value.trim(),
        configPath: document.getElementById('configPath').value.trim(),
        failOnCritical: 'true'
    };
}

document.querySelector('#findingTable tbody')?.addEventListener('click', (e) => {
    const btn = e.target.closest('.finding-file-link');
    if (!btn) return;
    const path = btn.getAttribute('data-path');
    const line = parseInt(btn.getAttribute('data-line') || '0', 10);
    if (path) {
        showFileContent(path, line);
    }
});

document.getElementById('filePreviewModal')?.addEventListener('click', (e) => {
    if (e.target.closest('[data-close="true"]')) {
        closeFileModal();
    }
});

document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        closeFileModal();
    }
});

scanForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        await runScan(`${API}/scans`, formParams());
    } catch (err) {
        showStatus('실패: ' + err.message, 'error');
    }
});

document.getElementById('quickScanBtn').addEventListener('click', async () => {
    try {
        await runScan(`${API}/scans/quick`);
    } catch (err) {
        showStatus('실패: ' + err.message, 'error');
    }
});

document.getElementById('gateBtn').addEventListener('click', async () => {
    try {
        showStatus('Gate 평가 중...', 'info');
        const params = { ...formParams(), failOn: 'CRITICAL' };
        const res = await fetch(`${API}/gate?` + new URLSearchParams(params), {
            method: 'POST',
            headers: { 'X-GUID': crypto.randomUUID(), 'X-USER-ID': 'ARCHITECT' }
        });
        const data = await res.json();
        if (!isSuccess(data)) {
            showStatus(errMsg(data), 'error');
            return;
        }
        const g = data.body?.response;
        showStatus(g.passed ? 'Gate 통과' : 'Gate 실패 — ' + g.gateMessage, g.passed ? 'success' : 'error');
    } catch (err) {
        showStatus('Gate 실패: ' + err.message, 'error');
    }
});
