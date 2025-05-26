document.addEventListener("DOMContentLoaded", function () {
    const recordBody = document.getElementById("record-body");
    const statusBody = document.getElementById("status-body");
    const addRowBtn = document.getElementById("add-row");
    const saveBtn = document.getElementById("save-records");
    const deleteBtn = document.getElementById("delete-selected");
    const downloadBtn = document.getElementById("download-excel");
    const monthSelector = document.getElementById("month-selector");

    let users = [];
    let allRecords = [];

    function normalizeDate(input) {
        if (!input) return "";
        if (typeof input === "string" && input.includes(".")) {
            const parts = input.split(".");
            if (parts.length === 3) {
                return `${parts[0]}-${parts[1].padStart(2, '0')}-${parts[2].padStart(2, '0')}`;
            }
        }
        return input;
    }

    function getMonth(dateStr) {
        return dateStr?.substring(0, 7); // '2025-05'
    }

    function buildMonthSelector() {
        const months = [...new Set(allRecords.map(r => getMonth(r.startDate)).filter(Boolean))];
        months.sort().reverse();
        monthSelector.innerHTML = `<option value="">전체</option>` +
            months.map(m => `<option value="${m}">${m}</option>`).join('');
    }

    function filterRecordsByMonth(month) {
        const filtered = month
            ? allRecords.filter(r => getMonth(r.startDate) === month)
            : allRecords;
        renderRecordRows(filtered);
    }

    function renderRecordRows(data) {
        recordBody.innerHTML = "";
        data.forEach(row => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td><input type="checkbox" class="row-check" data-id="${row.id}"></td>
                <td>
                    <select class="user-select">
                        ${users.map(u =>
                            `<option value="${u.id}" ${u.id === row.userId ? "selected" : ""}>${u.name}</option>`
                        ).join('')}
                    </select>
                </td>
                <td>
                    <select class="type-select">
                        <option value="연차사용" ${row.type === "연차사용" ? "selected" : ""}>연차사용</option>
                        <option value="대휴사용" ${row.type === "대휴사용" ? "selected" : ""}>대휴사용</option>
                        <option value="대휴부여" ${row.type === "대휴부여" ? "selected" : ""}>대휴부여</option>
                        <option value="기타" ${row.type === "기타" ? "selected" : ""}>기타</option>
                    </select>
                </td>
                <td><input type="date" class="start-date" value="${row.startDate}"></td>
                <td><input type="date" class="end-date" value="${row.endDate}"></td>
                <td contenteditable="true" class="editable-cell">${row.days}</td>
                <td contenteditable="true" class="editable-cell">${row.reason || ""}</td>
            `;
            recordBody.appendChild(tr);
        });
    }

    async function loadAllRecords() {
        let combined = [];
        for (const user of users) {
            const res = await fetch(`/api/attendance/records/${user.id}`);
            const data = await res.json();
            data.forEach(row => {
                row.userId = user.id;
            });
            combined = combined.concat(data);
        }
        allRecords = combined;
        buildMonthSelector();
        filterRecordsByMonth(monthSelector.value);
    }

    function addRow() {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td></td>
            <td>
                <select class="user-select">
                    ${users.map(u => `<option value="${u.id}">${u.name}</option>`).join('')}
                </select>
            </td>
            <td>
                <select class="type-select">
                    <option value="">-- 선택 --</option>
                    <option value="연차사용">연차 사용</option>
                    <option value="대휴사용">대휴 사용</option>
                    <option value="대휴부여">대휴 부여</option>
                    <option value="기타">기타</option>
                </select>
            </td>
            <td><input type="date" class="start-date"></td>
            <td><input type="date" class="end-date"></td>
            <td contenteditable="true" class="editable-cell"></td>
            <td contenteditable="true" class="editable-cell"></td>
        `;
        recordBody.appendChild(tr);
    }

    async function saveRecords() {
        const rows = recordBody.querySelectorAll("tr");
        const payload = [];

        rows.forEach(tr => {
            const id = tr.querySelector(".row-check")?.dataset?.id;
            const userId = parseInt(tr.querySelector(".user-select")?.value || "0");
            const type = tr.querySelector(".type-select")?.value;
            const startDate = tr.querySelector(".start-date")?.value;
            const endDate = tr.querySelector(".end-date")?.value;
            const tds = tr.querySelectorAll(".editable-cell");

            const record = {
                userId,
                type,
                startDate,
                endDate,
                days: parseFloat(tds[0]?.innerText.trim()),
                reason: tds[1]?.innerText.trim(),
            };

            if (
                !id &&
                userId > 0 &&
                record.startDate &&
                record.endDate &&
                !isNaN(record.days) &&
                record.days > 0 &&
                record.type
            ) {
                payload.push(record);
            }
        });

        if (payload.length === 0) {
            alert("저장할 유효한 데이터가 없습니다.");
            return;
        }

        for (const p of payload) {
            await fetch("/api/attendance/record", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(p),
            });
        }

        alert("저장 완료!");
        await loadStatus();
        await loadAllRecords();
    }

    async function deleteSelected() {
        const checked = document.querySelectorAll(".row-check:checked");
        if (checked.length === 0) {
            alert("삭제할 항목을 선택하세요.");
            return;
        }

        if (!confirm("정말 삭제하시겠습니까?")) return;

        for (const chk of checked) {
            const id = chk.dataset.id;
            if (id) {
                await fetch(`/api/attendance/record/${id}`, { method: "DELETE" });
            }
        }

        alert("삭제 완료!");
        await loadStatus();
        await loadAllRecords();
    }

    function downloadExcel() {
        window.location.href = "/api/attendance/export";
    }

    function loadStatus() {
        fetch("/api/attendance/status")
            .then(res => res.json())
            .then(data => {
                statusBody.innerHTML = "";
                data.forEach(row => {
                    const tr = document.createElement("tr");
                    tr.innerHTML = `
                        <td>${row.userName}</td>
                        <td>${row.joinDate}</td>
                        <td>${row.leaveDate || "-"}</td>
                        <td>${row.annualGranted}</td>
                        <td>${row.annualUsed}</td>
                        <td>${row.annualRemain}</td>
                        <td>${row.compensatoryGranted}</td>
                        <td>${row.compensatoryUsed}</td>
                        <td>${row.compensatoryRemain}</td>
                        <td>${row.annualGranted + row.compensatoryGranted}</td>
                        <td>${row.annualUsed + row.compensatoryUsed}</td>
                        <td>${row.annualRemain + row.compensatoryRemain}</td>
                    `;
                    statusBody.appendChild(tr);
                });
            });
    }

    monthSelector.addEventListener("change", () => {
        filterRecordsByMonth(monthSelector.value);
    });

    addRowBtn.addEventListener("click", addRow);
    saveBtn.addEventListener("click", () => setTimeout(saveRecords, 100));
    deleteBtn.addEventListener("click", deleteSelected);
    downloadBtn.addEventListener("click", downloadExcel);

    fetch("/api/users")
        .then(res => res.json())
        .then(data => {
            users = data;
            loadAllRecords();
            loadStatus();
        });
});
