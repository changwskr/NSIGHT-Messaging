const API_EXECUTE = '/zpilotfwk/sp-common/execute';

const form = document.getElementById('executeForm');
const statusBar = document.getElementById('statusBar');
const inputBox = document.getElementById('inputBox');
const resultBox = document.getElementById('resultBox');
const resultSummary = document.getElementById('resultSummary');
const resultBadge = document.getElementById('resultBadge');
const resetBtn = document.getElementById('resetBtn');

const DEFAULTS = {
    common: {
        terminalID: 'TERM001',
        terminalType: 'WEB',
        xmlSeq: '1',
        bankCode: '11',
        branchCode: '0001',
        glPostBranchCode: '0001',
        channelType: 'ONL',
        userID: 'TESTER',
        eventNo: '71001',
        nation: 'KR',
        regionCode: '01',
        timeZone: 'Asia/Seoul',
        fxRateCount: 0,
        reqName: 'SP_COMMON_TEST',
        systemDate: '',
        businessDate: '',
        systemInTime: '',
        systemOutTime: '*',
        transactionNo: '*',
        baseCurrency: 'KRW',
        multiPL: 'N',
        userLevel: 0,
        ipAddress: '127.0.0.1',
        systemName: 'WEB',
        operationName: 'WEB.sp-common.execute',
        tpfq: '200',
        txTimer: '60',
        hostseq: 'HOST-0001',
        orgseq: 'ORG-0001'
    },
    transactionMode: window.ZPILOTFWK_DEFAULT_TX_MODE || 'container',
    bizData: {
        name: '홍길동',
        age: 30,
        phoneNumber: '010-1234-5678'
    }
};

const COMMON_FIELD_IDS = {
    terminalID: 'commonTerminalID',
    terminalType: 'commonTerminalType',
    xmlSeq: 'commonXmlSeq',
    bankCode: 'commonBankCode',
    branchCode: 'commonBranchCode',
    glPostBranchCode: 'commonGlPostBranchCode',
    channelType: 'commonChannelType',
    userID: 'commonUserID',
    eventNo: 'commonEventNo',
    nation: 'commonNation',
    regionCode: 'commonRegionCode',
    timeZone: 'commonTimeZone',
    fxRateCount: 'commonFxRateCount',
    reqName: 'commonReqName',
    systemDate: 'commonSystemDate',
    businessDate: 'commonBusinessDate',
    systemInTime: 'commonSystemInTime',
    systemOutTime: 'commonSystemOutTime',
    transactionNo: 'commonTransactionNo',
    baseCurrency: 'commonBaseCurrency',
    multiPL: 'commonMultiPL',
    userLevel: 'commonUserLevel',
    ipAddress: 'commonIpAddress',
    systemName: 'commonSystemName',
    operationName: 'commonOperationName',
    tpfq: 'commonTpfq',
    txTimer: 'commonTxTimer',
    hostseq: 'commonHostseq',
    orgseq: 'commonOrgseq'
};

function showStatus(message, type) {
    statusBar.textContent = message;
    statusBar.className = 'status-bar';
    if (type === 'error') {
        statusBar.classList.add('error');
    } else if (type === 'success') {
        statusBar.classList.add('success');
    }
    statusBar.classList.remove('hidden');
}

function hideStatus() {
    statusBar.classList.add('hidden');
}

function setResultBadge(errorcode) {
    if (!errorcode) {
        resultBadge.classList.add('hidden');
        return;
    }
    resultBadge.classList.remove('hidden');
    const ok = errorcode.startsWith('I');
    resultBadge.textContent = ok ? 'SUCCESS' : 'ERROR';
    resultBadge.className = 'mode-badge ' + (ok ? 'create' : 'edit');
}

function readCommonField(key) {
    const el = document.getElementById(COMMON_FIELD_IDS[key]);
    if (!el) {
        return DEFAULTS.common[key];
    }
    if (key === 'fxRateCount' || key === 'userLevel') {
        const n = Number(el.value);
        return Number.isNaN(n) ? 0 : n;
    }
    return el.value.trim();
}

