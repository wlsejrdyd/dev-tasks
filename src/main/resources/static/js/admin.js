document.addEventListener("DOMContentLoaded", function () {
  const tableBody = document.querySelector("#adminUserTableBody");

  function fetchUsers() {
    fetch("/api/admin/users")
      .then((res) => res.json())
      .then((users) => {
        tableBody.innerHTML = "";
        users.forEach((user) => {
          const tr = document.createElement("tr");
          tr.innerHTML = `
            <td>${user.id}</td>
            <td>${user.name}</td>
            <td>${user.email}</td>
            <td>
              <select data-id="${user.id}" class="role-select">
                <option value="USER" ${user.role === "USER" ? "selected" : ""}>USER</option>
                <option value="ADMIN" ${user.role === "ADMIN" ? "selected" : ""}>ADMIN</option>
                <option value="GUEST" ${user.role === "GUEST" ? "selected" : ""}>GUEST</option>
              </select>
            </td>
            <td><input type="date" class="join-date" data-id="${user.id}" /></td>
            <td><input type="date" class="leave-date" data-id="${user.id}" /></td>
            <td><input type="number" class="annual-granted" data-id="${user.id}" step="0.5" min="0" /></td>
            <td>
              <button class="btn-update-status" data-id="${user.id}">💾</button>
              <button class="btn-delete" data-id="${user.id}">🗑</button>
            </td>
          `;
          tableBody.appendChild(tr);
        });

        attachEventHandlers();
        preloadStatusData();
      });
  }

  function preloadStatusData() {
    fetch("/api/attendance/status")
      .then((res) => res.json())
      .then((statuses) => {
        statuses.forEach((status) => {
          const id = status.userId;
          const joinInput = document.querySelector(`.join-date[data-id="${id}"]`);
          const leaveInput = document.querySelector(`.leave-date[data-id="${id}"]`);
          const grantedInput = document.querySelector(`.annual-granted[data-id="${id}"]`);

          if (joinInput && status.joinDate) joinInput.value = status.joinDate;
          if (leaveInput && status.leaveDate) leaveInput.value = status.leaveDate;
          if (grantedInput && status.annualGranted != null) grantedInput.value = status.annualGranted;
        });
      });
  }

  function attachEventHandlers() {
    document.querySelectorAll(".role-select").forEach((select) => {
      select.addEventListener("change", function () {
        const userId = this.dataset.id;
        const role = this.value;
        fetch(`/api/admin/users/${userId}/role?role=${role}`, {
          method: "PUT",
        }).then(() => alert("권한이 변경되었습니다."));
      });
    });

    document.querySelectorAll(".btn-delete").forEach((btn) => {
      btn.addEventListener("click", async function () {
        const userId = this.dataset.id;
        if (!confirm("정말로 삭제하시겠습니까?")) return;

        try {
          const res = await fetch(`/api/admin/users/${userId}`, { method: "DELETE" });

          if (res.ok) {
            alert("삭제 완료");
            fetchUsers();
          } else {
            alert("삭제 실패");
          }
        } catch (e) {
          alert("삭제 중 오류 발생");
        }
      });
    });

    document.querySelectorAll(".btn-update-status").forEach((btn) => {
      btn.addEventListener("click", function () {
        const userId = this.dataset.id;
        const joinDate = document.querySelector(`.join-date[data-id="${userId}"]`)?.value;
        const leaveDate = document.querySelector(`.leave-date[data-id="${userId}"]`)?.value;
        const annualGranted = document.querySelector(`.annual-granted[data-id="${userId}"]`)?.value;

        fetch("/api/admin/attendance-status", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ userId, joinDate, leaveDate, annualGranted }),
        }).then((res) => {
          if (res.ok) alert("근태 상태가 저장되었습니다.");
          else alert("저장 실패");
        });
      });
    });
  }

  // 📁 카테고리 관리
  const categoryList = document.getElementById("category-list");
  const newCategoryInput = document.getElementById("new-category");
  const addCategoryBtn = document.getElementById("add-category-btn");

  function loadCategories() {
    fetch("/api/weekly/categories")
      .then((res) => res.json())
      .then((data) => {
        categoryList.innerHTML = "";
        data.forEach((cat) => {
          const li = document.createElement("li");
          li.textContent = cat.name + " ";
          const delBtn = document.createElement("button");
          delBtn.textContent = "삭제";
          delBtn.addEventListener("click", () => deleteCategory(cat.id));
          li.appendChild(delBtn);
          categoryList.appendChild(li);
        });
      });
  }

  function deleteCategory(id) {
    fetch(`/api/admin/categories/${id}`, { method: "DELETE" })
      .then(() => loadCategories());
  }

  addCategoryBtn.addEventListener("click", function () {
    const name = newCategoryInput.value.trim();
    if (!name) return alert("카테고리명을 입력하세요.");
    fetch("/api/admin/categories", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name }),
    }).then(() => {
      newCategoryInput.value = "";
      loadCategories();
    });
  });

  // 📢 공지사항 등록
  const noticeBtn = document.getElementById("notice-submit-btn");
  noticeBtn.addEventListener("click", function () {
    const title = document.getElementById("notice-title").value.trim();
    const content = document.getElementById("notice-content").value.trim();
    const pinned = document.getElementById("notice-pinned").checked;
    if (!title || !content) return alert("제목과 내용을 입력하세요.");

    fetch("/api/admin/notices", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ title, content, pinned }),
    }).then(() => {
      alert("공지 등록 완료");
      document.getElementById("notice-title").value = "";
      document.getElementById("notice-content").value = "";
      document.getElementById("notice-pinned").checked = false;
    });
  });

  // 🗳 투표 등록
  const pollBtn = document.getElementById("poll-submit-btn");
  const addPollOptionBtn = document.getElementById("add-poll-option-btn");

  addPollOptionBtn.addEventListener("click", function () {
    const opt = document.createElement("input");
    opt.type = "text";
    opt.className = "poll-option";
    opt.placeholder = `선택지 ${document.querySelectorAll(".poll-option").length + 1}`;
    document.getElementById("poll-options").appendChild(opt);
    document.getElementById("poll-options").appendChild(document.createElement("br"));
  });

  pollBtn.addEventListener("click", function () {
    const question = document.getElementById("poll-question").value.trim();
    const options = Array.from(document.querySelectorAll(".poll-option"))
      .map(input => input.value.trim()).filter(text => text);

    if (!question || options.length < 2) {
      return alert("질문과 최소 2개 이상의 선택지를 입력하세요.");
    }

    fetch("/api/admin/poll", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ question, options }),
    }).then(() => {
      alert("투표 등록 완료");
      document.getElementById("poll-question").value = "";
      document.getElementById("poll-options").innerHTML = `
        <input type="text" class="poll-option" placeholder="선택지 1"><br>
        <input type="text" class="poll-option" placeholder="선택지 2"><br>
      `;
    });
  });

  // ✅ 탭 전환 기능
  const tabs = document.querySelectorAll(".tab-button");
  const contents = document.querySelectorAll(".tab-content");

  tabs.forEach((btn) => {
    btn.addEventListener("click", () => {
      tabs.forEach((b) => b.classList.remove("active"));
      contents.forEach((c) => c.classList.remove("active"));

      btn.classList.add("active");
      document.getElementById(btn.dataset.tab).classList.add("active");
    });
  });

  fetchUsers();
  loadCategories();
});
