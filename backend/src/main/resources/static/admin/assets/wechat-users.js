(function () {
  if (!Admin.requireAuth()) return;
  Admin.setActiveNav();
  Admin.renderTopbar();

  const rowsEl = document.getElementById('rows');
  const pageInfoEl = document.getElementById('pageInfo');
  const sizeEl = document.getElementById('size');
  const reloadBtn = document.getElementById('reloadBtn');
  const prevBtn = document.getElementById('prevBtn');
  const nextBtn = document.getElementById('nextBtn');

  let page = 1;

  function escapeHtml(s) {
    return String(s)
      .replaceAll('&', '&amp;')
      .replaceAll('<', '&lt;')
      .replaceAll('>', '&gt;')
      .replaceAll('"', '&quot;')
      .replaceAll("'", '&#39;');
  }

  async function load() {
    Admin.toast('');
    const size = Number(sizeEl.value || 20);
    const items = await Admin.api(`/admin/wechat-users?page=${page}&size=${size}`);
    pageInfoEl.textContent = `page=${page} size=${size} count=${items.length}`;
    rowsEl.innerHTML = items.map((u) => {
      const avatar = u.avatarUrl ? `<img class="thumb" src="${escapeHtml(u.avatarUrl)}" />` : '<span class="muted">-</span>';
      return `
        <tr>
          <td>${u.id}</td>
          <td>${escapeHtml(u.openid || '')}</td>
          <td>${escapeHtml(u.nickname || '')}</td>
          <td>${avatar}</td>
          <td>${escapeHtml(Admin.fmtDate(u.createdAt))}</td>
          <td>${escapeHtml(Admin.fmtDate(u.updatedAt))}</td>
        </tr>
      `;
    }).join('') || '<tr><td colspan="6" class="muted">暂无数据</td></tr>';
  }

  reloadBtn.addEventListener('click', load);
  prevBtn.addEventListener('click', () => { page = Math.max(1, page - 1); load(); });
  nextBtn.addEventListener('click', () => { page = page + 1; load(); });
  sizeEl.addEventListener('change', () => { page = 1; load(); });

  load().catch((e) => Admin.toast(`加载失败：${e && e.message ? e.message : '未知错误'}`, 'danger'));
})();
