/**
 * Xpilot — traceenvironment 구조 전환 Pilot (AC/AS/DC API)
 */
(function () {
    const API_PILOT = '/api/xpilot/pilot';
    const API_ENV = '/api/xpilot/environment';
    const PAGE = document.body.dataset.xpilotPage || '';

    function showStatus(msg, type) {
        const bar = document.getElementById('statusBar');
        if (!bar) return;
        bar.textContent = msg;
        bar.className = `status-bar ${type || 'info'}`;
        bar.classList.remove('hidden');
    }

    function escapeHtml(t) {
        const d = document.createElement('div');
        d.textContent = t ?? '';
        return d.innerHTML;
    }

    async function fetchJson(url, options) {
        const res = await fetch(url, options);
        const body = await res.json();
        if (!res.ok || body.success === false) {
            throw new Error(body.message || res.statusText);
        }
        return body;
    }

    async function loadDashboard() {
        const settings = await fetchJson(`${API_ENV}/settings-summary`);
        const pilots = await fetchJson(API_PILOT);
        const s = settings.data || {};
        const set = (id, v) => {
            const el = document.getElementById(id);
            if (el) el.textContent = v ?? '—';
        };
        set('envMatchCount', s.matchCount);
        set('envWarnCount', s.warnCount);
        set('envTotalCompared', s.totalCompared);
        set('pilotSessionCount', pilots.count ?? (pilots.data ? pilots.data.length : 0));
    }

    async function loadPilotList() {
        const tbody = document.getElementById('pilotTableBody');
        if (!tbody) return;
        try {
            const body = await fetchJson(API_PILOT);
            const rows = body.data || [];
            if (!rows.length) {
                tbody.innerHTML = '<tr><td colspan="5" class="muted">등록된 Pilot 세션이 없습니다.</td></tr>';
                return;
            }
            tbody.innerHTML = rows.map((p) => `
                <tr>
                    <td><code>${escapeHtml(p.pilotId)}</code></td>
                    <td>${escapeHtml(p.pilotName)}</td>
                    <td>${escapeHtml(p.targetModule)}</td>
                    <td><small>${escapeHtml(p.sourceStructure)} → ${escapeHtml(p.targetStructure)}</small></td>
                    <td>${escapeHtml(p.status)}</td>
                </tr>
            `).join('');
        } catch (e) {
            tbody.innerHTML = `<tr><td colspan="5" class="muted">${escapeHtml(e.message)}</td></tr>`;
        }
    }

    async function createPilot(payload) {
        const body = await fetchJson(`${API_PILOT}/create`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        showStatus(body.message || 'Pilot 세션이 생성되었습니다.', 'success');
        await loadPilotList();
        return body.data;
    }

    function bindPilot002() {
        const form = document.getElementById('pilotCreateForm');
        if (!form) return;
        document.getElementById('btnPilotCreate')?.addEventListener('click', async () => {
            try {
                const name = document.getElementById('pilotName')?.value?.trim();
                if (!name) {
                    showStatus('Pilot 이름을 입력하세요.', 'error');
                    return;
                }
                await createPilot({
                    pilotName: name,
                    envRunId: document.getElementById('envRunId')?.value?.trim() || null,
                    note: document.getElementById('pilotNote')?.value?.trim() || null
                });
                form.reset();
            } catch (e) {
                showStatus(e.message, 'error');
            }
        });
        document.getElementById('btnPilotSample')?.addEventListener('click', async () => {
            try {
                await fetchJson(`${API_PILOT}/create/sample`);
                showStatus('샘플 Pilot 세션이 생성되었습니다.', 'success');
                await loadPilotList();
            } catch (e) {
                showStatus(e.message, 'error');
            }
        });
        document.getElementById('btnPilotReload')?.addEventListener('click', () => loadPilotList());
        loadPilotList();
    }

    if (PAGE === 'pilot001') {
        loadDashboard().catch((e) => showStatus(e.message, 'error'));
    } else if (PAGE === 'pilot002') {
        bindPilot002();
    }
})();
