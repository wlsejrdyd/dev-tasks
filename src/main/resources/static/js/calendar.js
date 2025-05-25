document.addEventListener("DOMContentLoaded", () => {
  // 달력 아이콘 클릭 시 달력 팝업 열기
  const calendarBtn = document.createElement("div");
  calendarBtn.id = "calendarToggleBtn";
  calendarBtn.innerHTML = "📅";
  document.body.appendChild(calendarBtn);

  calendarBtn.addEventListener("click", () => {
    document.getElementById("calendarModal").classList.remove("hidden");
  });

  // 달력 외부 클릭 시 닫기
  document.addEventListener("click", (e) => {
    const modal = document.getElementById("calendarModal");
    if (!modal || modal.classList.contains("hidden")) return;

    if (!modal.contains(e.target) && e.target.id !== "calendarToggleBtn") {
      modal.classList.add("hidden");
    }
  });

  // 날짜 클릭 핸들러
  window.handleDateClick = function (dateStr) {
    document.getElementById("selectedDate").textContent = dateStr;
    document.getElementById("calendarModal").classList.add("hidden");
    document.getElementById("attendanceModal").classList.remove("hidden");
    document.getElementById("attendance-date").value = dateStr;
  };

  // 닫기 버튼
  document.querySelectorAll(".close-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      btn.closest(".modal").classList.add("hidden");
    });
  });
});
