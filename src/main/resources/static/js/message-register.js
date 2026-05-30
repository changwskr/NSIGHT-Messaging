const form = document.getElementById('messageForm');
const resultBox = document.getElementById('resultBox');
const tableBody = document.getElementById('messageTableBody');
const resetBtn = document.getElementById('resetBtn');
const reloadBtn = document.getElementById('reloadBtn');
const filterType = document.getElementById('filterType');
const filterChannel = document.getElementById('filterChannel');

function guid() {
    const now = new Date();
    const ymd = now.toISOString().replace(/[-:TZ.]/g, '').slice(0, 14);
    return `${ymd}-MSG-${crypto.randomUUID().slice(0, 8).toUpperCase()}`;
}

function formToJson() {
    const data = new FormData(form);
    const value = Object.fromEntries(data.entries());
    value.displayStartAt = value.displayStartAt || null;
    value.displayEndAt = value.displayEndAt || null;
    return value;
}

function printResult(data) {
    resultBox.textContent = JSON.stringify(data, null, 2);
}

async function loadMessages() {
    const params = new URLSearchParams();
    if (filterType.value) params.append('messageType', filterType.value);
    if (filterChannel.value) params.append('channelCode', filterChannel.value);
    const res = await fetch(`/api/v1/messages?${params.toString()}`, {
        headers: {
            'X-GUID': guid(),
            'X-USER-ID': 'ARCHITECT',
            'X-BRANCH-ID': '360001',
            'X-CENTER-ID': 'CENTER_1'
        }
    });
    const data = await res.json();
    const rows = data?.body?.response || [];
    tableBody.innerHTML = rows.map(row => `
        <tr>
            <td>${row.messageId}</td>
            <td><strong>${row.messageCode}</strong></td>
            <td>${row.messageName}</td>
            <td><span class="pill">${row.messageType}</span></td>
            <td>${row.channelCode}</td>
            <td>${row.useYn}</td>
            <td><span class="pill ${row.activeNow ? 'ok' : 'no'}">${row.activeNow ? 'ACTIVE' : 'INACTIVE'}</span></td>
            <td>${row.createdBy || ''}</td>
            <td>${row.createdAt ? row.createdAt.replace('T', ' ') : ''}</td>
        </tr>
    `).join('');
}

form.addEventListener('submit', async (event) => {
    event.preventDefault();
    const payload = formToJson();
    const res = await fetch('/api/v1/messages', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-GUID': guid(),
            'X-TRACE-ID': crypto.randomUUID(),
            'X-USER-ID': 'ARCHITECT',
            'X-BRANCH-ID': '360001',
            'X-CENTER-ID': 'CENTER_1',
            'X-TERMINAL-ID': 'TERM-360001-001'
        },
        body: JSON.stringify(payload)
    });
    const data = await res.json();
    printResult(data);
    await loadMessages();
});

resetBtn.addEventListener('click', () => {
    form.reset();
    form.locale.value = 'ko_KR';
    form.useYn.value = 'Y';
});
reloadBtn.addEventListener('click', loadMessages);
filterType.addEventListener('change', loadMessages);
filterChannel.addEventListener('change', loadMessages);
loadMessages();
