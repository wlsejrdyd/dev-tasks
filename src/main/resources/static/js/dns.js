function openDnsModal() {
  resetDnsForm();
  document.getElementById("dnsModal").classList.remove("hidden");
}

function closeDnsModal() {
  document.getElementById("dnsModal").classList.add("hidden");
}

function resetDnsForm() {
  document.getElementById("dnsForm").reset();
  document.getElementById("dns-id").value = '';
}

function submitDnsForm(event) {
  event.preventDefault();
  const form = document.getElementById("dnsForm");
  const formData = new FormData(form);
  const data = {};
  formData.forEach((value, key) => {
    if (key === "sslValid") data[key] = form[key].checked;
    else data[key] = value;
  });

  const isEdit = data.id && data.id !== "";

  fetch("/api/dns" + (isEdit ? "/" + data.id : ""), {
    method: isEdit ? "PUT" : "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  })
    .then(res => {
      if (!res.ok) throw new Error("저장 실패");
      return res.json();
    })
    .then(() => {
      alert("저장되었습니다.");
      location.reload();
    })
    .catch(err => {
      alert("오류 발생: " + err.message);
    });

  return false;
}

function editDns(id) {
  fetch("/api/dns")
    .then(res => res.json())
    .then(data => {
      const item = data.find(x => x.id === id);
      if (!item) return alert("데이터를 찾을 수 없습니다.");

      openDnsModal();
      document.getElementById("dns-id").value = item.id;
      document.getElementById("dns-host").value = item.host;
      document.getElementById("dns-type").value = item.type;
      document.getElementById("dns-ip").value = item.ip;
      document.getElementById("dns-description").value = item.description || "";
      document.getElementById("dns-sslValid").checked = item.sslValid;
    });
}

function deleteDns(id) {
  if (!confirm("정말 삭제하시겠습니까?")) return;

  fetch("/api/dns/" + id, { method: "DELETE" })
    .then(res => {
      if (!res.ok) throw new Error("삭제 실패");
      alert("삭제되었습니다.");
      location.reload();
    })
    .catch(err => {
      alert("오류 발생: " + err.message);
    });
}

function uploadZoneFile(input) {
  const file = input.files[0];
  if (!file) return;
  const reader = new FileReader();
  reader.onload = () => {
    fetch("/dns/upload", {
      method: "POST",
      headers: { "Content-Type": "text/plain" },
      body: reader.result,
    })
      .then(res => {
        if (!res.ok) throw new Error("업로드 실패");
        alert("업로드 성공");
        location.reload();
      })
      .catch(err => {
        alert("오류 발생: " + err.message);
      });
  };
  reader.readAsText(file);
}

function downloadZoneFile() {
  window.location.href = "/dns/download";
}

function searchDns() {
  const keyword = document.getElementById("searchKeyword").value;
  const type = document.getElementById("typeFilter").value;
  const ssl = document.getElementById("sslFilter").value;
  const url = new URL(window.location.origin + "/dns");
  if (keyword) url.searchParams.append("keyword", keyword);
  if (type) url.searchParams.append("type", type);
  if (ssl) url.searchParams.append("sslValid", ssl);
  location.href = url.toString();
}

function runSslCheck() {
  if (!confirm("모든 호스트에 대해 SSL 상태를 검사하시겠습니까?")) return;

  fetch("/api/dns/ssl-check", { method: "POST" })
    .then(res => {
      if (!res.ok) throw new Error("SSL 검사 실패");
      alert("SSL 상태가 갱신되었습니다.");
      location.reload();
    })
    .catch(err => {
      alert("오류 발생: " + err.message);
    });
}
