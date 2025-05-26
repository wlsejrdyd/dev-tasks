document.addEventListener("DOMContentLoaded", function () {
    const recordBody = document.getElementById("record-body");
    const statusBody = document.getElementById("status-body");
    const addRowBtn = document.getElementById("add-row");
    const saveBtn = document.getElementById("save-records");
    const deleteBtn = document.getElementById("delete-selected");

    let users = [];

    function normalizeDate(input) {
        if (!input) return "";
        if (input.includes(".")) {
            const parts = input.split(".");
            if (parts.length === 3) {
                return `${parts[0]}-${parts[1].padStart(2, '0')}-${parts[2].padStart(2, '0')}`;
            }
        }
        return input;
    }

    function loadUsers() {
        fetch("/api/users")
            .then(res => res.json())
            .then(data => {
                users = data;
                loadAllRecords();
            });
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
                    `;
                    statusBody.appendChild(tr);
                });
            });
    }

    function loadAllRecords() {
        recordBody.innerHTML = "";
        users.forEach(user => {
            fetch(`/api/attendance/records/${user.id}`)
                .then(res => res.json())
                .then(data => {
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
                                    <option value="연차" ${row.type === "연차" ? "selected" : ""}>연차</option>
                                    <option value="대휴" ${row.type === "대휴" ? "selected" : ""}>대휴</option>
                                </select>
                            </td>
                            <td contenteditable="true" class="editable-cell">${row.startDate}</td>
                            <td contenteditable="true" class="editable-cell">${row.endDate}</td>
                            <td contenteditable="true" class="editable-cell">${row.days}</td>
                            <td contenteditable="true" class="editable-cell">${row.reason || ""}</td>
                        `;
                        recordBody.appendChild(tr);
                    });
                });
        });
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
                    <option value="연차">연차</option>
                    <option value="대휴">대휴</option>
                </select>
            </td>
            <td contenteditable="true" class="editable-cell"></td>
            <td contenteditable="true" class="editable-cell"></td>
            <td contenteditable="true" class="editable-cell"></td>
            <td contenteditable="true" class="editable-cell"></td>
        `;
        recordBody.appendChild(tr);
    }

    function saveRecords() {
        const rows = recordBody.querySelectorAll("tr");
        const payload = [];
    
        rows.forEach(tr => {
            const id = tr.querySelector(".row-check")?.dataset?.id; // ✅ 이미 저장된 row인지 확인
            const userId = tr.querySelector(".user-select")?.value;
            const type = tr.querySelector(".type-select")?.value;
            const tds = tr.querySelectorAll(".editable-cell");
    
            const record = {
                userId: parseInt(userId),
                type: type,
                startDate: normalizeDate(tds[0]?.innerText.trim()),
                endDate: normalizeDate(tds[1]?.innerText.trim()),
                days: parseFloat(tds[2]?.innerText.trim()),
                reason: tds[3]?.innerText.trim()
            };
    
            // ⛔ 저장된 기존 row는 제외하고, 신규만 저장
            if (
                !id &&
                record.userId &&
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
    
        Promise.all(payload.map(p =>
            fetch("/api/attendance/record", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(p)
            })
        )).then(() => {
            alert("저장 완료!");
            loadStatus();
            loadAllRecords();
        });
    }

    function deleteSelected() {
        const checked = document.querySelectorAll(".row-check:checked");
        if (checked.length === 0) {
            alert("삭제할 항목을 선택하세요.");
            return;
        }

        if (!confirm("정말 삭제하시겠습니까?")) return;

        Promise.all(Array.from(checked).map(chk => {
            const id = chk.dataset.id;
            if (id) {
                return fetch(`/api/attendance/record/${id}`, { method: "DELETE" });
            }
        })).then(() => {
            alert("삭제 완료!");
            loadStatus();
            loadAllRecords();
        });
    }

    addRowBtn.addEventListener("click", addRow);
    saveBtn.addEventListener("click", saveRecords);
    deleteBtn.addEventListener("click", deleteSelected);

    loadUsers();
    loadStatus();
});
