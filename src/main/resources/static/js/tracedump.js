const API_BASE = '/api/v1/trace-dump';
const analyzeForm = document.getElementById('analyzeForm');
const statusBar = document.getElementById('statusBar');
const summaryBox = document.getElementById('summaryBox');
const findingList = document.getElementById('findingList');
const reportBox = document.getElementById('reportBox');
const reportPanel = document.getElementById('reportPanel');

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

function renderKvTable(tableEl, rows) {
    if (!tableEl) return;
    tableEl.classList.add('dump-report__table', 'dump-report__table--kv');
    tableEl.innerHTML = rows.map(([label, value]) => `
        <tr><th>${escapeHtml(label)}</th><td class="kv-cell">${escapeHtml(value)}</td></tr>
    `).join('');
}

function renderFindings(findings, target = findingList) {
    if (!findings || findings.length === 0) {
        target.innerHTML = '<p class="tx-detail-placeholder">Finding 없음</p>';
        return;
    }
    target.innerHTML = findings.map(f => {
        const cls = (f.severity === 'CRITICAL' || f.severity === 'HIGH') ? 'error fail' : 'header';
        return `
        <section class="envelope-card ${cls}">
            <h3>[${escapeHtml(f.severity)}] ${escapeHtml(f.id)} — ${escapeHtml(f.title)}</h3>
            <dl>
                <dt>Rule / 분류</dt><dd>${escapeHtml(f.id)} · ${escapeHtml(f.category)}</dd>
                <dt>가이드 §</dt><dd>${escapeHtml(f.guideSection)}</dd>
                <dt>판정 설명</dt><dd>${escapeHtml(f.description)}</dd>
                <dt>증거</dt><dd>${escapeHtml(f.evidence)}</dd>
            </dl>
        </section>`;
    }).join('');
}

function renderFourStep(guide) {
    const el = document.getElementById('fourStepGuide');
    if (!guide) {
        el.innerHTML = '';
        return;
    }
    el.innerHTML = `
        <div class="dump-step"><span class="dump-step__no">①</span><p>${escapeHtml(guide.step1Read)}</p></div>
        <div class="dump-step"><span class="dump-step__no">②</span><p>${escapeHtml(guide.step2Evidence)}</p></div>
        <div class="dump-step"><span class="dump-step__no">③</span><p>${escapeHtml(guide.step3Judgment)}</p></div>
        <div class="dump-step"><span class="dump-step__no">④</span><p>${escapeHtml(guide.step4Action)}</p></div>
    `;
}

function renderPipeline(steps) {
    const el = document.getElementById('pipelineList');
    el.innerHTML = (steps || []).map(s =>
        `<li><strong>${s.order}. ${escapeHtml(s.phase)}</strong> — ${escapeHtml(s.description)}</li>`
    ).join('');
}

function renderIndicators(ind) {
    if (!ind) return;
    renderKvTable(document.getElementById('indicatorsTable'), [
        ['Java Heap OOM', ind.javaHeapOom ? 'Y' : 'N'],
        ['Full GC 누적', String(ind.fullGcCount ?? 0)],
        ['Old Region 미회수', ind.oldRegionNotReduced ? 'Y' : 'N'],
        ['Full GC 후 Heap 미회수', ind.heapNotReducedAfterFullGc ? 'Y' : 'N'],
        ['Deadlock', ind.deadlockFound ? 'Y' : 'N'],
        ['Hikari 대기 Thread(max)', String(ind.hikariWaitingThreads ?? 0)],
        ['CruzAPIM 대기 Thread(max)', String(ind.cruzApimWaitingThreads ?? 0)],
        ['SESSION_CACHE 힌트', ind.sessionCacheHint ? 'Y' : 'N'],
        ['QUERY_RESULT_CACHE 힌트', ind.queryResultCacheHint ? 'Y' : 'N'],
        ['Heap Dump 수집', ind.heapDumpCollected ? 'Y' : 'N']
    ]);
}

