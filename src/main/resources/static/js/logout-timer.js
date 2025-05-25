document.addEventListener("DOMContentLoaded", function () {
  let remaining = 600; // 600초 = 10분
  const timerSpan = document.getElementById("logout-timer");

  function updateTimer() {
    const min = String(Math.floor(remaining / 60)).padStart(2, "0");
    const sec = String(remaining % 60).padStart(2, "0");
    timerSpan.textContent = `${min}:${sec}`;
    if (remaining > 0) remaining--;
  }

  setInterval(updateTimer, 1000);
});
