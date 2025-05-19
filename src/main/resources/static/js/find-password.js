document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("find-password-form");
  const result = document.getElementById("result");

  form.addEventListener("submit", async function (e) {
    e.preventDefault();
    const username = document.getElementById("username").value;
    const email = document.getElementById("email").value;

    const response = await fetch("/auth/find-password", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: new URLSearchParams({ username, email }),
    });

    const text = await response.text();
    result.innerText = text;
  });
});
