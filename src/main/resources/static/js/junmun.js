(function () {
    const API = '/api/junmun';
    let editMode = false;

    const el = (id) => document.getElementById(id);

    function showStatus(msg, isError) {
        const bar = el('statusBar');
        if (!bar) return;
        bar.textContent = msg;
        bar.classList.remove('hidden', 'error', 'ok');
        bar.classList.add(isError ? 'error' : 'ok');
    }

    async function api(url, options) {
        const res = await fetch(url, {
            headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
            ...options
        });
        const json = await res.json();
        if (!res.ok || !json.success) {
            throw new Error(json.message || '요청 실패');
        }
        return json.data;
    }

    function fillForm(d) {
        el('messageCode').value = d.messageCode || '';
        el('messageName').value = d.messageName || '';
        el('transactionId').value = d.transactionId || '';
        el('serviceId').value = d.serviceId || '';
        el('direction').value = d.direction || 'REQ';
        el('standardVersion').value = d.standardVersion || 'PH1-20080421';
        el('documentRef').value = d.documentRef || '';
        el('description').value = d.description || '';
        el('useYn').value = d.useYn || 'Y';
        el('layoutJson').value = formatJson(d.layoutJson);
        el('sampleJson').value = formatJson(d.sampleJson);
        el('messageCode').readOnly = editMode;
        el('formModeBadge').textContent = editMode ? 'UPDATE' : 'CREATE';
        el('formModeBadge').className = 'mode-badge ' + (editMode ? 'update' : 'create');
        el('btnDelete').classList.toggle('hidden', !editMode);
        el('formTitle').textContent = editMode ? '전문 정의 수정' : '전문 정의 등록';
    }

    function readForm() {
        return {
            messageCode: el('messageCode').value.trim(),
            messageName: el('messageName').value.trim(),
            transactionId: el('transactionId').value.trim(),
            serviceId: el('serviceId').value.trim(),
            direction: el('direction').value,
            standardVersion: el('standardVersion').value.trim(),
            documentRef: el('documentRef').value.trim(),
            description: el('description').value.trim(),
            useYn: el('useYn').value,
            layoutJson: el('layoutJson').value,
            sampleJson: el('sampleJson').value,
            createdBy: 'WEB',
            updatedBy: 'WEB'
        };
    }

    function formatJson(text) {
        if (!text) return '';
        try {
            return JSON.stringify(JSON.parse(text), null, 2);
        } catch (e) {
            return text;
        }
    }

    function resetForm() {
        editMode = false;
        el('messageCode').readOnly = false;
        fillForm({
            messageCode: '',
            messageName: '',
            transactionId: '',
            serviceId: '',
            direction: 'REQ',
            standardVersion: 'PH1-20080421',
            useYn: 'Y',
            layoutJson: '',
            sampleJson: ''
        });
    }

    async function loadPh1Defaults() {
        const d = await api(API + '/defaults');
        editMode = false;
        fillForm(d);
        showStatus('PH1 내부표준전문 기본값을 불러왔습니다.', false);
    }

    async function refreshList() {
        const code = el('searchCode').value.trim();
        const dir = el('searchDirection').value;
        let url = API + '/definitions?';
        if (code) url += 'messageCode=' + encodeURIComponent(code) + '&';
        if (dir) url += 'direction=' + encodeURIComponent(dir);
        const rows = await api(url);
        const tbody = el('junmunTable').querySelector('tbody');
        tbody.innerHTML = '';
        rows.forEach((r) => {
            const tr = document.createElement('tr');
            tr.innerHTML = `<td>${esc(r.messageCode)}</td><td>${esc(r.messageName)}</td>
                <td>${esc(r.transactionId)}</td><td>${esc(r.direction)}</td><td>${esc(r.standardVersion)}</td>`;
            tr.addEventListener('click', () => selectRow(r.messageCode));
            tbody.appendChild(tr);
        });
    }

    function esc(s) {
        return (s == null ? '' : String(s)).replace(/[&<>"']/g, (c) =>
            ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[c]);
    }

    async function selectRow(messageCode) {
        const d = await api(API + '/definitions/' + encodeURIComponent(messageCode));
        editMode = true;
        fillForm(d);
        showStatus('전문 정의를 불러왔습니다: ' + messageCode, false);
    }

    async function save() {
        const body = readForm();
        try {
            JSON.parse(body.layoutJson);
            if (body.sampleJson) JSON.parse(body.sampleJson);
        } catch (e) {
            showStatus('JSON 형식 오류: ' + e.message, true);
            return;
        }
        if (editMode) {
            await api(API + '/definitions/' + encodeURIComponent(body.messageCode), {
                method: 'PUT',
                body: JSON.stringify(body)
            });
            showStatus('수정되었습니다.', false);
        } else {
            await api(API + '/definitions', { method: 'POST', body: JSON.stringify(body) });
            editMode = true;
            el('messageCode').readOnly = true;
            showStatus('등록되었습니다.', false);
        }
        refreshList();
    }

    async function remove() {
        const code = el('messageCode').value.trim();
        if (!code || !confirm('전문 정의를 삭제하시겠습니까?')) return;
        await api(API + '/definitions/' + encodeURIComponent(code), { method: 'DELETE' });
        showStatus('삭제되었습니다.', false);
        resetForm();
        refreshList();
    }

    async function buildEnvelope() {
        const code = el('messageCode').value.trim();
        if (!code) {
            showStatus('전문코드를 먼저 선택하거나 입력하세요.', true);
            return;
        }
        let fieldValues = {};
        try {
            fieldValues = JSON.parse(el('fieldValuesJson').value || '{}');
        } catch (e) {
            showStatus('필드값 JSON 오류', true);
            return;
        }
        const result = await api(API + '/definitions/' + encodeURIComponent(code) + '/build', {
            method: 'POST',
            body: JSON.stringify({ fieldValues })
        });
        el('envelopeResult').value = result.envelopeJson || '';
        el('validateSummary').textContent = result.valid
            ? '생성 및 자동 검증 성공'
            : '검증 오류: ' + (result.validationErrors || []).join(', ');
        showStatus('JSON 전문이 생성되었습니다.', false);
    }

    async function validateEnvelope() {
        const code = el('messageCode').value.trim();
        const envelope = el('envelopeResult').value;
        if (!code || !envelope) {
            showStatus('전문코드와 envelope JSON이 필요합니다.', true);
            return;
        }
        const result = await api(API + '/definitions/' + encodeURIComponent(code) + '/validate', {
            method: 'POST',
            body: JSON.stringify({ envelopeJson: envelope })
        });
        el('validateSummary').textContent = result.valid
            ? '검증 성공'
            : '검증 실패: ' + (result.validationErrors || []).join(', ');
        showStatus(result.valid ? '검증 성공' : '검증 실패', !result.valid);
    }

    document.addEventListener('DOMContentLoaded', () => {
        el('btnLoadPh1').addEventListener('click', () => loadPh1Defaults().catch((e) => showStatus(e.message, true)));
        el('btnSave').addEventListener('click', () => save().catch((e) => showStatus(e.message, true)));
        el('btnReset').addEventListener('click', resetForm);
        el('btnDelete').addEventListener('click', () => remove().catch((e) => showStatus(e.message, true)));
        el('btnRefreshList').addEventListener('click', () => refreshList().catch((e) => showStatus(e.message, true)));
        el('btnBuild').addEventListener('click', () => buildEnvelope().catch((e) => showStatus(e.message, true)));
        el('btnValidate').addEventListener('click', () => validateEnvelope().catch((e) => showStatus(e.message, true)));
        loadPh1Defaults().catch(() => refreshList().catch(() => {}));
        refreshList().catch(() => {});
    });
})();
