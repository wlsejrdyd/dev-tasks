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
      btn.addEventListener("click", function () {
        const userId = this.dataset.id;
        if (!confirm("정말로 삭제하시겠습니까?")) return;

        fetch(`/api/admin/users/${userId}`, {
          method: "DELETE",
        }).then((res) => {
          if (res.ok) {
            alert("삭제 완료");
            fetchUsers();
          } else {
            alert("삭제 실패");
          }
        });
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
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            userId,
            joinDate,
            leaveDate,
            annualGranted,
          }),
        }).then((res) => {
          if (res.ok) {
            alert("근태 상태가 저장되었습니다.");
          } else {
            alert("저장 실패");
          }
        });
      });
    });
  }

  fetchUsers();
});
