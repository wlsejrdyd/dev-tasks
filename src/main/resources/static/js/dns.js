function openDnsModal() {
  resetDnsForm();
  document.getElementById("dnsModal").classList.remove("hidden");
}

function closeDnsModal() {
  document.getElementById("dnsModal").classList.add("hidden");
}

function resetDnsForm() {
  const form = document.getElementById("dnsForm");
  if (form) {
    form.reset();
    document.getElementById("dns-id").value = "";
  }
}

function submitDnsForm(event) {
  event.preventDefault();
  const form = document.getElementById("dnsForm");
  const data = Object.fromEntries(new FormData(form));
  data.sslValid = form["sslValid"].checked;

  if (!data.maindomain || data.maindomain.trim() === "") {
    alert("메인 도메인은 필수 항목입니다.");
    return;
  }

  const isEdit = data.id && data.id !== "";
  const method = isEdit ? "PUT" : "POST";
  const url = "/api/dns" + (isEdit ? `/${data.id}` : "");

  fetch(url, {
    method,
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
    .catch(err => alert("오류 발생: " + err.message));
}

function editDns(id) {
  fetch(`/api/dns/${id}`)
    .then(res => {
      if (!res.ok) throw new Error("데이터를 불러올 수 없습니다.");
      return res.json();
    })
    .then(item => {
      openDnsModal();
      document.getElementById("dns-id").value = item.id;
      document.getElementById("dns-maindomain").value = item.maindomain;
      document.getElementById("dns-host").value = item.host;
      document.getElementById("dns-type").value = item.type;
      document.getElementById("dns-ip").value = item.ip;
      document.getElementById("dns-description").value = item.description || "";
      document.getElementById("dns-sslValid").checked = item.sslValid;
    })
    .catch(err => alert("오류 발생: " + err.message));
}

function deleteDns(id) {
  if (!confirm("정말 삭제하시겠습니까?")) return;
  fetch(`/api/dns/${id}`, { method: "DELETE" })
    .then(res => {
      if (!res.ok) throw new Error("삭제 실패");
      alert("삭제되었습니다.");
      location.reload();
    })
    .catch(err => alert("오류 발생: " + err.message));
}

function downloadZoneFile() {
  window.location.href = "/dns/download";
}

function downloadZoneFileForMaindomain(maindomain) {
  const encoded = encodeURIComponent(maindomain);
  window.location.href = `/dns/download?maindomain=${encoded}`;
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
    .catch(err => alert("오류 발생: " + err.message));
}

function runSslExpiryCheck() {
  if (!confirm("모든 호스트의 SSL 만료일을 확인하고 갱신하시겠습니까?")) return;

  fetch("/api/dns/ssl-expiry-check", { method: "POST" })
    .then(res => {
      if (!res.ok) throw new Error("만료일 검사 실패");
      alert("SSL 만료일이 갱신되었습니다.");
      location.reload();
    })
    .catch(err => alert("오류 발생: " + err.message));
}

function toggleMaindomain(maindomainId) {
  const group = document.getElementById(maindomainId);
  if (group) {
    group.style.display = group.style.display === "none" ? "table" : "none";
  }
}

function triggerZoneUpload() {
  const fileInput = document.getElementById("zoneFileInput");
  if (fileInput) fileInput.click();
}

function uploadZoneInForm() {
  const fileInput = document.getElementById("zoneFileInput");
  const maindomainInput = document.getElementById("dns-maindomain");
  const file = fileInput?.files[0];
  const maindomain = maindomainInput?.value;

  if (!file) return;
  if (!maindomain || maindomain.trim() === "") {
    alert("먼저 메인 도메인을 입력해주세요.");
    return;
  }

  const formData = new FormData();
  formData.append("file", file);
  formData.append("maindomain", maindomain);

  fetch("/dns/upload", {
    method: "POST",
    body: formData
  })
    .then(res => {
      if (!res.ok) throw new Error("업로드 실패");
      alert("업로드 성공");
      location.reload();
    })
    .catch(err => alert("오류 발생: " + err.message));
}

document.addEventListener("DOMContentLoaded", () => {
  const keywordInput = document.getElementById("searchKeyword");
  if (keywordInput) {
    keywordInput.addEventListener("keydown", function (e) {
      if (e.key === "Enter") {
        e.preventDefault();
        searchDns();
      }
    });
  }
});

function openMaindomainModal() {
  resetDnsForm();
  document.getElementById("dnsModal").classList.remove("hidden");
  const maindomainInput = document.getElementById("dns-maindomain");
  if (maindomainInput) {
    maindomainInput.focus();
  }
}