function setCommonField(key, value) {
    const el = document.getElementById(COMMON_FIELD_IDS[key]);
    if (!el) {
        return;
    }
    el.value = value != null ? value : '';
}

function buildCommon() {
    const common = {};
    Object.keys(COMMON_FIELD_IDS).forEach(function (key) {
        common[key] = readCommonField(key);
    });
    return common;
}

function buildBizData() {
    return {
        name: document.getElementById('bizName').value.trim(),
        age: Number(document.getElementById('bizAge').value),
        phoneNumber: document.getElementById('bizPhoneNumber').value.trim()
    };
}

function buildRequest() {
    return {
        common: buildCommon(),
        bizData: buildBizData()
    };
}

function buildInputView(transactionMode, request, url) {
    return {
        method: 'POST',
        url: url,
        transactionMode: transactionMode,
        requestBody: request
    };
}

function renderInputView(view) {
    inputBox.textContent = JSON.stringify(view, null, 2);
}

function formatBizDataSummary(bizData) {
    if (!bizData) {
        return 'bizData=-';
    }
    return 'name=' + (bizData.name || '-') +
        ', age=' + (bizData.age != null ? bizData.age : '-') +
        ', phoneNumber=' + (bizData.phoneNumber || '-');
}

function formatCommonSummary(common) {
    if (!common) {
        return 'eventNo=-, transactionNo=-';
    }
    return 'eventNo=' + (common.eventNo || '-') +
        ', transactionNo=' + (common.transactionNo || '-') +
        ', systemOutTime=' + (common.systemOutTime || '-');
}

async function execute() {
    const transactionMode = document.getElementById('transactionMode').value;
    const request = buildRequest();
    const url = API_EXECUTE + '?transactionMode=' + encodeURIComponent(transactionMode);

    renderInputView(buildInputView(transactionMode, request, url));

    showStatus('실행 중…', 'info');
    resultSummary.textContent = '처리 중…';
    resultBox.textContent = '…';
    setResultBadge(null);

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(request)
        });

        const body = await response.json();
        resultBox.textContent = JSON.stringify(body, null, 2);

        if (!response.ok || !body.success) {
            const message = body.message || ('HTTP ' + response.status);
            resultSummary.textContent = '실행 실패: ' + message;
            setResultBadge('E');
            showStatus(message, 'error');
            return;
        }

        const data = body.data || {};
        const err = data.err || {};
        resultSummary.textContent = 'errorcode=' + (err.errorcode || err.errcode || '-') +
            ', ' + formatCommonSummary(data.common) +
            ', ' + formatBizDataSummary(data.bizData);
        setResultBadge(err.errorcode || err.errcode || 'E');
        showStatus('실행 완료', 'success');
    } catch (err) {
        resultSummary.textContent = '통신 오류';
        resultBox.textContent = String(err);
        setResultBadge('E');
        showStatus(err.message || '통신 오류', 'error');
    }
}

function resetForm() {
    Object.keys(COMMON_FIELD_IDS).forEach(function (key) {
        setCommonField(key, DEFAULTS.common[key]);
    });
    document.getElementById('transactionMode').value = DEFAULTS.transactionMode;
    document.getElementById('bizName').value = DEFAULTS.bizData.name;
    document.getElementById('bizAge').value = DEFAULTS.bizData.age;
    document.getElementById('bizPhoneNumber').value = DEFAULTS.bizData.phoneNumber;
    resultSummary.textContent = '실행 전입니다.';
    inputBox.textContent = '입력 정보가 여기에 표시됩니다.';
    resultBox.textContent = '결과가 여기에 표시됩니다.';
    setResultBadge(null);
    hideStatus();
}

form.addEventListener('submit', function (event) {
    event.preventDefault();
    execute();
});

resetBtn.addEventListener('click', resetForm);
