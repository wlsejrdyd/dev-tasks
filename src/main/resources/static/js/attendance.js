document.addEventListener("DOMContentLoaded", function () {

  /** ----------- 공통 스타일 ------------ **/
  const baseBtnStyle = `
    padding: 6px 14px;
    font-size: 14px;
    border: 1px solid #888;
    border-radius: 4px;
    background-color: #f4f4f4;
    cursor: pointer;
  `;

  /** ----------- 요약 테이블 기능 ------------ **/

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
        if (!targetRow) {
          const newRow = document.createElement("tr");
          newRow.innerHTML = newRowTemplate();
          tbody.appendChild(newRow);
          targetRow = newRow;
          targetRow.querySelectorAll("td[contenteditable=true]").forEach(attachPasteHandler);
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

  function newRowTemplate() {
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

  const summaryTable = document.querySelector(".attendance-table");
  summaryTable.querySelectorAll("td[contenteditable=true]").forEach(attachPasteHandler);

  const btnWrap = document.createElement("div");
  btnWrap.style.display = "flex";
  btnWrap.style.justifyContent = "flex-end";
  btnWrap.style.gap = "10px";
  btnWrap.style.marginBottom = "10px";

  const saveBtn = document.createElement("button");
  saveBtn.textContent = "💾 저장";
  saveBtn.style = baseBtnStyle;

  const addRowBtn = document.createElement("button");
  addRowBtn.textContent = "➕ 행 추가";
  addRowBtn.style = baseBtnStyle;

  btnWrap.appendChild(saveBtn);
  btnWrap.appendChild(addRowBtn);
  summaryTable.parentElement.insertBefore(btnWrap, summaryTable);

  saveBtn.addEventListener("click", function () {
    const table = summaryTable.querySelector("tbody");
    const data = [];
    Array.from(table.rows).forEach(row => {
      const cells = row.querySelectorAll("td");
      const name = cells[0]?.innerText.trim();
      if (!name) return;
      data.push({
        name,
        joinDate: cells[1]?.innerText.trim() || "",
        leaveDate: cells[2]?.innerText.trim() || "",
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
      .then(msg => {
        alert(msg);
        location.reload();
      })
      .catch(err => alert("❌ 저장 오류: " + err.message));
  });

  addRowBtn.addEventListener("click", function () {
    const tbody = summaryTable.querySelector("tbody");
    const newRow = document.createElement("tr");
    newRow.innerHTML = newRowTemplate();
    tbody.appendChild(newRow);
    newRow.querySelectorAll("td[contenteditable=true]").forEach(attachPasteHandler);
  });

  /** ----------- 상세 내역 테이블 기능 ------------ **/

  const detailTable = document.querySelector(".data-table");
  if (detailTable) {
    const detailTbody = detailTable.querySelector("tbody");

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

    addDetailBtn.addEventListener("click", function () {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td contenteditable="true"></td>
        <td contenteditable="true">대휴 부여</td>
        <td contenteditable="true">2025-01-01</td>
        <td contenteditable="true">2025-01-01</td>
        <td contenteditable="true">1</td>
        <td contenteditable="true"></td>
      `;
      detailTbody.appendChild(row);
    });

    saveDetailBtn.addEventListener("click", function () {
      const data = [];
      Array.from(detailTbody.rows).forEach(row => {
        const cells = row.querySelectorAll("td");
        const name = cells[0]?.innerText.trim();
        const type = cells[1]?.innerText.trim();
        const start = cells[2]?.innerText.trim();
        const end = cells[3]?.innerText.trim();
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
        .then(msg => {
          alert(msg);
          location.reload();
        })
        .catch(err => alert("❌ 상세 저장 오류: " + err.message));
    });
  }

  // 소수점 자릿수 포맷
  document.querySelectorAll(".attendance-table td").forEach(td => {
    const value = parseFloat(td.textContent);
    if (!isNaN(value)) {
      if (td.previousElementSibling?.innerText === "사용") return;
      if (value % 1 !== 0) td.textContent = value.toFixed(1);
    }
  });
});
