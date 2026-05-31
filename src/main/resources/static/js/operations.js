const API_INFO = '/api/v1/operations/info';

const ENDPOINTS = {
    health: { path: '/actuator/health', label: 'Health Check' },
    info: { path: '/actuator/info', label: 'Application Info' },
    metrics: { path: '/actuator/metrics', label: 'Metrics' },
    prometheus: { path: '/actuator/prometheus', label: 'Prometheus', text: true }
};

const statusBar = document.getElementById('statusBar');
const checkResult = document.getElementById('checkResult');
const checkMeta = document.getElementById('checkMeta');
const healthBadge = document.getElementById('healthStatusBadge');

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

function renderDl(container, rows) {
    container.innerHTML = rows.map(([dt, dd]) => `
        <dt>${escapeHtml(dt)}</dt>
        <dd>${dd}</dd>
    `).join('');
}

function codeBlock(value) {
    return `<code>${escapeHtml(String(value))}</code>`;
}

function renderStorageSections(info) {
    const el = document.getElementById('opsStorageSections');
    const msg = info.messageLog || {};
    const file = info.fileStorage || {};
    const dump = info.traceDump || {};
    const oom = info.oomInspector || {};

    el.innerHTML = `
        <article class="ops-block">
            <h3>입·출력 전문 (Message Log)</h3>
            <dl class="ops-info-dl">
                <dt>저장 경로</dt><dd>${codeBlock(msg.storagePath)}</dd>
                <dt>활성</dt><dd>${codeBlock(msg.enabled)}</dd>
                <dt>요청 패턴</dt><dd>${codeBlock(msg.pathPattern)}</dd>
                <dt>응답 패턴</dt><dd>${codeBlock(msg.pathPatternResponse)}</dd>
            </dl>
        </article>
        <article class="ops-block">
            <h3>첨부 파일</h3>
            <dl class="ops-info-dl">
                <dt>저장 경로</dt><dd>${codeBlock(file.storagePath)} ${file.directoryExists ? '' : '<span class="ops-warn">(디렉터리 없음)</span>'}</dd>
                <dt>경로 패턴</dt><dd>${codeBlock(file.pathPattern)}</dd>
                <dt>최대 크기</dt><dd>${codeBlock(file.maxFileSizeBytes)} bytes</dd>
            </dl>
        </article>
        <article class="ops-block">
            <h3>JVM 덤프 증거</h3>
            <dl class="ops-info-dl">
                <dt>증거 경로</dt><dd>${codeBlock(dump.evidencePath)}</dd>
                <dt>화면</dt><dd>${codeBlock(dump.uiPath)}</dd>
            </dl>
        </article>
        <article class="ops-block">
            <h3>OOM 점검 기준</h3>
            <dl class="ops-info-dl">
                <dt>Java 소스</dt><dd>${codeBlock(oom.defaultSourceRoot)}</dd>
                <dt>Mapper</dt><dd>${codeBlock(oom.defaultMapperRoot)}</dd>
                <dt>설정</dt><dd>${codeBlock(oom.defaultConfigPath)}</dd>
            </dl>
        </article>
    `;
}

function renderLinkSections(info) {
    const el = document.getElementById('opsLinkSections');
    const integ = info.integrations || {};
    const endpoints = info.actuatorEndpoints || [];
    const consoles = info.consoleLinks || [];

    const endpointRows = endpoints.map(e => `
        <tr>
            <td>${escapeHtml(e.name)}</td>
            <td><code>${escapeHtml(e.path)}</code></td>
            <td>${escapeHtml(e.description)}</td>
            <td><button type="button" class="btn-link ops-inline-check" data-path="${escapeHtml(e.path)}">실행</button></td>
        </tr>
    `).join('');

    const consoleRows = consoles.map(c => `
        <tr>
            <td>${escapeHtml(c.name)}</td>
            <td><a href="${escapeHtml(c.path)}" target="_blank" rel="noopener"><code>${escapeHtml(c.path)}</code></a></td>
            <td>${escapeHtml(c.note)}</td>
        </tr>
    `).join('');

    el.innerHTML = `
        <article class="ops-block">
            <h3>외부 연계</h3>
            <dl class="ops-info-dl">
                <dt>AP ID</dt><dd>${codeBlock(info.apId)}</dd>
                <dt>CruzAPIM</dt><dd>${codeBlock(integ.cruzApimBaseUrl)}</dd>
                <dt>로그 경로</dt><dd>${codeBlock(info.loggingPath)}</dd>
            </dl>
        </article>
        <article class="ops-block">
            <h3>Actuator 엔드포인트</h3>
            <div class="table-wrap">
                <table class="data-table ops-table">
                    <thead><tr><th>이름</th><th>경로</th><th>설명</th><th></th></tr></thead>
                    <tbody>${endpointRows}</tbody>
                </table>
            </div>
        </article>
        <article class="ops-block">
            <h3>관리 콘솔·API</h3>
            <div class="table-wrap">
                <table class="data-table ops-table">
                    <thead><tr><th>이름</th><th>경로</th><th>비고</th></tr></thead>
                    <tbody>${consoleRows}</tbody>
                </table>
            </div>
        </article>
    `;

    el.querySelectorAll('.ops-inline-check').forEach(btn => {
        btn.addEventListener('click', () => {
            const path = btn.getAttribute('data-path');
            const key = Object.keys(ENDPOINTS).find(k => ENDPOINTS[k].path === path);
            if (key) {
                runCheck(key);
            } else {
                runCheckByPath(path, path);
            }
        });
    });
}

