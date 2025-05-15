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

  // 등록용 모달 열기
  window.openModal = () => {
    resetModalForm(); // 빈 폼
    document.getElementById("projectModal").style.display = "flex";
  };

  // 수정용 프로젝트 불러오기
  window.loadProject = (id) => {
    fetch(`/projects/api/${id}`)
      .then(res => res.json())
      .then(data => {
        document.getElementById("projectModal").style.display = "flex";

        // 필드 값 설정
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

  // 폼 초기화
  function resetModalForm() {
    const form = document.querySelector("#projectModal form");
    form.reset();
    form.action = "/projects/save";
    form.querySelector("input[name='id']").value = '';
  }
});
