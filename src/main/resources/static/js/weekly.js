document.addEventListener("DOMContentLoaded", function () {
  const yearSelect = document.getElementById("year-select");
  const monthSelect = document.getElementById("month-select");
  const weekSelect = document.getElementById("week-select");
  const categoryFilter = document.getElementById("category-filter");
  const loadBtn = document.getElementById("load-btn");
  const saveBtn = document.getElementById("save-btn");
  const addRowBtn = document.getElementById("add-row-btn");
  const tableBody = document.getElementById("weekly-body");

  let categoryList = [];

  function fillCategoryFilter() {
    fetch("/api/weekly/categories")
      .then(res => res.json())
      .then(data => {
        categoryList = data;
        categoryFilter.innerHTML = `<option value="all">전체 카테고리</option>`;
        data.forEach(cat => {
          const opt = document.createElement("option");
          opt.value = cat.id;
          opt.textContent = cat.name;
          categoryFilter.appendChild(opt);
        });
      });
  }

  function createCategorySelect(selectedId = null) {
    const select = document.createElement("select");
    select.className = "report-category";
    categoryList.forEach(cat => {
      const opt = document.createElement("option");
      opt.value = cat.id;
      opt.textContent = cat.name;
      if (selectedId && Number(selectedId) === cat.id) {
        opt.selected = true;
      }
      select.appendChild(opt);
    });
    return select;
  }

  function loadReports() {
    const year = yearSelect.value;
    const month = monthSelect.value;
    const week = weekSelect.value;
    const categoryId = categoryFilter.value;

    fetch(`/api/weekly/reports?year=${year}&month=${month}&week=${week}`)
      .then(res => res.json())
      .then(data => {
        tableBody.innerHTML = "";
        data.forEach(row => {
          if (categoryId !== "all" && row.categoryId != categoryId) return;
          const tr = document.createElement("tr");
          const categorySelect = createCategorySelect(row.categoryId);
          tr.innerHTML = `
            <td><input type="date" value="${row.date}" class="report-date"></td>
            <td></td>
            <td><input type="text" value="${row.title}" class="report-title"></td>
            <td><input type="text" value="${row.content}" class="report-content" style="width: 100%"></td>
          `;
          tr.children[1].appendChild(categorySelect);
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
        categoryId: Number(tr.querySelector(".report-category").value),
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
    const categorySelect = createCategorySelect();
    tr.innerHTML = `
      <td><input type="date" class="report-date"></td>
      <td></td>
      <td><input type="text" class="report-title"></td>
      <td><input type="text" class="report-content" style="width: 100%"></td>
    `;
    tr.children[1].appendChild(categorySelect);
    tableBody.appendChild(tr);
  });

  initSelects();
  fillCategoryFilter();
  setTimeout(loadReports, 300); // 카테고리 로드 기다리기
});
