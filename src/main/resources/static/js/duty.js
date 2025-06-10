document.addEventListener("DOMContentLoaded", async () => {
    const dutyTable = document.getElementById("duty-table").querySelector("tbody");
    const statTableBody = document.getElementById("stat-table").querySelector("tbody");
    const saveBtn = document.getElementById("save-btn");
    const yearSelect = document.getElementById("year-select");
    const monthSelect = document.getElementById("month-select");
    const nightSummaryBox = document.getElementById("night-summary-box");

    let holidayMap = {};

    async function loadHolidayMap() {
        try {
            const res = await fetch("/json/holidays.json");
            holidayMap = await res.json();
        } catch (err) {
            console.error("공휴일 데이터를 불러오는 데 실패했습니다:", err);
        }
    }

    function getFormattedDate(date) {
        return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
    }

    async function loadTable() {
        const year = parseInt(yearSelect.value);
        const month = parseInt(monthSelect.value);
        const res = await fetch(`/api/duty/load?year=${year}&month=${month}`);
        const data = await res.json();

        createEditableGrid();

        const days = ["일", "월", "화", "수", "목", "금", "토"];
        const firstDay = new Date(year, month - 1, 1).getDay();
        let currentDay = 1;
        const daysInMonth = new Date(year, month, 0).getDate();

        const today = new Date();
        const todayStr = getFormattedDate(today);
        const yesterStr = getFormattedDate(new Date(today.getTime() - 86400000));
        const beforeStr = getFormattedDate(new Date(today.getTime() - 86400000 * 2));

        const dateMap = {}; // {dateStr: {col, rowMap}}

        for (let i = 0; i < dutyTable.rows.length; i++) {
            const mod = i % 5;
            const week = Math.floor(i / 5);

            if (mod === 0) {
                for (let j = 1; j <= 7; j++) {
                    const cell = dutyTable.rows[i].cells[j];
                    cell.innerText = days[j - 1];
                    cell.className = j === 1 ? "sunday" : j === 7 ? "saturday" : "weekday";
                }
            } else if (mod === 1) {
                for (let j = 1; j <= 7; j++) {
                    const dayIndex = week * 7 + (j - 1);
                    const cell = dutyTable.rows[i].cells[j];
                    if (dayIndex >= firstDay && currentDay <= daysInMonth) {
                        const dateStr = getFormattedDate(new Date(year, month - 1, currentDay));
                        cell.innerText = `${month}월 ${currentDay}일`;

                        dateMap[dateStr] = { col: j, week };

                        if (holidayMap[dateStr]) {
                            cell.classList.add("holiday");
                            cell.title = holidayMap[dateStr];
                        }

                        currentDay++;
                    } else {
                        cell.innerText = "-";
                    }
                }
            } else if (mod === 2) dutyTable.rows[i].cells[0].innerText = "주간 근무(09~19)";
            else if (mod === 3) dutyTable.rows[i].cells[0].innerText = "야간 근무(19~09)";
            else if (mod === 4) dutyTable.rows[i].cells[0].innerText = "야간 근무(22~08)";
        }

        for (const cell of data) {
            const { weekIndex, weekdayIndex, time, name } = cell;
            const rowIndex = weekIndex * 5 + getTimeIndex(time);
            const td = dutyTable.rows[rowIndex]?.cells[weekdayIndex + 1];
            if (td) {
                td.innerText = td.innerText ? td.innerText + ", " + name : name;
            }
        }

        function getNightNamesByDate(dateStr) {
            const pos = dateMap[dateStr];
            if (!pos) return "-";
            const td19 = dutyTable.rows[pos.week * 5 + 3]?.cells[pos.col];
            const td22 = dutyTable.rows[pos.week * 5 + 4]?.cells[pos.col];
            const names = [td19?.innerText || "", td22?.innerText || ""].filter(x => x.trim() !== "");
            return names.length ? names.join(", ") : "-";
        }

        const todayNight = getNightNamesByDate(todayStr);
        const yesterdayNight = getNightNamesByDate(yesterStr);
        const beforeNight = getNightNamesByDate(beforeStr);

        nightSummaryBox.innerHTML = `| 전전 야간: ${beforeNight} || 전 야간: ${yesterdayNight} || 오늘 야간: ${todayNight} |`;

        const todayCol = dateMap[todayStr]?.col;
        const todayWeek = dateMap[todayStr]?.week;
        if (todayCol && todayWeek >= 0) {
            [3, 4].forEach(offset => {
                const td = dutyTable.rows[todayWeek * 5 + offset]?.cells[todayCol];
                if (td) td.classList.add("today-highlight");
            });
        }
    }

    function getTimeIndex(time) {
        if (time === "주간 근무(09~19)") return 2;
        if (time === "야간 근무(19~09)") return 3;
        if (time === "야간 근무(22~08)") return 4;
        return 0;
    }

    function createEditableGrid() {
        dutyTable.innerHTML = "";
        for (let w = 0; w < 5; w++) {
            for (let r = 0; r < 6; r++) {
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

    async function loadStats() {
        const year = yearSelect.value;
        const month = monthSelect.value.toString().padStart(2, "0");
        const ym = `${year}-${month}`;
        const res = await fetch(`/api/duty/stat/${ym}`);
        const stats = await res.json();
        statTableBody.innerHTML = "";
        stats.forEach(row => {
            const total = row.day + row.night;
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${row.name}</td>
                <td>${row.weekdayDay || 0}</td>
                <td>${row.weekdayNight || 0}</td>
                <td>${row.weekendDay || 0}</td>
                <td>${row.weekendNight || 0}</td>
                <td>${row.holidayDay || 0}</td>
                <td>${row.holidayNight || 0}</td>
                <td>${total}</td>
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
            if (time.includes("근무")) rows.push({ time, data });
        }

        await fetch("/api/duty/save", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ year, month, rows })
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
                if (cell) cell.innerText = rows[i][j].trim();
            }
        }
    });

    await loadHolidayMap();
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
