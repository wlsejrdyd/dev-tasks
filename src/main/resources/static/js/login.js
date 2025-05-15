document.addEventListener("DOMContentLoaded", function () {
  const usernameInput = document.getElementById("username");
  const passwordInput = document.getElementById("password");

  if (usernameInput) {
    usernameInput.setAttribute("inputmode", "latin");
    usernameInput.style.imeMode = "disabled";
    usernameInput.addEventListener("focus", () => {
      usernameInput.setAttribute("inputmode", "latin");
      usernameInput.style.imeMode = "disabled";
    });
  }

  document.querySelector("form").addEventListener("submit", function (e) {
    const username = usernameInput.value.trim();
    const password = passwordInput.value.trim();
    if (!username || !password) {
      alert("아이디와 비밀번호를 입력해주세요.");
      e.preventDefault();
    }
  });
});
