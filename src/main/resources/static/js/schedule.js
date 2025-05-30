document.addEventListener("DOMContentLoaded", function () {
    const yearSelect = document.getElementById("year-select");
    const monthSelect = document.getElementById("month-select");
    const calendarContainer = document.getElementById("calendar-container");
    const modal = document.getElementById("schedule-modal");
    const closeBtn = modal.querySelector(".close");
    const form = document.getElementById("schedule-form");

    const startInput = document.getElementById("schedule-start");
    const endInput = document.getElementById("schedule-end");
    const deleteBtn = document.createElement("button");
    deleteBtn.textContent = "삭제";
    deleteBtn.type = "button";

    let editingId = null;

    const now = new Date();
    for (let y = now.getFullYear() - 2; y <= now.getFullYear() + 2; y++) {
        const opt = document.createElement("option");
        opt.value = y;
        opt.textContent = y;
        if (y === now.getFullYear()) opt.selected = true;
        yearSelect.appendChild(opt);
    }

    for (let m = 1; m <= 12; m++) {
        const opt = document.createElement("option");
        opt.value = m;
        opt.textContent = m;
        if (m === now.getMonth() + 1) opt.selected = true;
        monthSelect.appendChild(opt);
    }

    function getRandomColor() {
        const colors = [
            "#d1eaff", "#ffe2cc", "#e2ffd5", "#f3d1ff", "#ffdbdb",
            "#fff3b0", "#c2f0f7", "#d9d9ff", "#c5f7e0", "#ffecd1"
        ];
        return colors[Math.floor(Math.random() * colors.length)];
    }

    function renderCalendar(year, month) {
        calendarContainer.innerHTML = "";
        const firstDay = new Date(year, month - 1, 1);
        const lastDate = new Date(year, month, 0).getDate();
        const startWeekday = firstDay.getDay();
        const totalCells = startWeekday + lastDate;
        const cellMap = {};

        for (let i = 0; i < totalCells; i++) {
            const cell = document.createElement("div");
            cell.className = "calendar-day";

            if (i >= startWeekday) {
                const date = i - startWeekday + 1;
                const dateStr = `${year}-${String(month).padStart(2, "0")}-${String(date).padStart(2, "0")}`;
                cell.dataset.date = dateStr;

                const dateSpan = document.createElement("div");
                dateSpan.className = "date";
                dateSpan.textContent = date;
                cell.appendChild(dateSpan);

                cellMap[dateStr] = cell;
            }

            calendarContainer.appendChild(cell);
        }

        fetch(`/api/schedules?year=${year}&month=${month}`)
            .then(res => res.json())
            .then(data => {
                data.forEach(schedule => {
                    const start = new Date(schedule.startDate);
                    const end = new Date(schedule.endDate);
                    const color = getRandomColor();

                    for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
                        const yyyy = d.getFullYear();
                        const mm = String(d.getMonth() + 1).padStart(2, "0");
                        const dd = String(d.getDate()).padStart(2, "0");
                        const key = `${yyyy}-${mm}-${dd}`;

                        const eventDiv = document.createElement("div");
                        eventDiv.className = "event";
                        eventDiv.textContent = schedule.title;
                        eventDiv.title = `[${schedule.attendees}] ${schedule.content}`;
                        eventDiv.style.backgroundColor = color;
                        eventDiv.dataset.schedule = JSON.stringify(schedule);

                        eventDiv.addEventListener("click", (e) => {
                            e.stopPropagation();
                            const s = JSON.parse(eventDiv.dataset.schedule);
                            editingId = s.id;
                            modal.style.display = "block";
                            startInput.value = s.startDate.replace(" ", "T");
                            endInput.value = s.endDate.replace(" ", "T");
                            document.getElementById("schedule-title").value = s.title;
                            document.getElementById("schedule-attendees").value = s.attendees;
                            document.getElementById("schedule-desc").value = s.content;
                            if (!form.contains(deleteBtn)) {
                                form.appendChild(deleteBtn);
                            }
                        });

                        if (cellMap[key]) {
                            cellMap[key].appendChild(eventDiv);
                        }
                    }
                });
            });
    }

    function openModal(dateStr) {
        modal.style.display = "block";
        form.reset();
        editingId = null;
        startInput.value = `${dateStr}T09:00`;
        endInput.value = `${dateStr}T18:00`;
        if (form.contains(deleteBtn)) {
            deleteBtn.remove();
        }
    }

    closeBtn.addEventListener("click", () => {
        modal.style.display = "none";
    });

    form.addEventListener("submit", function (e) {
        e.preventDefault();
        const data = {
            title: document.getElementById("schedule-title").value,
            startDate: startInput.value,
            endDate: endInput.value,
            attendees: document.getElementById("schedule-attendees").value,
            content: document.getElementById("schedule-desc").value,
            time: `${startInput.value.slice(11, 16)} ~ ${endInput.value.slice(11, 16)}`
        };

        const method = editingId ? "PUT" : "POST";
        const url = editingId ? `/api/schedules/${editingId}` : "/api/schedules";

        fetch(url, {
            method: method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        }).then(res => {
            if (res.ok) {
                alert("저장 성공");
                modal.style.display = "none";
                renderCalendar(Number(yearSelect.value), Number(monthSelect.value));
            } else {
                alert("저장 실패");
            }
        });
    });

    deleteBtn.addEventListener("click", function () {
        if (editingId && confirm("정말 삭제하시겠습니까?")) {
            fetch(`/api/schedules/${editingId}`, {
                method: "DELETE"
            }).then(res => {
                if (res.ok) {
                    alert("삭제 완료");
                    modal.style.display = "none";
                    renderCalendar(Number(yearSelect.value), Number(monthSelect.value));
                } else {
                    alert("삭제 실패");
                }
            });
        }
    });

    calendarContainer.addEventListener("click", function (e) {
        const day = e.target.closest(".calendar-day");
        if (day && day.dataset.date) {
            openModal(day.dataset.date);
        }
    });

    renderCalendar(Number(yearSelect.value), Number(monthSelect.value));
    yearSelect.addEventListener("change", () => {
        renderCalendar(Number(yearSelect.value), Number(monthSelect.value));
    });
    monthSelect.addEventListener("change", () => {
        renderCalendar(Number(yearSelect.value), Number(monthSelect.value));
    });
});