function renderHypotheses(rows) {
    const body = document.querySelector('#hypothesisTable tbody');
    if (!rows || rows.length === 0) {
        body.innerHTML = '<tr><td colspan="7">원인 가설 없음</td></tr>';
        return;
    }
    body.innerHTML = rows.map(r => `
        <tr class="hyp-row">
            <td class="hyp-cell hyp-cell--pri">${r.priority}</td>
            <td class="hyp-cell hyp-cell--name"><strong>${escapeHtml(r.causeName)}</strong></td>
            <td class="hyp-cell hyp-cell--sev"><span class="pill ${r.severity === 'Critical' || r.severity === 'High' ? 'no' : ''}">${escapeHtml(r.severity)}</span></td>
            <td class="hyp-cell hyp-cell--conf">${escapeHtml(r.confidence)}</td>
            <td class="hyp-cell hyp-cell--ev">${escapeHtml(r.evidence)}</td>
            <td class="hyp-cell hyp-cell--rule"><code>${escapeHtml(r.ruleIds)}</code></td>
            <td class="hyp-cell hyp-cell--act">${escapeHtml(r.recommendedAction)}</td>
        </tr>
    `).join('');
}

function renderGc(gc) {
    if (!gc) return;
    renderKvTable(document.getElementById('gcTable'), [
        ['요약', gc.summary],
        ['Full GC', String(gc.fullGcCount ?? 0)],
        ['Max Pause (ms)', String(gc.maxPauseMs ?? 0)],
        ['Old Region 고정', String(gc.oldRegionUnchangedCount ?? 0)],
        ['Heap 미회수', String(gc.heapUnchangedCount ?? 0)]
    ]);
    document.getElementById('gcPatterns').textContent =
        'GC 로그에서 찾을 패턴: ' + (gc.watchPatterns || []).join(' · ');
}

function renderOomCorrelations(rows) {
    const body = document.querySelector('#oomCorrelationTable tbody');
    if (!rows || rows.length === 0) {
        body.innerHTML = '<tr><td colspan="6">OOM 관련 로그 패턴 미검출 — 증거 ZIP에 gc*.log, hs_err, thread dump를 포함하세요.</td></tr>';
        return;
    }
    body.innerHTML = rows.map(r => `
        <tr class="oom-row oom-row-${(r.severity || 'INFO').toLowerCase()}">
            <td class="oom-cell oom-cell--area">
                <strong>${escapeHtml(r.problemArea)}</strong>
                <span class="oom-id">${escapeHtml(r.id)}</span>
            </td>
            <td class="oom-cell oom-log-cell">${escapeHtml(r.logEvidence)}</td>
            <td class="oom-cell oom-cell--program">${escapeHtml(r.relatedProgram)}</td>
            <td class="oom-cell oom-cell--cause">${escapeHtml(r.probableCause)}</td>
            <td class="oom-cell oom-cell--file"><code>${escapeHtml(r.evidenceFile)}</code></td>
            <td class="oom-cell oom-cell--sev"><span class="pill ${r.severity === 'HIGH' ? 'no' : ''}">${escapeHtml(r.severity)}</span></td>
        </tr>
    `).join('');
}

