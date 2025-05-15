// 모달 열기 (신규 등록)
function openIpModal() {
  fetch('/ip/new')
    .then(res => res.text())
    .then(html => {
      document.getElementById('modal-body').innerHTML = html;
      document.getElementById('ipModal').classList.remove('hidden');
    });
}

// 모달 닫기
function closeIpModal() {
  document.getElementById('ipModal').classList.add('hidden');
}

// 수정용 데이터 불러오기
function loadIp(id) {
  fetch('/ip/edit/' + id)
    .then(res => res.text())
    .then(html => {
      document.getElementById('modal-body').innerHTML = html;
      document.getElementById('ipModal').classList.remove('hidden');
    });
}

// 저장 처리
function submitIpForm(event) {
  event.preventDefault();

  const form = document.getElementById('ipForm');
  const formData = new FormData(form);
  const data = {};
  formData.forEach((value, key) => {
    if (key === 'projectId' && value === '') {
      data[key] = null;
    } else {
      data[key] = value;
    }
  });

  const isEdit = data.id !== undefined && data.id !== '';

  fetch('/api/ip' + (isEdit ? '/' + data.id : ''), {
    method: isEdit ? 'PUT' : 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  })
    .then(res => {
      if (!res.ok) throw new Error('저장 실패');
      return res.json();
    })
    .then(() => {
      alert('저장되었습니다.');
      location.reload();
    })
    .catch(err => {
      alert('오류 발생: ' + err.message);
    });

  return false;
}

// 연락처 자동 하이픈 삽입
function autoHyphen(input) {
  let num = input.value.replace(/[^0-9]/g, '');

  if (num.startsWith("02")) {
    if (num.length <= 2)
      input.value = num;
    else if (num.length <= 5)
      input.value = num.replace(/(\d{2})(\d{1,3})/, "$1-$2");
    else if (num.length <= 9)
      input.value = num.replace(/(\d{2})(\d{3,4})(\d{0,4})/, "$1-$2-$3");
    else
      input.value = num.substring(0, 10).replace(/(\d{2})(\d{4})(\d{4})/, "$1-$2-$3");
  } else {
    if (num.length < 4)
      input.value = num;
    else if (num.length < 7)
      input.value = num.replace(/(\d{3})(\d{1,3})/, "$1-$2");
    else if (num.length < 11)
      input.value = num.replace(/(\d{3})(\d{3,4})(\d{1,4})/, "$1-$2-$3");
    else
      input.value = num.substring(0, 11).replace(/(\d{3})(\d{4})(\d{4})/, "$1-$2-$3");
  }
}
