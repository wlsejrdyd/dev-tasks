// IP 리스트 접기/펼치기
function toggleIpList(id) {
  const target = document.getElementById('range-' + id);
  if (!target) return;

  // 다른 대역 닫기
  document.querySelectorAll('.ip-list').forEach(el => {
    if (el.id !== 'range-' + id) {
      el.classList.add('hidden');
    }
  });

  target.classList.toggle('hidden');
}

// 네트워크 대역 생성 모달 열기
function openIpRangeModal() {
  fetch('/ip-range/new')
    .then(res => res.text())
    .then(html => {
      document.getElementById('range-modal-body').innerHTML = html;
      document.getElementById('ipRangeModal').classList.remove('hidden');
    });
}

function closeIpRangeModal() {
  document.getElementById('ipRangeModal').classList.add('hidden');
}

function submitIpRangeForm(event) {
  event.preventDefault();

  const form = document.getElementById('ipRangeForm');
  const formData = new FormData(form);
  const data = {};
  formData.forEach((value, key) => {
    data[key] = value;
  });

  fetch('/api/ip-range', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  })
    .then(res => {
      if (!res.ok) throw new Error("대역 생성 실패");
      return res.json();
    })
    .then(() => {
      alert("대역이 생성되었습니다.");
      location.reload();
    })
    .catch(err => {
      alert("오류 발생: " + err.message);
    });

  return false;
}
