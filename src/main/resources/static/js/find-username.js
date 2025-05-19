document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("find-username-form");
  const result = document.getElementById("result");

  form.addEventListener("submit", async function (e) {
    e.preventDefault();
    const email = document.getElementById("email").value;

    const response = await fetch("/auth/find-username", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: new URLSearchParams({ email }),
    });

    const text = await response.text();
    result.innerText = text;
  });
});
