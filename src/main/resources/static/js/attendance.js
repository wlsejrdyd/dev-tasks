document.addEventListener("DOMContentLoaded", function () {
  const baseBtnStyle = `
    padding: 6px 14px;
    font-size: 14px;
    border: 1px solid #888;
    border-radius: 4px;
    background-color: #f4f4f4;
    cursor: pointer;
  `;

  function normalizeDate(str) {
    str = str?.trim();
    if (!str) return "";

    if (/^\d{4}-\d{2}-\d{2}$/.test(str)) return str;
    if (/^\d{4}\.\d{2}\.\d{2}$/.test(str)) return str.replace(/\./g, "-");
    if (/^\d{1,2}\/\d{1,2}$/.test(str)) {
      const [month, day] = str.split("/");
      return `2025-${month.padStart(2, "0")}-${day.padStart(2, "0")}`;
    }
    if (/^\d{4}$/.test(str)) return `${str}-01-01`;
    return str;
  }

  function attachPasteHandler(cell) {
    cell.addEventListener("paste", function (e) {
      e.preventDefault();
      const text = e.clipboardData.getData("text/plain");
      const rows = text.split(/\r?\n/).map(row => row.split("\t"));
      const table = this.closest("table");
      const tbody = table.querySelector("tbody");
      let startRow = this.parentElement.rowIndex;
      let startCol = this.cellIndex;

      for (let i = 0; i < rows.length; i++) {
        const cells = rows[i];
        let targetRow = table.rows[startRow + i];
        if (!targetRow || !targetRow.cells.length) {
          const newRow = document.createElement("tr");
          newRow.innerHTML = table.classList.contains("data-table") ? detailRowTemplate() : summaryRowTemplate();
          tbody.appendChild(newRow);
          targetRow = newRow;
          newRow.querySelectorAll("td[contenteditable=true]").forEach(attachPasteHandler);
        }
        for (let j = 0; j < cells.length; j++) {
          const targetCell = targetRow.cells[startCol + j];
          if (targetCell && targetCell.isContentEditable) {
            targetCell.textContent = cells[j];
          }
        }
      }
    });
  }

  function summaryRowTemplate() {
    return `
      <td contenteditable="true">이름</td>
      <td contenteditable="true">YYYY.MM.DD</td>
      <td contenteditable="true">-</td>
      <td contenteditable="true">17</td>
      <td contenteditable="true">0</td>
      <td>17.0</td>
      <td contenteditable="true">0</td>
      <td contenteditable="true">0</td>
      <td>0.0</td>
      <td>17.0</td>
      <td>0.0</td>
      <td>17.0</td>
    `;
  }

  function detailRowTemplate() {
    return `
      <td contenteditable="true">홍길동</td>
      <td contenteditable="true">대휴 부여</td>
      <td contenteditable="true">2025-01-01</td>
      <td contenteditable="true">2025-01-01</td>
      <td contenteditable="true">1</td>
      <td contenteditable="true">사유</td>
    `;
  }

  function enableRowDelete(selector) {
    const table = document.querySelector(selector);
    if (!table) return;
    table.addEventListener("keydown", function (e) {
      if (e.key === "Delete" || e.key === "Backspace") {
        const sel = window.getSelection();
        if (!sel.rangeCount) return;
        const td = sel.getRangeAt(0).startContainer.closest("td");
        if (td) td.closest("tr")?.remove();
      }
    });
  }

  // 📘 요약 테이블
  const summaryTable = document.querySelector(".attendance-table");
  summaryTable?.querySelectorAll("td[contenteditable=true]").forEach(attachPasteHandler);

  const summaryBtnWrap = document.createElement("div");
  summaryBtnWrap.style.display = "flex";
  summaryBtnWrap.style.justifyContent = "flex-end";
  summaryBtnWrap.style.gap = "10px";
  summaryBtnWrap.style.marginBottom = "10px";

  const saveBtn = document.createElement("button");
  saveBtn.textContent = "💾 저장";
  saveBtn.style = baseBtnStyle;

  const addBtn = document.createElement("button");
  addBtn.textContent = "➕ 행 추가";
  addBtn.style = baseBtnStyle;

  summaryBtnWrap.appendChild(saveBtn);
  summaryBtnWrap.appendChild(addBtn);
  summaryTable?.parentElement.insertBefore(summaryBtnWrap, summaryTable);

  saveBtn.addEventListener("click", function () {
    const table = summaryTable.querySelector("tbody");
    const data = [];

    Array.from(table.rows).forEach(row => {
      const cells = row.querySelectorAll("td");
      const name = cells[0]?.innerText.trim();
      if (!name) return;
      data.push({
        name,
        joinDate: normalizeDate(cells[1]?.innerText.trim()),
        leaveDate: normalizeDate(cells[2]?.innerText.trim()),
        annualGiven: parseFloat(cells[3]?.innerText) || 0,
        annualUsed: parseFloat(cells[4]?.innerText) || 0,
        dayoffGiven: parseFloat(cells[6]?.innerText) || 0,
        dayoffUsed: parseFloat(cells[7]?.innerText) || 0
      });
    });

    fetch("/api/attendance/save-summary", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data)
    })
      .then(res => res.text())
      .then(alert)
      .then(() => location.reload())
      .catch(err => alert("❌ 저장 실패: " + err.message));
  });

  addBtn.addEventListener("click", function () {
    const tbody = summaryTable.querySelector("tbody");
    const row = document.createElement("tr");
    row.innerHTML = summaryRowTemplate();
    tbody.appendChild(row);
    row.querySelectorAll("td[contenteditable=true]").forEach(attachPasteHandler);
  });

  enableRowDelete(".attendance-table");

  // 📘 상세 내역 테이블
  const detailTable = document.querySelector(".data-table");
  const detailTbody = detailTable?.querySelector("tbody");

  if (detailTable) {
    const detailBtnWrap = document.createElement("div");
    detailBtnWrap.style.display = "flex";
    detailBtnWrap.style.justifyContent = "flex-end";
    detailBtnWrap.style.gap = "10px";
    detailBtnWrap.style.margin = "20px 0";

    const saveDetailBtn = document.createElement("button");
    saveDetailBtn.textContent = "💾 상세 저장";
    saveDetailBtn.style = baseBtnStyle;

    const addDetailBtn = document.createElement("button");
    addDetailBtn.textContent = "➕ 상세 추가";
    addDetailBtn.style = baseBtnStyle;

    detailBtnWrap.appendChild(saveDetailBtn);
    detailBtnWrap.appendChild(addDetailBtn);
    detailTable.parentElement.insertBefore(detailBtnWrap, detailTable);

    saveDetailBtn.addEventListener("click", function () {
      const data = [];

      Array.from(detailTbody.rows).forEach(row => {
        const cells = row.querySelectorAll("td");
        const name = cells[0]?.innerText.trim();
        const type = cells[1]?.innerText.trim();
        const start = normalizeDate(cells[2]?.innerText.trim());
        const end = normalizeDate(cells[3]?.innerText.trim());
        const days = parseFloat(cells[4]?.innerText.trim()) || 0;
        const reason = cells[5]?.innerText.trim();

        if (!name || !type || !start || !end) return;
        data.push({ name, type, startDate: start, endDate: end, days, reason });
      });

      fetch("/api/attendance/save-detail", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
      })
        .then(res => res.text())
        .then(alert)
        .then(() => location.reload())
        .catch(err => alert("❌ 상세 저장 실패: " + err.message));
    });

    addDetailBtn.addEventListener("click", function () {
      const row = document.createElement("tr");
      row.innerHTML = detailRowTemplate();
      detailTbody.appendChild(row);
      row.querySelectorAll("td[contenteditable=true]").forEach(attachPasteHandler);
    });

    detailTbody.querySelectorAll("td[contenteditable=true]").forEach(attachPasteHandler);
    enableRowDelete(".data-table");
  }

  document.querySelectorAll(".attendance-table td").forEach(td => {
    const value = parseFloat(td.textContent);
    if (!isNaN(value) && td.previousElementSibling?.innerText !== "사용") {
      td.textContent = value.toFixed(1);
    }
  });
});
