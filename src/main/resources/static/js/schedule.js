document.addEventListener("DOMContentLoaded", function () {
    const yearSelect = document.getElementById("year-select");
    const monthSelect = document.getElementById("month-select");
    const calendarContainer = document.getElementById("calendar-container");
    const modal = document.getElementById("schedule-modal");
    const closeBtn = modal.querySelector(".close");
    const form = document.getElementById("schedule-form");

    const titleInput = document.getElementById("schedule-title");
    const startInput = document.getElementById("schedule-start");
    const endInput = document.getElementById("schedule-end");
    const attendeesInput = document.getElementById("schedule-attendees");
    const descInput = document.getElementById("schedule-desc");

    let schedules = [];
    let editScheduleId = null; // 수정중인 일정 ID

    // 연도/월 select 박스 채우기
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

    function datesBetween(startDate, endDate) {
        const dates = [];
        let current = new Date(startDate);
        const last = new Date(endDate);
        while (current <= last) {
            dates.push(current.toISOString().split("T")[0]);
            current.setDate(current.getDate() + 1);
        }
        return dates;
    }

    function renderCalendar(year, month) {
        calendarContainer.innerHTML = "";
        const firstDay = new Date(year, month - 1, 1);
        const lastDate = new Date(year, month, 0).getDate();

        const startWeekday = firstDay.getDay();
        const totalCells = startWeekday + lastDate;

        // 날짜별 일정 묶기 (날짜 => 일정 배열)
        const scheduleMap = {};
        schedules.forEach(sch => {
            const schDates = datesBetween(sch.startDate, sch.endDate);
            schDates.forEach(date => {
                if (!scheduleMap[date]) scheduleMap[date] = [];
                scheduleMap[date].push(sch);
            });
        });

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

                // 일정 붙이기
                const daySchedules = scheduleMap[dateStr] || [];
                daySchedules.forEach(sch => {
                    const eventDiv = document.createElement("div");
                    eventDiv.className = "event";
                    eventDiv.textContent = sch.title;
                    eventDiv.title = `참석자: ${sch.attendees || ''}\n시간: ${sch.time || ''}\n내용: ${sch.content || ''}`;
                    eventDiv.dataset.scheduleId = sch.id;
                    cell.appendChild(eventDiv);
                });
            }

            calendarContainer.appendChild(cell);
        }
    }

    function openModal(dateStr, schedule) {
        modal.classList.remove("hidden");
        modal.style.display = "block";

        if (schedule) {
            // 수정 모드
            editScheduleId = schedule.id;
            titleInput.value = schedule.title || "";
            startInput.value = `${schedule.startDate}T09:00`;
            endInput.value = `${schedule.endDate}T18:00`;
            attendeesInput.value = schedule.attendees || "";
            descInput.value = schedule.content || "";
        } else {
            // 신규 등록 모드
            editScheduleId = null;
            titleInput.value = "";
            startInput.value = `${dateStr}T09:00`;
            endInput.value = `${dateStr}T18:00`;
            attendeesInput.value = "";
            descInput.value = "";
        }
    }

    closeBtn.addEventListener("click", () => {
        modal.style.display = "none";
        editScheduleId = null;
    });

    // 폼 제출 처리 (신규등록 및 수정)
    form.addEventListener("submit", function (e) {
        e.preventDefault();
        const data = {
            title: titleInput.value,
            startDate: startInput.value.split('T')[0],
            endDate: endInput.value.split('T')[0],
            attendees: attendeesInput.value,
            content: descInput.value,
            time: `${startInput.value.split('T')[1]} ~ ${endInput.value.split('T')[1]}`
        };

        const method = editScheduleId ? "PUT" : "POST";
        const url = editScheduleId ? `/api/schedules/${editScheduleId}` : "/api/schedules";

        fetch(url, {
            method: method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        })
        .then(res => {
            if (!res.ok) throw new Error("일정 저장에 실패했습니다.");
            return res.text();
        })
        .then(() => {
            modal.style.display = "none";
            form.reset();
            editScheduleId = null;
            fetchSchedules();
        })
        .catch(err => alert(err.message));
    });

    // 일정 삭제 버튼 추가 및 처리
    // 모달 내에 삭제 버튼 생성 (만약 없다면)
    if (!document.getElementById("delete-btn")) {
        const deleteBtn = document.createElement("button");
        deleteBtn.type = "button";
        deleteBtn.id = "delete-btn";
        deleteBtn.textContent = "삭제";
        deleteBtn.style.marginLeft = "10px";
        form.appendChild(deleteBtn);

        deleteBtn.addEventListener("click", () => {
            if (!editScheduleId) return alert("삭제할 일정을 선택해주세요.");
            if (!confirm("정말 삭제하시겠습니까?")) return;

            fetch(`/api/schedules/${editScheduleId}`, { method: "DELETE" })
                .then(res => {
                    if (!res.ok) throw new Error("일정 삭제에 실패했습니다.");
                    return res.text();
                })
                .then(() => {
                    modal.style.display = "none";
                    form.reset();
                    editScheduleId = null;
                    fetchSchedules();
                })
                .catch(err => alert(err.message));
        });
    }

    // 날짜 클릭 시 모달 열기 - 일정 클릭 시 수정 모달 열기
    calendarContainer.addEventListener("click", function (e) {
        const eventDiv = e.target.closest(".event");
        if (eventDiv && eventDiv.dataset.scheduleId) {
            const scheduleId = Number(eventDiv.dataset.scheduleId);
            const schedule = schedules.find(sch => sch.id === scheduleId);
            if (schedule) openModal(null, schedule);
            return;
        }

        const day = e.target.closest(".calendar-day");
        if (day && day.dataset.date) {
            openModal(day.dataset.date, null);
        }
    });

    // 초기 렌더링 및 변경 이벤트 바인딩
    fetchSchedules();

    yearSelect.addEventListener("change", fetchSchedules);
    monthSelect.addEventListener("change", fetchSchedules);

    function fetchSchedules() {
        const year = Number(yearSelect.value);
        const month = Number(monthSelect.value);
        fetch(`/api/schedules?year=${year}&month=${month}`)
            .then(res => {
                if (!res.ok) throw new Error("일정 데이터를 불러오는데 실패했습니다.");
                return res.json();
            })
            .then(data => {
                schedules = data;
                renderCalendar(year, month);
            })
            .catch(err => alert(err.message));
    }
});
