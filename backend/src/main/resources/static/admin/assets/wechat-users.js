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
  let items = [];

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
    items = await Admin.api(`/admin/wechat-users?page=${page}&size=${size}`);
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
          <td>
            <div class="actions">
              <button data-edit="${u.id}">改昵称</button>
              <button class="danger" data-del="${u.id}">删除</button>
            </div>
          </td>
        </tr>
      `;
    }).join('') || '<tr><td colspan="7" class="muted">暂无数据</td></tr>';
  }

  async function editNickname(id) {
    const item = items.find((x) => String(x.id) === String(id));
    const current = item && item.nickname ? String(item.nickname) : '';
    const nn = prompt(`修改微信用户 #${id} 的昵称（留空将清空）`, current);
    if (nn === null) return;
    try {
      await Admin.api(`/admin/wechat-users/${encodeURIComponent(id)}`,
        { method: 'PUT', body: { nickname: nn } });
      Admin.toast('已更新');
      await load();
    } catch (e) {
      Admin.toast(`更新失败：${e && e.message ? e.message : '未知错误'}`, 'danger');
    }
  }

  async function delUser(id) {
    if (!confirm(`确认删除微信用户 #${id} ?（会同时删除该用户的收藏记录）`)) return;
    try {
      await Admin.api(`/admin/wechat-users/${encodeURIComponent(id)}`, { method: 'DELETE' });
      Admin.toast('已删除');
      await load();
    } catch (e) {
      Admin.toast(`删除失败：${e && e.message ? e.message : '未知错误'}`, 'danger');
    }
  }

  reloadBtn.addEventListener('click', load);
  prevBtn.addEventListener('click', () => { page = Math.max(1, page - 1); load(); });
  nextBtn.addEventListener('click', () => { page = page + 1; load(); });
  sizeEl.addEventListener('change', () => { page = 1; load(); });

  rowsEl.addEventListener('click', (ev) => {
    const t = ev.target;
    if (!(t instanceof HTMLElement)) return;
    const editId = t.getAttribute('data-edit');
    const delId = t.getAttribute('data-del');
    if (editId) editNickname(editId);
    if (delId) delUser(delId);
  });

  load().catch((e) => Admin.toast(`加载失败：${e && e.message ? e.message : '未知错误'}`, 'danger'));
})();
