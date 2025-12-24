(function () {
  if (!Admin.requireAuth()) return;
  Admin.setActiveNav();
  Admin.renderTopbar();

  const curWechatId = document.getElementById('curWechatId');
  const curQrcodeUrl = document.getElementById('curQrcodeUrl');
  const qrcodePreview = document.getElementById('qrcodePreview');
  const wechatIdEl = document.getElementById('wechatId');
  const qrcodeUrlEl = document.getElementById('qrcodeUrl');
  const saveBtn = document.getElementById('saveBtn');
  const reloadBtn = document.getElementById('reloadBtn');

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
    const d = await Admin.api('/admin/contact');
    curWechatId.textContent = d.wechatId || '-';
    curQrcodeUrl.textContent = d.qrcodeUrl || '-';
    wechatIdEl.value = d.wechatId || '';
    qrcodeUrlEl.value = d.qrcodeUrl || '';
    qrcodePreview.innerHTML = d.qrcodeUrl
      ? `<div class="muted" style="margin-bottom:6px;">预览</div><img class="thumb" style="width:160px;height:160px;" src="${escapeHtml(d.qrcodeUrl)}" />`
      : '<div class="muted">无二维码</div>';
  }

  async function save() {
    Admin.toast('');
    try {
      const payload = {
        wechatId: (wechatIdEl.value || '').trim() || null,
        qrcodeUrl: (qrcodeUrlEl.value || '').trim() || null,
      };
      await Admin.api('/admin/contact', { method: 'PUT', body: payload });
      Admin.toast('已保存');
      await load();
    } catch (e) {
      Admin.toast(`保存失败：${e && e.message ? e.message : '未知错误'}`, 'danger');
    }
  }

  reloadBtn.addEventListener('click', load);
  saveBtn.addEventListener('click', save);
  load().catch((e) => Admin.toast(`加载失败：${e && e.message ? e.message : '未知错误'}`, 'danger'));
})();
