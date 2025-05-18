document.addEventListener("DOMContentLoaded", function () {
    const serviceTableBody = document.getElementById("serviceTableBody");
    const modal = document.getElementById("serviceModal");
    const modalTitle = document.getElementById("modalTitle");
    const closeBtn = document.querySelector("#serviceModal .close");
    const form = document.getElementById("serviceForm");
    const addServiceBtn = document.getElementById("addServiceBtn");

    const confirmDeleteModal = document.getElementById("confirmDeleteModal");
    const confirmDeleteBtn = document.getElementById("confirmDeleteBtn");
    const cancelDeleteBtn = document.getElementById("cancelDeleteBtn");

    let deleteId = null;

    function fetchServices() {
        fetch("/api/internal-services")
            .then(res => res.json())
            .then(data => renderTable(data));
    }

    function renderTable(data) {
        serviceTableBody.innerHTML = "";
        data.forEach(service => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${service.name}</td>
                <td>${service.description || ""}</td>
                <td><a href="${service.url}" target="_blank">${service.url}</a></td>
                <td>${service.manager}</td>
                <td>${formatDate(service.createdAt)}</td>
                <td><button class="edit-btn" data-id="${service.id}">수정</button></td>
                <td><button class="delete-btn" data-id="${service.id}">삭제</button></td>
            `;
            serviceTableBody.appendChild(row);
        });
    }

    function formatDate(dateStr) {
        const date = new Date(dateStr);
        return date.toISOString().slice(0, 10);
    }

    addServiceBtn.addEventListener("click", () => {
        modalTitle.textContent = "서비스 등록";
        form.reset();
        document.getElementById("serviceId").value = "";
        modal.classList.remove("hidden");
    });

    closeBtn.addEventListener("click", () => {
        modal.classList.add("hidden");
    });

    serviceTableBody.addEventListener("click", (e) => {
        if (e.target.classList.contains("edit-btn")) {
            const id = e.target.dataset.id;
            fetch(`/api/internal-services/${id}`)
                .then(res => res.json())
                .then(service => {
                    modalTitle.textContent = "서비스 수정";
                    document.getElementById("serviceId").value = service.id;
                    document.getElementById("name").value = service.name;
                    document.getElementById("description").value = service.description || "";
                    document.getElementById("url").value = service.url;
                    document.getElementById("manager").value = service.manager;
                    modal.classList.remove("hidden");
                });
        }

        if (e.target.classList.contains("delete-btn")) {
            deleteId = e.target.dataset.id;
            confirmDeleteModal.classList.remove("hidden");
        }
    });

    cancelDeleteBtn.addEventListener("click", () => {
        deleteId = null;
        confirmDeleteModal.classList.add("hidden");
    });

    confirmDeleteBtn.addEventListener("click", () => {
        if (!deleteId) return;
        fetch(`/api/internal-services/${deleteId}`, { method: "DELETE" })
            .then(() => {
                deleteId = null;
                confirmDeleteModal.classList.add("hidden");
                fetchServices();
            });
    });

    form.addEventListener("submit", function (e) {
        e.preventDefault();
        const payload = {
            name: document.getElementById("name").value,
            description: document.getElementById("description").value,
            url: document.getElementById("url").value,
            manager: document.getElementById("manager").value
        };
        const id = document.getElementById("serviceId").value;
        const method = id ? "PUT" : "POST";
        const url = id ? `/api/internal-services/${id}` : "/api/internal-services";

        fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        })
            .then(() => {
                modal.classList.add("hidden");
                fetchServices();
            });
    });

    fetchServices();
});
