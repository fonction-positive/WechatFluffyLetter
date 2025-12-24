(function () {
  if (!Admin.requireAuth()) return;
  Admin.setActiveNav();
  Admin.renderTopbar();

  const rowsEl = document.getElementById('rows');
  const pageInfoEl = document.getElementById('pageInfo');
  const sizeEl = document.getElementById('size');
  const prevBtn = document.getElementById('prevBtn');
  const nextBtn = document.getElementById('nextBtn');
  const applyBtn = document.getElementById('applyBtn');
  const clearBtn = document.getElementById('clearBtn');
  const userIdEl = document.getElementById('userId');
  const productIdEl = document.getElementById('productId');

  let page = 1;
  let filters = { userId: '', productId: '' };

  function escapeHtml(s) {
    return String(s)
      .replaceAll('&', '&amp;')
      .replaceAll('<', '&lt;')
      .replaceAll('>', '&gt;')
      .replaceAll('"', '&quot;')
      .replaceAll("'", '&#39;');
  }

  function buildQuery() {
    const size = Number(sizeEl.value || 20);
    const params = new URLSearchParams();
    params.set('page', String(page));
    params.set('size', String(size));
    if (filters.userId) params.set('userId', filters.userId);
    if (filters.productId) params.set('productId', filters.productId);
    return params.toString();
  }

  async function load() {
    Admin.toast('');
    const items = await Admin.api(`/admin/favorites?${buildQuery()}`);
    const size = Number(sizeEl.value || 20);
    pageInfoEl.textContent = `page=${page} size=${size} count=${items.length}`;
    rowsEl.innerHTML = items.map((f) => {
      return `
        <tr>
          <td>${f.id}</td>
          <td>${f.userId}</td>
          <td>${escapeHtml(f.openid || '')}</td>
          <td>${f.productId}</td>
          <td>${escapeHtml(f.productNameZh || '')}</td>
          <td>${escapeHtml(f.productNameEn || '')}</td>
          <td>${escapeHtml(Admin.fmtDate(f.createdAt))}</td>
          <td><button class="danger" data-del="${f.id}">删除</button></td>
        </tr>
      `;
    }).join('') || '<tr><td colspan="8" class="muted">暂无数据</td></tr>';
  }

  async function del(id) {
    if (!confirm(`确认删除收藏记录 #${id} ?`)) return;
    try {
      await Admin.api(`/admin/favorites/${encodeURIComponent(id)}`, { method: 'DELETE' });
      Admin.toast('已删除');
      await load();
    } catch (e) {
      Admin.toast(`删除失败：${e && e.message ? e.message : '未知错误'}`, 'danger');
    }
  }

  rowsEl.addEventListener('click', (ev) => {
    const t = ev.target;
    if (!(t instanceof HTMLElement)) return;
    const delId = t.getAttribute('data-del');
    if (delId) del(delId);
  });
  prevBtn.addEventListener('click', () => { page = Math.max(1, page - 1); load(); });
  nextBtn.addEventListener('click', () => { page = page + 1; load(); });
  sizeEl.addEventListener('change', () => { page = 1; load(); });
  applyBtn.addEventListener('click', () => {
    filters.userId = (userIdEl.value || '').trim();
    filters.productId = (productIdEl.value || '').trim();
    page = 1;
    load();
  });
  clearBtn.addEventListener('click', () => {
    userIdEl.value = '';
    productIdEl.value = '';
    filters = { userId: '', productId: '' };
    page = 1;
    load();
  });

  load().catch((e) => Admin.toast(`加载失败：${e && e.message ? e.message : '未知错误'}`, 'danger'));
})();
