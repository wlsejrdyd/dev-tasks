document.addEventListener("DOMContentLoaded", function () {

  // ✅ 붙여넣기 핸들러 (재사용 가능)
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

        // 🟡 부족한 행 자동 추가
        if (!targetRow) {
          const newRow = document.createElement("tr");
          newRow.innerHTML = `
            <td contenteditable="true"></td>
            <td contenteditable="true"></td>
            <td contenteditable="true">-</td>
            <td contenteditable="true">17</td>
            <td contenteditable="true">0</td>
            <td>17</td>
            <td contenteditable="true">0</td>
            <td contenteditable="true">0</td>
            <td>0</td>
            <td>17</td>
            <td>0</td>
            <td>17</td>
          `;
          tbody.appendChild(newRow);
          targetRow = newRow;

          // 🔄 새로 추가된 셀에도 붙여넣기 핸들러 연결
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

  // ✅ 초기에 붙여넣기 이벤트 연결
  document.querySelectorAll("td[contenteditable=true]").forEach(attachPasteHandler);

  // ✅ 저장 버튼
  const saveBtn = document.createElement("button");
  saveBtn.textContent = "💾 저장";
  saveBtn.style.marginTop = "20px";
  saveBtn.style.marginRight = "10px";
  document.querySelector(".main-content").appendChild(saveBtn);

  saveBtn.addEventListener("click", function () {
    const table = document.querySelector(".attendance-table tbody");
    const data = [];

    Array.from(table.rows).forEach((row, index) => {
      const cells = row.querySelectorAll("td");
      const name = cells[0]?.innerText.trim();

      if (!name) {
        console.warn(`❗ ${index + 1}번째 행은 이름이 비어 있어 건너뜀`);
        return;
      }

      data.push({
        name: name,
        joinDate: cells[1]?.innerText.trim() || "",
        leaveDate: cells[2]?.innerText.trim() || "",
        annualGiven: parseFloat(cells[3]?.innerText) || 0,
        annualUsed: parseFloat(cells[4]?.innerText) || 0,
        dayoffGiven: parseFloat(cells[6]?.innerText) || 0,
        dayoffUsed: parseFloat(cells[7]?.innerText) || 0
      });
    });

    if (data.length === 0) {
      alert("저장할 데이터가 없습니다.");
      return;
    }

    fetch("/api/attendance/save-summary", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data)
    })
      .then(res => {
        if (!res.ok) throw new Error("저장 실패");
        return res.text();
      })
      .then(() => alert("✅ 저장 완료"))
      .catch(err => alert("❌ 저장 오류: " + err.message));
  });

  // ✅ 행 추가 버튼
  const addRowBtn = document.createElement("button");
  addRowBtn.textContent = "➕ 행 추가";
  addRowBtn.style.marginTop = "20px";
  document.querySelector(".main-content").appendChild(addRowBtn);

  addRowBtn.addEventListener("click", function () {
    const tbody = document.querySelector(".attendance-table tbody");
    const newRow = document.createElement("tr");

    newRow.innerHTML = `
      <td contenteditable="true">이름</td>
      <td contenteditable="true">YYYY.MM.DD</td>
      <td contenteditable="true">-</td>
      <td contenteditable="true">17</td>
      <td contenteditable="true">0</td>
      <td>17</td>
      <td contenteditable="true">0</td>
      <td contenteditable="true">0</td>
      <td>0</td>
      <td>17</td>
      <td>0</td>
      <td>17</td>
    `;

    tbody.appendChild(newRow);
    newRow.querySelectorAll("td[contenteditable=true]").forEach(attachPasteHandler); // 🔄 붙여넣기 이벤트 연결
  });
});
