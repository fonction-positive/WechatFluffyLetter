(function () {
  const TOKEN_KEY = 'fluffy_admin_token';
  const ROLE_KEY = 'fluffy_admin_role';

  function getToken() {
    return localStorage.getItem(TOKEN_KEY) || '';
  }

  function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token || '');
  }

  function getRole() {
    return localStorage.getItem(ROLE_KEY) || '';
  }

  function setRole(role) {
    localStorage.setItem(ROLE_KEY, role || '');
  }

  function clearAuth() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(ROLE_KEY);
  }

  function qs() {
    const params = new URLSearchParams(location.search);
    const out = {};
    for (const [k, v] of params.entries()) out[k] = v;
    return out;
  }

  function fmtDate(v) {
    if (!v) return '';
    try {
      const d = new Date(v);
      if (Number.isNaN(d.getTime())) return String(v);
      return d.toLocaleString();
    } catch {
      return String(v);
    }
  }

  function setActiveNav() {
    const here = location.pathname.split('/').pop() || 'index.html';
    document.querySelectorAll('[data-nav] a').forEach((a) => {
      const href = (a.getAttribute('href') || '').split('#')[0];
      const target = href.split('/').pop();
      if (target === here) a.classList.add('active');
      else a.classList.remove('active');
    });
  }

  function redirectToLogin() {
    const returnUrl = encodeURIComponent(location.pathname + location.search);
    location.href = `login.html?returnUrl=${returnUrl}`;
  }

  function requireAuth() {
    if (!getToken()) {
      redirectToLogin();
      return false;
    }
    return true;
  }

  async function api(path, options = {}) {
    const method = options.method || 'GET';
    const token = getToken();
    const headers = Object.assign({}, options.headers || {});
    headers['Accept'] = headers['Accept'] || 'application/json';
    if (token) headers['Authorization'] = headers['Authorization'] || `Bearer ${token}`;

    let body = options.body;
    if (body && typeof body === 'object' && !(body instanceof FormData)) {
      headers['Content-Type'] = headers['Content-Type'] || 'application/json';
      body = JSON.stringify(body);
    }

    const resp = await fetch(path, { method, headers, body });
    if (resp.status === 401 || resp.status === 403) {
      clearAuth();
      redirectToLogin();
      throw new Error('unauthorized');
    }

    const text = await resp.text();
    if (!resp.ok) {
      throw new Error(text || `HTTP ${resp.status}`);
    }

    if (!text) return null;
    try {
      return JSON.parse(text);
    } catch {
      return text;
    }
  }

  function renderTopbar() {
    const token = getToken();
    const role = getRole();
    const statusEl = document.querySelector('[data-login-status]');
    const logoutBtn = document.querySelector('[data-logout]');

    if (statusEl) {
      statusEl.textContent = token ? `已登录${role ? ' · ' + role : ''}` : '未登录';
    }
    if (logoutBtn) {
      logoutBtn.style.display = token ? 'inline-block' : 'none';
      logoutBtn.addEventListener('click', () => {
        clearAuth();
        redirectToLogin();
      });
    }
  }

  function toast(msg, kind) {
    const el = document.querySelector('[data-toast]');
    if (!el) return;
    el.textContent = msg || '';
    el.style.display = msg ? 'block' : 'none';
    el.classList.toggle('danger', kind === 'danger');
  }

  window.Admin = {
    getToken,
    setToken,
    getRole,
    setRole,
    clearAuth,
    requireAuth,
    api,
    qs,
    fmtDate,
    setActiveNav,
    renderTopbar,
    toast,
  };
})();
