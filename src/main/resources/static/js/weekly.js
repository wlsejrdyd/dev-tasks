document.addEventListener("DOMContentLoaded", function () {
  const yearSelect = document.getElementById("year-select");
  const monthSelect = document.getElementById("month-select");
  const weekSelect = document.getElementById("week-select");
  const loadBtn = document.getElementById("load-btn");
  const saveBtn = document.getElementById("save-btn");
  const addRowBtn = document.getElementById("add-row-btn");
  const tbody = document.getElementById("report-body");

  let categoryList = [];

  // 카테고리 목록 불러오기
  fetch("/api/weekly/categories")
    .then((res) => res.json())
    .then((data) => {
      categoryList = data;
    })
    .catch(() => {
      alert("카테고리 불러오기 실패");
      categoryList = [{ name: "기타" }];
    });

  const now = new Date();
  for (let y = now.getFullYear(); y >= now.getFullYear() - 3; y--) {
    yearSelect.innerHTML += `<option value="${y}">${y}년</option>`;
  }
  for (let m = 1; m <= 12; m++) {
    monthSelect.innerHTML += `<option value="${String(m).padStart(2, "0")}">${m}월</option>`;
  }
  for (let w = 1; w <= 5; w++) {
    weekSelect.innerHTML += `<option value="${w}">${w}주차</option>`;
  }

  yearSelect.value = now.getFullYear();
  monthSelect.value = String(now.getMonth() + 1).padStart(2, "0");
  weekSelect.value = 1;

  loadBtn.addEventListener("click", function () {
    const year = yearSelect.value;
    const month = monthSelect.value;
    const week = weekSelect.value;

    fetch(`/api/weekly?year=${year}&month=${month}&week=${week}`)
      .then((res) => res.json())
      .then((data) => renderTable(data))
      .catch(() => alert("불러오기 실패"));
  });

  saveBtn.addEventListener("click", function () {
    const rows = tbody.querySelectorAll("tr");
    const year = yearSelect.value;
    const month = monthSelect.value;
    const week = weekSelect.value;

    const reports = Array.from(rows).map((row) => {
      const date = row.querySelector("input.date")?.value.trim();
      const category = row.querySelector("select.category")?.value.trim();
      const title = row.querySelector("td.title")?.innerText.trim();
      const content = row.querySelector("td.content")?.innerText.trim();
      return { date, category, title, content };
    });

    fetch("/api/weekly?year=" + year + "&month=" + month + "&week=" + week, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(reports),
    })
      .then((res) => {
        if (res.ok) alert("저장 완료");
        else throw new Error();
      })
      .catch(() => alert("저장 실패"));
  });

  addRowBtn.addEventListener("click", function () {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td><input type="date" class="date" style="width:100%; border: none;"></td>
      <td>${renderCategorySelect()}</td>
      <td class="title" contenteditable="true"></td>
      <td class="content" contenteditable="true"></td>
    `;
    tbody.appendChild(tr);
  });

  function renderTable(data) {
    tbody.innerHTML = "";
    data.forEach((item) => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td><input type="date" class="date" value="${item.date || ""}" style="width:100%; border: none;"></td>
        <td>${renderCategorySelect(item.category)}</td>
        <td class="title" contenteditable="true">${item.title || ""}</td>
        <td class="content" contenteditable="true">${item.content || ""}</td>
      `;
      tbody.appendChild(tr);
    });
  }

  function renderCategorySelect(selectedValue = "") {
    const options = categoryList
      .map(
        (cat) =>
          `<option value="${cat.name}" ${
            cat.name === selectedValue ? "selected" : ""
          }>${cat.name}</option>`
      )
      .join("");
    return `<select class="category" style="width:100%; border:none; background:none;">${options}</select>`;
  }
});
