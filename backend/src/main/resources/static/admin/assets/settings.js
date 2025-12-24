(function () {
  if (!Admin.requireAuth()) return;
  Admin.setActiveNav();
  Admin.renderTopbar();

  const originEl = document.getElementById('origin');
  const tokenEl = document.getElementById('token');
  const roleEl = document.getElementById('role');
  const clearBtn = document.getElementById('clearBtn');

  originEl.textContent = location.origin;
  const token = Admin.getToken();
  tokenEl.textContent = token ? (token.length > 24 ? token.slice(0, 12) + '…' + token.slice(-8) : token) : '-';
  roleEl.textContent = Admin.getRole() || '-';

  clearBtn.addEventListener('click', () => {
    Admin.clearAuth();
    Admin.toast('已清除登录态');
    setTimeout(() => location.href = 'login.html', 400);
  });
})();
