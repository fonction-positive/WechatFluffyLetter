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
    const size = Number(sizeEl.value || 10);
    const items = await Admin.api(`/admin/products?page=${page}&size=${size}`);
    pageInfoEl.textContent = `page=${page} size=${size} count=${items.length}`;
    rowsEl.innerHTML = items.map((p) => {
      const cover = p.coverImageUrl ? `<img class="thumb" src="${escapeHtml(p.coverImageUrl)}" />` : '<span class="muted">-</span>';
      return `
        <tr>
          <td>${cover}</td>
          <td>${p.id}</td>
          <td>${p.categoryId}</td>
          <td>${escapeHtml(p.nameZh || '')}</td>
          <td>${escapeHtml(p.nameEn || '')}</td>
          <td>${p.price ?? ''}</td>
          <td>${p.discountPrice ?? ''}</td>
          <td>${p.hot ? 'true' : 'false'}</td>
          <td>${p.active ? 'true' : 'false'}</td>
          <td>
            <div class="actions">
              <a href="product-edit.html?id=${encodeURIComponent(p.id)}"><button>编辑</button></a>
              <button class="danger" data-del="${p.id}">下线</button>
            </div>
          </td>
        </tr>
      `;
    }).join('') || '<tr><td colspan="10" class="muted">暂无数据</td></tr>';
  }

  async function deactivate(id) {
    if (!confirm(`确认下线商品 #${id} ?`)) return;
    try {
      await Admin.api(`/admin/products/${encodeURIComponent(id)}`, { method: 'DELETE' });
      Admin.toast('已下线');
      await load();
    } catch (e) {
      Admin.toast(`操作失败：${e && e.message ? e.message : '未知错误'}`, 'danger');
    }
  }

  reloadBtn.addEventListener('click', load);
  prevBtn.addEventListener('click', () => { page = Math.max(1, page - 1); load(); });
  nextBtn.addEventListener('click', () => { page = page + 1; load(); });
  sizeEl.addEventListener('change', () => { page = 1; load(); });
  rowsEl.addEventListener('click', (ev) => {
    const t = ev.target;
    if (!(t instanceof HTMLElement)) return;
    const delId = t.getAttribute('data-del');
    if (delId) deactivate(delId);
  });

  load().catch((e) => Admin.toast(`加载失败：${e && e.message ? e.message : '未知错误'}`, 'danger'));
})();
