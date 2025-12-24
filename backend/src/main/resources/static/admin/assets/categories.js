(function () {
  if (!Admin.requireAuth()) return;
  Admin.setActiveNav();
  Admin.renderTopbar();

  const els = {
    rows: document.getElementById('rows'),
    reloadBtn: document.getElementById('reloadBtn'),
    saveBtn: document.getElementById('saveBtn'),
    resetBtn: document.getElementById('resetBtn'),
    editId: document.getElementById('editId'),
    code: document.getElementById('code'),
    nameZh: document.getElementById('nameZh'),
    nameEn: document.getElementById('nameEn'),
    sortOrder: document.getElementById('sortOrder'),
    active: document.getElementById('active'),
    formTitle: document.getElementById('formTitle'),
  };

  let items = [];

  function resetForm() {
    els.editId.value = '';
    els.code.value = '';
    els.nameZh.value = '';
    els.nameEn.value = '';
    els.sortOrder.value = '0';
    els.active.value = 'true';
    els.formTitle.textContent = '新建/编辑';
    Admin.toast('');
  }

  function fillForm(item) {
    els.editId.value = String(item.id);
    els.code.value = item.code || '';
    els.nameZh.value = item.nameZh || '';
    els.nameEn.value = item.nameEn || '';
    els.sortOrder.value = String(item.sortOrder ?? 0);
    els.active.value = String(!!item.active);
    els.formTitle.textContent = `编辑 #${item.id}`;
    Admin.toast('');
  }

  function tr(item) {
    const activeText = item.active ? 'true' : 'false';
    return `
      <tr>
        <td>${item.id}</td>
        <td>${escapeHtml(item.code || '')}</td>
        <td>${escapeHtml(item.nameZh || '')}</td>
        <td>${escapeHtml(item.nameEn || '')}</td>
        <td>${item.sortOrder ?? 0}</td>
        <td>${activeText}</td>
        <td>
          <div class="actions">
            <button data-edit="${item.id}">编辑</button>
            <button class="danger" data-del="${item.id}">下线</button>
          </div>
        </td>
      </tr>
    `;
  }

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
    items = await Admin.api('/admin/categories');
    els.rows.innerHTML = items.map(tr).join('') || '<tr><td colspan="7" class="muted">暂无数据</td></tr>';
  }

  async function save() {
    Admin.toast('');
    const payload = {
      code: (els.code.value || '').trim(),
      nameZh: (els.nameZh.value || '').trim(),
      nameEn: (els.nameEn.value || '').trim(),
      sortOrder: Number(els.sortOrder.value || 0),
      active: els.active.value === 'true',
    };
    if (!payload.code || !payload.nameZh || !payload.nameEn) {
      Admin.toast('code / 中文名 / 英文名 为必填', 'danger');
      return;
    }

    const id = (els.editId.value || '').trim();
    try {
      if (id) {
        await Admin.api(`/admin/categories/${encodeURIComponent(id)}`, { method: 'PUT', body: payload });
        Admin.toast('已保存');
      } else {
        await Admin.api('/admin/categories', { method: 'POST', body: payload });
        Admin.toast('已创建');
      }
      resetForm();
      await load();
    } catch (e) {
      Admin.toast(`保存失败：${e && e.message ? e.message : '未知错误'}`, 'danger');
    }
  }

  async function deactivate(id) {
    if (!confirm(`确认下线分类 #${id} ?`)) return;
    try {
      await Admin.api(`/admin/categories/${encodeURIComponent(id)}`, { method: 'DELETE' });
      Admin.toast('已下线');
      await load();
    } catch (e) {
      Admin.toast(`操作失败：${e && e.message ? e.message : '未知错误'}`, 'danger');
    }
  }

  els.reloadBtn.addEventListener('click', load);
  els.resetBtn.addEventListener('click', resetForm);
  els.saveBtn.addEventListener('click', save);
  els.rows.addEventListener('click', (ev) => {
    const t = ev.target;
    if (!(t instanceof HTMLElement)) return;
    const editId = t.getAttribute('data-edit');
    const delId = t.getAttribute('data-del');
    if (editId) {
      const item = items.find((x) => String(x.id) === String(editId));
      if (item) fillForm(item);
    }
    if (delId) deactivate(delId);
  });

  resetForm();
  load().catch((e) => Admin.toast(`加载失败：${e && e.message ? e.message : '未知错误'}`, 'danger'));
})();
