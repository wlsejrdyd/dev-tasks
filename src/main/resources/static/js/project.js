document.addEventListener("DOMContentLoaded", () => {
  console.log("📁 project.js loaded");

  const params = new URLSearchParams(window.location.search);
  const keyword = params.get("keyword");

  if (keyword) {
    const cards = document.querySelectorAll(".card");
    cards.forEach(card => {
      const title = card.querySelector(".card-header h3");
      if (title && title.textContent.includes(keyword)) {
        card.style.border = "2px solid #ff9800";
        card.style.backgroundColor = "#fff8e1";
      }
    });
  }

  window.openModal = () => {
    resetModalForm();
    document.getElementById("projectModal").style.display = "flex";
  };

  window.loadProject = (id) => {
    fetch(`/projects/api/${id}`)
      .then(res => res.json())
      .then(data => {
        document.getElementById("projectModal").style.display = "flex";
        document.querySelector("#projectModal form").action = "/projects/update";
        document.querySelector("#projectModal input[name='id']").value = data.id;
        document.querySelector("#projectModal input[name='name']").value = data.name || '';
        document.querySelector("#projectModal textarea[name='description']").value = data.description || '';
        document.querySelector("#projectModal input[name='startDate']").value = data.startDate || '';
        document.querySelector("#projectModal input[name='endDate']").value = data.endDate || '';
        document.querySelector("#projectModal select[name='status']").value = data.status || '진행중';
        document.querySelector("#projectModal input[name='requestDept']").value = data.requestDept || '';
        document.querySelector("#projectModal input[name='requester']").value = data.requester || '';
        document.querySelector("#projectModal input[name='ipRequested']").checked = data.ipRequested;
        document.querySelector("#projectModal input[name='firewallRequested']").checked = data.firewallRequested;
        document.querySelector("#projectModal input[name='vmRequested']").checked = data.vmRequested;
        document.querySelector("#projectModal input[name='serverSetupRequested']").checked = data.serverSetupRequested;
      });
  };

  window.closeModal = () => {
    document.getElementById("projectModal").style.display = "none";
  };

  function resetModalForm() {
    const form = document.querySelector("#projectModal form");
    form.reset();
    form.action = "/projects/save";
    form.querySelector("input[name='id']").value = '';
  }

  window.deleteProjectFile = (el) => {
    const fileId = el.getAttribute("data-id");
    if (!fileId || !confirm("삭제할까요?")) return;

    fetch(`/projects/file/delete/${fileId}`, {
      method: "DELETE"
    }).then(res => {
      if (res.ok) location.reload();
      else alert("삭제 실패");
    });
  };

  window.toggleFileList = (btn) => {
    const projectId = btn.getAttribute("data-id");
    const fileList = document.querySelector(`.project-files[data-id='${projectId}']`);
    if (fileList) {
      fileList.style.display = (fileList.style.display === "none" || !fileList.style.display) ? "block" : "none";
    }
  };
});
