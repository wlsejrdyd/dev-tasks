document.addEventListener("DOMContentLoaded", function () {
  const upcomingBox = document.querySelector(".upcoming-schedule-box");

  function formatDateTime(dtStr) {
    if (!dtStr) return "-";
    const dt = new Date(dtStr);
    const yyyy = dt.getFullYear();
    const mm = String(dt.getMonth() + 1).padStart(2, "0");
    const dd = String(dt.getDate()).padStart(2, "0");
    const hh = String(dt.getHours()).padStart(2, "0");
    const min = String(dt.getMinutes()).padStart(2, "0");
    return `${yyyy}-${mm}-${dd} ${hh}:${min}`;
  }

  function renderUpcomingSchedules() {
    fetch("/api/schedules/upcoming")
      .then(res => res.json())
      .then(data => {
        if (!Array.isArray(data) || data.length === 0) {
          upcomingBox.innerHTML = "<p>📅 다가오는 일정 없음</p>";
          return;
        }

        const ul = document.createElement("ul");
        data.forEach(item => {
          const li = document.createElement("li");
          const start = formatDateTime(item.startDate);
          const end = formatDateTime(item.endDate);
          const title = item.title || "-";
          const content = item.content || "-";
          const attendees = item.attendees || "-";
          li.textContent = `${start} ~ ${end} | ${title} | ${content} | ${attendees}`;
          ul.appendChild(li);
        });

        upcomingBox.innerHTML = "<h3>📅 다가오는 일정</h3>";
        upcomingBox.appendChild(ul);
      });
  }

  renderUpcomingSchedules();
});
