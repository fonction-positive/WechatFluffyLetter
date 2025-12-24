(function () {
  if (!Admin.requireAuth()) return;
  Admin.setActiveNav();
  Admin.renderTopbar();

  const els = {
    rows: document.getElementById('rows'),
    reloadBtn: document.getElementById('reloadBtn'),
    editId: document.getElementById('editId'),
    username: document.getElementById('username'),
    role: document.getElementById('role'),
    password: document.getElementById('password'),
    createBtn: document.getElementById('createBtn'),
    saveRoleBtn: document.getElementById('saveRoleBtn'),
    resetPwdBtn: document.getElementById('resetPwdBtn'),
    clearBtn: document.getElementById('clearBtn'),
  };

  let items = [];

  function escapeHtml(s) {
    return String(s)
      .replaceAll('&', '&amp;')
      .replaceAll('<', '&lt;')
      .replaceAll('>', '&gt;')
      .replaceAll('"', '&quot;')
      .replaceAll("'", '&#39;');
  }

  function resetForm() {
    els.editId.value = '';
    els.username.value = '';
    els.role.value = 'admin';
    els.password.value = '';
    Admin.toast('');
  }

  function fillForm(item) {
    els.editId.value = String(item.id);
    els.username.value = item.username || '';
    els.role.value = item.role || 'admin';
    els.password.value = '';
    Admin.toast('');
  }

  function render() {
    els.rows.innerHTML = items.map((u) => {
      return `
        <tr>
          <td>${u.id}</td>
          <td>${escapeHtml(u.username || '')}</td>
          <td>${escapeHtml(u.role || '')}</td>
          <td>
            <div class="actions">
              <button data-edit="${u.id}">编辑</button>
              <button class="danger" data-del="${u.id}">删除</button>
            </div>
          </td>
        </tr>
      `;
    }).join('') || '<tr><td colspan="4" class="muted">暂无数据</td></tr>';
  }

  async function load() {
    Admin.toast('');
    try {
      items = await Admin.api('/admin/admin-users');
      render();
    } catch (e) {
      if (e && e.message === 'forbidden') {
        Admin.toast('无权限：需要 superadmin 才能管理管理员账号', 'danger');
        items = [];
        render();
        return;
      }
      Admin.toast(`加载失败：${e && e.message ? e.message : '未知错误'}`, 'danger');
    }
  }

  async function create() {
    Admin.toast('');
    const username = (els.username.value || '').trim();
    const password = els.password.value || '';
    const role = (els.role.value || '').trim();
    if (!username || !password) {
      Admin.toast('创建需要 username + password', 'danger');
      return;
    }
    try {
      await Admin.api('/admin/admin-users', { method: 'POST', body: { username, password, role } });
      Admin.toast('已创建');
      resetForm();
      await load();
    } catch (e) {
      Admin.toast(`创建失败：${e && e.message ? e.message : '未知错误'}`, 'danger');
    }
  }

  async function saveRole() {
    Admin.toast('');
    const id = (els.editId.value || '').trim();
    if (!id) {
      Admin.toast('请先从列表选择一个用户', 'danger');
      return;
    }
    try {
      await Admin.api(`/admin/admin-users/${encodeURIComponent(id)}/role`, { method: 'PUT', body: { role: els.role.value } });
      Admin.toast('角色已更新');
      await load();
    } catch (e) {
      Admin.toast(`更新失败：${e && e.message ? e.message : '未知错误'}`, 'danger');
    }
  }

  async function resetPwd() {
    Admin.toast('');
    const id = (els.editId.value || '').trim();
    const password = els.password.value || '';
    if (!id) {
      Admin.toast('请先从列表选择一个用户', 'danger');
      return;
    }
    if (!password) {
      Admin.toast('请输入新密码', 'danger');
      return;
    }
    try {
      await Admin.api(`/admin/admin-users/${encodeURIComponent(id)}/password`, { method: 'PUT', body: { password } });
      Admin.toast('密码已重置');
      els.password.value = '';
    } catch (e) {
      Admin.toast(`重置失败：${e && e.message ? e.message : '未知错误'}`, 'danger');
    }
  }

  async function del(id) {
    if (!confirm(`确认删除管理员 #${id} ?`)) return;
    try {
      await Admin.api(`/admin/admin-users/${encodeURIComponent(id)}`, { method: 'DELETE' });
      Admin.toast('已删除');
      resetForm();
      await load();
    } catch (e) {
      Admin.toast(`删除失败：${e && e.message ? e.message : '未知错误'}`, 'danger');
    }
  }

  els.reloadBtn.addEventListener('click', load);
  els.clearBtn.addEventListener('click', resetForm);
  els.createBtn.addEventListener('click', create);
  els.saveRoleBtn.addEventListener('click', saveRole);
  els.resetPwdBtn.addEventListener('click', resetPwd);
  els.rows.addEventListener('click', (ev) => {
    const t = ev.target;
    if (!(t instanceof HTMLElement)) return;
    const editId = t.getAttribute('data-edit');
    const delId = t.getAttribute('data-del');
    if (editId) {
      const item = items.find((x) => String(x.id) === String(editId));
      if (item) fillForm(item);
    }
    if (delId) del(delId);
  });

  resetForm();
  load();
})();