function renderReportView(view, findings) {
    if (!view) {
        reportPanel.classList.add('hidden');
        return;
    }
    reportPanel.classList.remove('hidden');

    const oom = view.oomCategory || 'UNKNOWN';
    const badge = document.getElementById('reportOomBadge');
    badge.textContent = 'OOM: ' + oom;
    badge.className = 'dump-report__oom-badge' + (oom === 'UNKNOWN' ? ' ok' : '');

    document.getElementById('reportMeta').innerHTML = `
        분석 시각: <strong>${escapeHtml(view.analyzedAt?.replace('T', ' '))}</strong><br/>
        증거 경로: <code>${escapeHtml(view.evidencePath)}</code>
    `;

    renderFourStep(view.fourStepGuide);
    renderPipeline(view.analysisPipeline);
    renderIndicators(view.keyIndicators);
    renderGc(view.gcAnalysis);
    renderHypotheses(view.causeHypotheses || []);

    const ov = view.overview || {};
    renderKvTable(document.getElementById('overviewTable'), [
        ['발생일시', ov.occurredAt],
        ['대상 시스템', ov.targetSystem],
        ['AP/VM', ov.apVm],
        ['JVM 옵션', ov.jvmOptions],
        ['장애 유형', ov.faultType],
        ['사용자 영향', ov.userImpact]
    ]);

    const evidenceBody = document.querySelector('#evidenceTable tbody');
    const inventory = view.evidenceInventory || [];
    evidenceBody.innerHTML = inventory.length === 0
        ? '<tr><td colspan="4">수집된 증거 없음</td></tr>'
        : inventory.map(row => `
            <tr class="ev-row">
                <td>${escapeHtml(row.evidenceType)}</td>
                <td class="ev-cell--file"><code>${escapeHtml(row.fileName)}</code></td>
                <td class="ev-cell--time">${escapeHtml(row.collectedAt)}</td>
                <td class="ev-cell--note">${escapeHtml(row.note)}</td>
            </tr>
        `).join('');

    const j = view.primaryJudgment || {};
    renderOomCorrelations(view.oomCorrelations || []);

    renderKvTable(document.getElementById('judgmentTable'), [
        ['Heap 사용률', j.heapUsage],
        ['Old Region 증가 여부', j.oldRegionGrowth],
        ['Full GC 반복 여부', j.fullGcRepeat],
        ['Deadlock 여부', j.deadlock],
        ['Hikari Pool 대기 여부', j.hikariPoolWait],
        ['Native Memory 증가 여부', j.nativeMemoryGrowth]
    ]);

    const heap = view.heapAnalysis || {};
    renderKvTable(document.getElementById('heapTable'), [
        ['Top Retained Object', heap.topRetainedObject],
        ['의심 클래스', heap.suspectClasses],
        ['GC Root 경로', heap.gcRootPath],
        ['누수 추정 원인', heap.leakCauseEstimate]
    ]);
    const heapFiles = heap.heapDumpFiles || [];
    document.getElementById('heapFileList').innerHTML = heapFiles.length === 0
        ? '<li>Heap Dump 파일 없음</li>'
        : heapFiles.map(f => `<li><code>${escapeHtml(f)}</code></li>`).join('');

    const thread = view.threadAnalysis || {};
    renderKvTable(document.getElementById('threadSummaryTable'), [
        ['RUNNABLE Thread 수', String(thread.runnableCount ?? 0)],
        ['WAITING Thread 수', String(thread.waitingCount ?? 0)],
        ['BLOCKED Thread 수', String(thread.blockedCount ?? 0)],
        ['Deadlock', thread.deadlock],
        ['DB Pool 대기', thread.dbPoolWait],
        ['외부연계 대기', thread.externalWait]
    ]);
    const dumps = thread.dumps || [];
    const threadBody = document.querySelector('#threadDetailTable tbody');
    threadBody.innerHTML = dumps.length === 0
        ? '<tr><td colspan="9">Thread Dump 없음</td></tr>'
        : dumps.map(d => `
            <tr class="th-row">
                <td class="th-cell--file"><code>${escapeHtml(d.sourceFile)}</code></td>
                <td class="th-cell--num">${d.totalThreads}</td>
                <td class="th-cell--num">${d.runnable}</td>
                <td class="th-cell--num">${d.waiting}</td>
                <td class="th-cell--num">${d.blocked}</td>
                <td class="th-cell--num">${d.deadlock ? 'Y' : 'N'}</td>
                <td class="th-cell--num">${d.hikariWait}</td>
                <td class="th-cell--num">${d.jdbcWait}</td>
                <td class="th-cell--num">${d.cruzApimWait ?? 0}</td>
            </tr>
        `).join('');

    const conclusions = view.causeConclusions || [];
    const conclusionBody = document.querySelector('#conclusionTable tbody');
    conclusionBody.innerHTML = conclusions.length === 0
        ? '<tr><td colspan="2" class="dump-empty">원인 결론 없음</td></tr>'
        : conclusions.map(c => `
        <tr>
            <th>${escapeHtml(c.causeType)}</th>
            <td class="conclusion-cell">${escapeHtml(c.judgment)}</td>
        </tr>
    `).join('');

    const act = view.actionPlan || {};
    renderKvTable(document.getElementById('actionTable'), [
        ['즉시 조치', act.immediate],
        ['설정 조치', act.config],
        ['소스 조치', act.source],
        ['운영 조치', act.operations],
        ['성능테스트 보완', act.performanceTest]
    ]);

    renderFindings(findings, document.getElementById('reportFindingList'));
    reportPanel.scrollIntoView({ behavior: 'smooth', block: 'start' });
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
        (CRITICAL ${summary.criticalSeverityCount ?? 0}, HIGH ${summary.highSeverityCount ?? 0})
    `;
    renderFindings(body.findings);
    reportBox.textContent = body.markdownReport || '';
    renderReportView(body.reportView, body.findings);
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
        showStatus('분석이 완료되었습니다. 보고서를 확인하세요.', 'success');
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

document.getElementById('printReportBtn')?.addEventListener('click', () => window.print());
