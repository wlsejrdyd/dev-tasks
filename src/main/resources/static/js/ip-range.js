function toggleIpList(id) {
  const target = document.getElementById('range-' + id);
  if (!target) return;

  document.querySelectorAll('.ip-list').forEach(el => {
    if (el.id !== 'range-' + id) {
      el.classList.add('hidden');
    }
  });

  target.classList.toggle('hidden');
}

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
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  })
    .then(res => {
      if (!res.ok) return res.text().then(msg => { throw new Error(msg); });
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

function deleteRange(id) {
  if (!confirm("이 대역을 삭제하시겠습니까? 관련된 모든 IP도 삭제됩니다.")) return;

  fetch('/api/ip-range/' + id, {
    method: 'DELETE'
  })
    .then(res => {
      if (!res.ok) throw new Error("삭제 실패");
      alert("삭제되었습니다.");
      location.reload();
    })
    .catch(err => {
      alert("오류 발생: " + err.message);
    });
}
