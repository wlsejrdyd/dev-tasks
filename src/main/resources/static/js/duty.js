document.addEventListener("DOMContentLoaded", () => {
    const dutyTable = document.getElementById("duty-table").querySelector("tbody");
    const statTableBody = document.getElementById("stat-table").querySelector("tbody");
    const saveBtn = document.getElementById("save-btn");
    const yearSelect = document.getElementById("year-select");
    const monthSelect = document.getElementById("month-select");

    function createEditableGrid() {
        dutyTable.innerHTML = "";
        for (let w = 0; w < 5; w++) {
            for (let r = 0; r < 5; r++) {
                const tr = document.createElement("tr");
                for (let c = 0; c < 8; c++) {
                    const td = document.createElement("td");
                    td.contentEditable = true;
                    td.style.minWidth = "80px";
                    tr.appendChild(td);
                }
                dutyTable.appendChild(tr);
            }
        }
    }

    async function loadTable() {
        const year = parseInt(yearSelect.value);
        const month = parseInt(monthSelect.value);
        const res = await fetch(`/api/duty/load?year=${year}&month=${month}`);
        const data = await res.json();

        createEditableGrid();

	for (let i = 0; i < dutyTable.rows.length; i++) {
            const mod = i % 5;
            const week = Math.floor(i / 5);
            if (mod === 0) {
                const days = ["일", "월", "화", "수", "목", "금", "토"];
                for (let j = 1; j <= 7; j++) {
                    dutyTable.rows[i].cells[j].innerText = days[j - 1];
                }
            } else if (mod === 1) {
                const startDay = week * 7;
                for (let j = 1; j <= 7; j++) {
                    const d = new Date(year, month - 1, startDay + j);
                    if (d.getMonth() + 1 === month) {
                        dutyTable.rows[i].cells[j].innerText = `${month}월 ${d.getDate()}일`;
                    } else {
                        dutyTable.rows[i].cells[j].innerText = "-";
                    }
                }
            } else if (mod === 2) {
                dutyTable.rows[i].cells[0].innerText = "주간 근무(09~19)";
            } else if (mod === 3) {
                dutyTable.rows[i].cells[0].innerText = "야간 근무(19~09)";
            } else if (mod === 4) {
                dutyTable.rows[i].cells[0].innerText = "야간 근무(22~08)";
            }
        }

        for (const cell of data) {
            const { weekIndex, weekdayIndex, time, name } = cell;
            const rowIndex = weekIndex * 5 + getTimeIndex(time);
            const td = dutyTable.rows[rowIndex]?.cells[weekdayIndex + 1];
            if (td) {
                td.innerText = td.innerText ? td.innerText + ", " + name : name;
            }
        }
    }

    function getTimeIndex(time) {
	if (time === "주간 근무(09~19)") return 2;
        if (time === "야간 근무(19~09)") return 3;
        if (time === "야간 근무(22~08)") return 4;
        return 0;
    }

    async function loadStats() {
        const year = yearSelect.value;
        const month = monthSelect.value.toString().padStart(2, "0");
        const ym = `${year}-${month}`;

        const res = await fetch(`/api/duty/stat/${ym}`);
        const stats = await res.json();

        statTableBody.innerHTML = "";
        stats.forEach(row => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${row.name}</td>
                <td>${row.day}</td>
                <td>${row.night}</td>
                <td>${row.day + row.night}</td>
            `;
            statTableBody.appendChild(tr);
        });
    }

    async function saveData() {
        const year = parseInt(yearSelect.value);
        const month = parseInt(monthSelect.value);
        const rows = [];

        for (let r = 0; r < dutyTable.rows.length; r++) {
            const tds = dutyTable.rows[r].cells;
            const time = tds[0]?.innerText?.trim() || "-";
            const data = [];
            for (let c = 1; c < tds.length; c++) {
                const value = tds[c].innerText.trim();
                data.push(value === "" ? "-" : value);
            }

            // 💡 무조건 rows 에 넣는다
            if (time.includes("근무")) {
                rows.push({ time, data });
            }
        }

        const payload = { year, month, rows };

        await fetch("/api/duty/save", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        alert("저장 완료");
        loadTable();
        loadStats();
    }

    function populateYearMonth() {
        const now = new Date();
        const thisYear = now.getFullYear();
        const thisMonth = now.getMonth() + 1;

        for (let y = thisYear - 1; y <= thisYear + 1; y++) {
            const opt = document.createElement("option");
            opt.value = y;
            opt.textContent = `${y}년`;
            if (y === thisYear) opt.selected = true;
            yearSelect.appendChild(opt);
        }

        for (let m = 1; m <= 12; m++) {
            const opt = document.createElement("option");
            opt.value = m;
            opt.textContent = `${m}월`;
            if (m === thisMonth) opt.selected = true;
            monthSelect.appendChild(opt);
        }
    }

    document.getElementById("duty-table").addEventListener("paste", function (e) {
        e.preventDefault();
        const clipboardData = e.clipboardData || window.clipboardData;
        const text = clipboardData.getData("text/plain");

        const rows = text.split("\n").map(row => row.split("\t"));
        const table = e.currentTarget;

        const selection = window.getSelection();
        const anchorCell = selection.anchorNode?.closest("td");
        if (!anchorCell) return;

        const startRow = anchorCell.parentElement.rowIndex;
        const startCol = anchorCell.cellIndex;

        for (let i = 0; i < rows.length; i++) {
            const row = table.rows[startRow + i];
            if (!row) continue;

            for (let j = 0; j < rows[i].length; j++) {
                const cell = row.cells[startCol + j];
                if (cell) {
                    cell.innerText = rows[i][j].trim();
                }
            }
        }
    });

    populateYearMonth();
    loadTable();
    loadStats();

    saveBtn.addEventListener("click", saveData);
    yearSelect.addEventListener("change", () => {
        loadTable();
        loadStats();
    });
    monthSelect.addEventListener("change", () => {
        loadTable();
        loadStats();
    });
});
