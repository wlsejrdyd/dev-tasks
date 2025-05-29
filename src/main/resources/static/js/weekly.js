document.addEventListener("DOMContentLoaded", function () {
  const yearSelect = document.getElementById("year-select");
  const monthSelect = document.getElementById("month-select");
  const weekSelect = document.getElementById("week-select");
  const loadBtn = document.getElementById("load-btn");
  const saveBtn = document.getElementById("save-btn");
  const tbody = document.getElementById("report-body");

  // 연도/월/주차 옵션 초기화
  const now = new Date();
  for (let y = now.getFullYear(); y >= now.getFullYear() - 3; y--) {
    const opt = document.createElement("option");
    opt.value = y;
    opt.textContent = `${y}년`;
    yearSelect.appendChild(opt);
  }

  for (let m = 1; m <= 12; m++) {
    const opt = document.createElement("option");
    opt.value = m.toString().padStart(2, "0");
    opt.textContent = `${m}월`;
    monthSelect.appendChild(opt);
  }

  for (let w = 1; w <= 5; w++) {
    const opt = document.createElement("option");
    opt.value = w;
    opt.textContent = `${w}주차`;
    weekSelect.appendChild(opt);
  }

  yearSelect.value = now.getFullYear();
  monthSelect.value = String(now.getMonth() + 1).padStart(2, "0");
  weekSelect.value = 1;

  // 불러오기 버튼
  loadBtn.addEventListener("click", function () {
    const year = yearSelect.value;
    const month = monthSelect.value;
    const week = weekSelect.value;

    fetch(`/weekly?year=${year}&month=${month}&week=${week}`)
      .then((res) => res.json())
      .then((data) => renderTable(data))
      .catch((err) => alert("불러오기 실패"));
  });

  // 저장 버튼
  saveBtn.addEventListener("click", function () {
    const rows = tbody.querySelectorAll("tr");
    const year = yearSelect.value;
    const month = monthSelect.value;
    const week = weekSelect.value;

    const reports = Array.from(rows).map((row) => {
      const cells = row.querySelectorAll("td");
      return {
        date: cells[0]?.textContent.trim(),
        category: cells[1]?.textContent.trim(),
        title: cells[2]?.textContent.trim(),
        content: cells[3]?.textContent.trim(),
      };
    });

    fetch("/weekly", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        year,
        month,
        week,
        reports,
      }),
    })
      .then((res) => {
        if (res.ok) alert("저장 완료");
        else throw new Error();
      })
      .catch(() => alert("저장 실패"));
  });

  function renderTable(data) {
    tbody.innerHTML = "";
    data.forEach((item) => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${item.date || ""}</td>
        <td>${item.category || ""}</td>
        <td>${item.title || ""}</td>
        <td>${item.content || ""}</td>
      `;
      tbody.appendChild(tr);
    });
  }
});
