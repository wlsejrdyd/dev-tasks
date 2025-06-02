document.addEventListener("DOMContentLoaded", function () {
  const yearSelect = document.getElementById("year-select");
  const monthSelect = document.getElementById("month-select");
  const weekSelect = document.getElementById("week-select");
  const categoryFilter = document.getElementById("category-filter");
  const loadBtn = document.getElementById("load-btn");
  const saveBtn = document.getElementById("save-btn");
  const addRowBtn = document.getElementById("add-row-btn");
  const tableBody = document.getElementById("weekly-body");

  function fillCategoryFilter() {
    fetch("/api/weekly/categories")
      .then(res => res.json())
      .then(data => {
        categoryFilter.innerHTML = `<option value="all">전체 카테고리</option>`;
        data.forEach(cat => {
          const opt = document.createElement("option");
          opt.value = cat.name;
          opt.textContent = cat.name;
          categoryFilter.appendChild(opt);
        });
      });
  }

  function loadReports() {
    const year = yearSelect.value;
    const month = monthSelect.value;
    const week = weekSelect.value;
    const category = categoryFilter.value;

    fetch(`/api/weekly/reports?year=${year}&month=${month}&week=${week}`)
      .then(res => res.json())
      .then(data => {
        tableBody.innerHTML = "";
        data.forEach(row => {
          if (category !== "all" && row.category !== category) return;
          const tr = document.createElement("tr");
          tr.innerHTML = `
            <td><input type="date" value="${row.date}" class="report-date"></td>
            <td><input type="text" value="${row.category}" class="report-category"></td>
            <td><input type="text" value="${row.title}" class="report-title"></td>
            <td><input type="text" value="${row.content}" class="report-content" style="width: 100%"></td>
          `;
          tableBody.appendChild(tr);
        });
      });
  }

  function saveReports() {
    const year = yearSelect.value;
    const month = monthSelect.value;
    const week = weekSelect.value;
    const rows = tableBody.querySelectorAll("tr");

    const data = Array.from(rows).map(tr => {
      return {
        date: tr.querySelector(".report-date").value,
        category: tr.querySelector(".report-category").value,
        title: tr.querySelector(".report-title").value,
        content: tr.querySelector(".report-content").value,
      };
    });

    fetch("/api/weekly/reports", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ year, month, week, reports: data }),
    }).then(() => {
      alert("저장 완료");
      loadReports();
    });
  }

  function initSelects() {
    const now = new Date();
    for (let y = now.getFullYear() - 1; y <= now.getFullYear(); y++) {
      const opt = document.createElement("option");
      opt.value = y;
      opt.textContent = `${y}년`;
      yearSelect.appendChild(opt);
    }
    for (let m = 1; m <= 12; m++) {
      const opt = document.createElement("option");
      opt.value = m;
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
    monthSelect.value = now.getMonth() + 1;
    weekSelect.value = 1;
  }

  loadBtn.addEventListener("click", loadReports);
  saveBtn.addEventListener("click", saveReports);
  addRowBtn.addEventListener("click", () => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td><input type="date" class="report-date"></td>
      <td><input type="text" class="report-category"></td>
      <td><input type="text" class="report-title"></td>
      <td><input type="text" class="report-content" style="width: 100%"></td>
    `;
    tableBody.appendChild(tr);
  });

  initSelects();
  fillCategoryFilter();
  loadReports();
});
