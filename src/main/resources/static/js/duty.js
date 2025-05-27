document.addEventListener("DOMContentLoaded", () => {
    const dutyTable = document.getElementById("duty-table").querySelector("tbody");
    const statTableBody = document.getElementById("stat-table").querySelector("tbody");
    const saveBtn = document.getElementById("save-btn");
    const yearSelect = document.getElementById("year-select");
    const monthSelect = document.getElementById("month-select");

    function createEditableGrid() {
        dutyTable.innerHTML = "";
        for (let i = 0; i < 30; i++) {
            const tr = document.createElement("tr");
            for (let j = 0; j < 8; j++) {
                const td = document.createElement("td");
                td.contentEditable = true;
                td.style.minWidth = "80px";
                tr.appendChild(td);
            }
            dutyTable.appendChild(tr);
        }
    }

    async function loadTable() {
        const year = parseInt(yearSelect.value);
        const month = parseInt(monthSelect.value);
        const res = await fetch(`/api/duty/load?year=${year}&month=${month}`);
        const data = await res.json();

        createEditableGrid();

        for (const cell of data) {
            const { weekIndex, weekdayIndex, time, name } = cell;
            const rowIndex = weekIndex * 3 + getTimeIndex(time);
            const td = dutyTable.rows[rowIndex]?.cells[weekdayIndex + 1];
            if (td) {
                td.innerText = td.innerText ? td.innerText + ", " + name : name;
            }
        }

        // 시간 라벨 및 날짜 출력
        const startDate = new Date(year, month - 1, 1);
        for (let week = 0; week < 5; week++) {
            const baseRow = week * 3;
            const headerRow = dutyTable.rows[baseRow];
            if (!headerRow) continue;

            headerRow.cells[0].innerText = "";
            for (let day = 0; day < 7; day++) {
                const date = new Date(startDate);
                date.setDate(1 + week * 7 + day - (startDate.getDay() % 7));
                if (date.getMonth() + 1 === month) {
                    headerRow.cells[day + 1].innerText = `${month}월 ${date.getDate()}일`;
                } else {
                    headerRow.cells[day + 1].innerText = "";
                }
            }

            if (dutyTable.rows[baseRow + 1]) {
                dutyTable.rows[baseRow + 1].cells[0].innerText = "주간 근무(09~19)";
            }
            if (dutyTable.rows[baseRow + 2]) {
                dutyTable.rows[baseRow + 2].cells[0].innerText = "야간 근무(19~09)";
            }
        }
    }

    function getTimeIndex(time) {
        if (time.includes("주간")) return 1;
        if (time.includes("야간")) return 2;
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
            const mod = r % 3;
            if (mod === 1 || mod === 2) {
                const time = tds[0]?.innerText?.trim() || "";
                const data = [];
                for (let c = 1; c < tds.length; c++) {
                    data.push(tds[c].innerText.trim());
                }
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

        const rows = text.trim().split("\n").map(row => row.split("\t"));
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
