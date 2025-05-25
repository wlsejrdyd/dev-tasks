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
              </select>
            </td>
            <td>
              <button class="btn-delete" data-id="${user.id}">삭제</button>
            </td>
          `;
          tableBody.appendChild(tr);
        });

        attachEventHandlers();
      });
  }

  function attachEventHandlers() {
    // 권한 변경
    document.querySelectorAll(".role-select").forEach((select) => {
      select.addEventListener("change", function () {
        const userId = this.dataset.id;
        const role = this.value;

        fetch(`/api/admin/users/${userId}/role?role=${role}`, {
          method: "PUT",
        }).then(() => alert("권한이 변경되었습니다."));
      });
    });

    // 사용자 삭제
    document.querySelectorAll(".btn-delete").forEach((btn) => {
      btn.addEventListener("click", function () {
        const userId = this.dataset.id;
        if (!confirm("정말로 삭제하시겠습니까?")) return;

        fetch(`/api/admin/users/${userId}`, {
          method: "DELETE",
        })
          .then((res) => {
            if (res.ok) {
              alert("삭제 완료");
              fetchUsers();
            } else {
              alert("삭제 실패");
            }
          });
      });
    });
  }

  fetchUsers();
});