async function loadOperationsInfo() {
    try {
        const res = await fetch(API_INFO, {
            headers: { 'X-GUID': crypto.randomUUID(), 'X-USER-ID': 'ARCHITECT' }
        });
        const data = await res.json();
        if (!isSuccess(data)) {
            showStatus(errMsg(data), 'error');
            document.getElementById('opsSummary').textContent = '운영 정보를 불러오지 못했습니다.';
            return;
        }
        const info = data.body?.response;
        const profiles = (info.activeProfiles || []).join(', ') || 'default';
        document.getElementById('opsSummary').innerHTML = `
            <strong>${escapeHtml(info.applicationName)}</strong> · 프로파일 <code>${escapeHtml(profiles)}</code>
            · 포트 <code>${info.serverPort}</code>
        `;
        renderDl(document.getElementById('opsOverview'), [
            ['애플리케이션', info.applicationName],
            ['활성 프로파일', profiles],
            ['AP ID', info.apId],
            ['서버 포트', String(info.serverPort)],
            ['로그 파일 경로', info.loggingPath]
        ].map(([k, v]) => [k, codeBlock(v)]));
        renderStorageSections(info);
        renderLinkSections(info);
    } catch (err) {
        showStatus('운영 정보 로드 실패: ' + err.message, 'error');
    }
}

function setHealthBadge(status, httpStatus) {
    healthBadge.classList.remove('hidden', 'ok', 'warn', 'fail');
    const upper = (status || '').toUpperCase();
    if (upper === 'UP') {
        healthBadge.textContent = 'UP';
        healthBadge.classList.add('ok');
    } else if (upper === 'DOWN' || upper === 'OUT_OF_SERVICE') {
        healthBadge.textContent = upper;
        healthBadge.classList.add('fail');
    } else if (httpStatus >= 400) {
        healthBadge.textContent = 'HTTP ' + httpStatus;
        healthBadge.classList.add('fail');
    } else {
        healthBadge.textContent = status || 'UNKNOWN';
        healthBadge.classList.add('warn');
    }
}

function formatResult(body, isText) {
    if (isText) {
        return body;
    }
    try {
        const parsed = typeof body === 'string' ? JSON.parse(body) : body;
        return JSON.stringify(parsed, null, 2);
    } catch (e) {
        return body;
    }
}

function extractHealthStatus(body, isText) {
    if (isText) return null;
    try {
        const obj = typeof body === 'string' ? JSON.parse(body) : body;
        return obj.status || null;
    } catch (e) {
        return null;
    }
}

async function runCheckByPath(path, label, isText = false) {
    const started = Date.now();
    checkMeta.textContent = `${label} 호출 중… (${path})`;
    checkResult.classList.remove('hidden');
    checkResult.textContent = '요청 중…';

    try {
        const res = await fetch(path, { headers: { Accept: isText ? 'text/plain' : 'application/json' } });
        const raw = await res.text();
        const elapsed = Date.now() - started;
        const formatted = formatResult(raw, isText);
        checkResult.textContent = formatted;
        checkMeta.textContent = `${label} · HTTP ${res.status} · ${elapsed}ms · ${path}`;

        if (path.includes('/health')) {
            setHealthBadge(extractHealthStatus(raw, isText), res.status);
        }
        showStatus(res.ok ? `${label} 조회 완료` : `${label} HTTP ${res.status}`, res.ok ? 'success' : 'error');
        return res.ok;
    } catch (err) {
        checkResult.textContent = '요청 실패: ' + err.message;
        checkMeta.textContent = `${label} · 오류`;
        setHealthBadge('ERROR', 0);
        showStatus(`${label} 실패: ` + err.message, 'error');
        return false;
    }
}

async function runCheck(key) {
    const ep = ENDPOINTS[key];
    if (!ep) return false;
    return runCheckByPath(ep.path, ep.label, !!ep.text);
}

document.querySelectorAll('.ops-check-btn').forEach(btn => {
    btn.addEventListener('click', () => runCheck(btn.getAttribute('data-endpoint')));
});

document.getElementById('checkAllBtn')?.addEventListener('click', async () => {
    showStatus('전체 Health Check 실행 중…', 'info');
    const keys = ['health', 'info', 'metrics'];
    for (const key of keys) {
        await runCheck(key);
    }
    showStatus('전체 실행 완료 (Prometheus 제외)', 'success');
});

loadOperationsInfo();
