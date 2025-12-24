(function () {
  Admin.renderTopbar();

  const q = Admin.qs();
  const returnUrl = q.returnUrl ? String(q.returnUrl) : 'index.html';
  const usernameEl = document.getElementById('username');
  const passwordEl = document.getElementById('password');
  const loginBtn = document.getElementById('loginBtn');
  const hint = document.getElementById('hint');

  if (Admin.getToken()) {
    location.href = returnUrl;
    return;
  }

  function setBusy(busy) {
    loginBtn.disabled = busy;
    hint.textContent = busy ? '登录中…' : '';
  }

  async function doLogin() {
    Admin.toast('');
    const username = (usernameEl.value || '').trim();
    const password = passwordEl.value || '';
    if (!username || !password) {
      Admin.toast('请输入用户名和密码', 'danger');
      return;
    }

    setBusy(true);
    try {
      const data = await Admin.api('/admin/login', { method: 'POST', body: { username, password } });
      Admin.setToken(data.token);
      Admin.setRole(data.role);
      location.href = returnUrl;
    } catch (e) {
      Admin.toast(`登录失败：${e && e.message ? e.message : '未知错误'}`, 'danger');
    } finally {
      setBusy(false);
    }
  }

  loginBtn.addEventListener('click', doLogin);
  passwordEl.addEventListener('keydown', (ev) => {
    if (ev.key === 'Enter') doLogin();
  });
})();
