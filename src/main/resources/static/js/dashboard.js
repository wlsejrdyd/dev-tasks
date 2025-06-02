document.addEventListener("DOMContentLoaded", function () {
  const upcomingBox = document.querySelector(".upcoming-schedule-box");
  const noticeList = document.getElementById("notice-list");
  const pollForm = document.getElementById("poll-form");
  const pollQuestion = document.getElementById("poll-question");

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

  function renderNotices() {
    fetch("/api/notices")
      .then(res => res.json())
      .then(data => {
        noticeList.innerHTML = "";
        data.forEach(notice => {
          const li = document.createElement("li");
          const title = document.createElement("div");
          const content = document.createElement("div");

          title.textContent = "📌 " + notice.title;
          title.style.cursor = "pointer";

          content.textContent = notice.content;
          content.style.display = "none";
          content.style.marginTop = "4px";
          content.style.fontSize = "13px";
          content.style.color = "#333";

          title.addEventListener("click", function () {
            content.style.display = content.style.display === "none" ? "block" : "none";
          });

          li.appendChild(title);
          li.appendChild(content);
          noticeList.appendChild(li);
        });
      })
      .catch(() => {
        noticeList.innerHTML = "<li>공지사항을 불러올 수 없습니다.</li>";
      });
  }

  function renderPoll() {
    fetch("/api/poll")
      .then(res => res.json())
      .then(poll => {
        if (!poll || !poll.question || !poll.options) {
          pollQuestion.textContent = "진행 중인 투표가 없습니다.";
          pollForm.innerHTML = "";
          return;
        }

        pollQuestion.textContent = poll.question;

        if (poll.voted) {
          const totalVotes = poll.options.reduce((sum, opt) => sum + opt.voteCount, 0);
          pollForm.innerHTML = poll.options
            .map(opt => {
              const percent = totalVotes > 0 ? ((opt.voteCount / totalVotes) * 100).toFixed(1) : 0;
              return `<div>${opt.optionText} - ${opt.voteCount}표 (${percent}%)</div>`;
            })
            .join("");
        } else {
          pollForm.innerHTML = poll.options
            .map(opt =>
              `<label><input type="radio" name="poll" value="${opt.id}"> ${opt.optionText}</label>`
            )
            .join("") + `<button type="submit">투표하기</button>`;

          pollForm.addEventListener("submit", function (e) {
            e.preventDefault();
            const selected = pollForm.querySelector("input[name='poll']:checked");
            if (!selected) {
              alert("선택지를 고르세요");
              return;
            }

            fetch("/api/poll/vote", {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify({ pollId: poll.id, optionId: selected.value }),
            })
              .then(res => {
                if (res.ok) {
                  alert("투표 완료!");
                  renderPoll(); // 다시 렌더링
                } else {
                  res.text().then(msg => alert("투표 실패: " + msg));
                }
              });
          });
        }
      })
      .catch(() => {
        pollQuestion.textContent = "투표 정보를 불러올 수 없습니다.";
        pollForm.innerHTML = "";
      });
  }

  renderUpcomingSchedules();
  renderNotices();
  renderPoll();
});
