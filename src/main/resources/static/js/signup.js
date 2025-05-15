document.addEventListener("DOMContentLoaded", function () {
  const username = document.getElementById("username");
  const password = document.getElementById("password");
  const email = document.getElementById("email");
  const phone = document.getElementById("phone");

  const usernameMsg = document.getElementById("username-msg");
  const passwordMsg = document.getElementById("password-msg");
  const emailMsg = document.getElementById("email-msg");

  // 아이디 입력 제한 + 영문 키보드 고정
  if (username) {
    username.setAttribute("inputmode", "latin");
    username.style.imeMode = "disabled";
    username.addEventListener("focus", () => {
      username.setAttribute("inputmode", "latin");
      username.style.imeMode = "disabled";
    });

    username.addEventListener("input", () => {
      const pattern = /^[a-zA-Z0-9_]+$/;
      if (!pattern.test(username.value)) {
        usernameMsg.textContent = "아이디는 영문자, 숫자, _ 만 사용할 수 있습니다.";
        usernameMsg.style.color = "red";
        return;
      }

      // 아이디 중복 확인
      fetch(`/check-username?username=${username.value}`)
        .then(res => res.json())
        .then(data => {
          if (data.exists === true) {
            usernameMsg.textContent = "이미 사용 중인 아이디입니다.";
            usernameMsg.style.color = "red";
          } else if (data.exists === false) {
            usernameMsg.textContent = "사용 가능한 아이디입니다.";
            usernameMsg.style.color = "green";
          } else {
            usernameMsg.textContent = "확인할 수 없습니다.";
            usernameMsg.style.color = "gray";
          }
        })
        .catch(() => {
          usernameMsg.textContent = "오류 발생. 다시 시도해주세요.";
          usernameMsg.style.color = "red";
        });
    });
  }

  // 이메일 형식 + 중복 확인
  email.addEventListener("input", () => {
    const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!pattern.test(email.value)) {
      emailMsg.textContent = "올바른 이메일 형식이 아닙니다.";
      emailMsg.style.color = "red";
      return;
    }

    // 이메일 중복 확인
    fetch(`/check-email?email=${email.value}`)
      .then(res => res.json())
      .then(data => {
        if (data.exists === true) {
          emailMsg.textContent = "이미 등록된 이메일입니다.";
          emailMsg.style.color = "red";
        } else if (data.exists === false) {
          emailMsg.textContent = "사용 가능한 이메일입니다.";
          emailMsg.style.color = "green";
        } else {
          emailMsg.textContent = "확인할 수 없습니다.";
          emailMsg.style.color = "gray";
        }
      })
      .catch(() => {
        emailMsg.textContent = "오류 발생. 다시 시도해주세요.";
        emailMsg.style.color = "red";
      });
  });

  // 비밀번호 강도 검사
  password.addEventListener("input", () => {
    const pw = password.value;
    const pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{9,}$/;
    if (!pattern.test(pw)) {
      passwordMsg.textContent = "대소문자, 숫자, 특수문자를 포함한 9자 이상이어야 합니다.";
      passwordMsg.style.color = "red";
    } else {
      passwordMsg.textContent = "사용 가능한 비밀번호입니다.";
      passwordMsg.style.color = "green";
    }
  });

  // 전화번호 자동 하이픈 삽입
  phone.addEventListener("input", () => {
    let num = phone.value.replace(/\D/g, "");
    if (num.length <= 3) {
      phone.value = num;
    } else if (num.length <= 7) {
      phone.value = num.slice(0, 3) + "-" + num.slice(3);
    } else if (num.length <= 11) {
      phone.value = num.slice(0, 3) + "-" + num.slice(3, 7) + "-" + num.slice(7);
    } else {
      phone.value = num.slice(0, 3) + "-" + num.slice(3, 7) + "-" + num.slice(7, 11);
    }
  });
});
