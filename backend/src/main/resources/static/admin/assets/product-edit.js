(function () {
  if (!Admin.requireAuth()) return;
  Admin.setActiveNav();
  Admin.renderTopbar();

  const q = Admin.qs();
  const id = q.id ? String(q.id) : '';

  const titleEl = document.getElementById('title');
  const saveBtn = document.getElementById('saveBtn');
  const addImgBtn = document.getElementById('addImgBtn');
  const imgRowsEl = document.getElementById('imgRows');
  const pickUploadBtn = document.getElementById('pickUploadBtn');
  const pickUploadInput = document.getElementById('pickUploadInput');

  const els = {
    categoryId: document.getElementById('categoryId'),
    price: document.getElementById('price'),
    discountPrice: document.getElementById('discountPrice'),
    hot: document.getElementById('hot'),
    active: document.getElementById('active'),
    zh_name: document.getElementById('zh_name'),
    zh_brief: document.getElementById('zh_brief'),
    zh_desc: document.getElementById('zh_desc'),
    en_name: document.getElementById('en_name'),
    en_brief: document.getElementById('en_brief'),
    en_desc: document.getElementById('en_desc'),
  };

  let images = [];

  function escapeHtml(s) {
    return String(s)
      .replaceAll('&', '&amp;')
      .replaceAll('<', '&lt;')
      .replaceAll('>', '&gt;')
      .replaceAll('"', '&quot;')
      .replaceAll("'", '&#39;');
  }

  function renderImages() {
    imgRowsEl.innerHTML = images.map((img, idx) => {
      const preview = img.imageUrl ? `<img class="thumb" src="${escapeHtml(img.imageUrl)}" />` : '<span class="muted">-</span>';
      return `
        <tr>
          <td>${preview}</td>
          <td>
            <button type="button" data-img-pick="${idx}">选择图片</button>
            <input data-img-file="${idx}" type="file" accept="image/*" style="display:none;" />
            <div class="muted" style="margin-top:4px;">选择后自动上传</div>
          </td>
          <td><input data-img-url="${idx}" value="${escapeHtml(img.imageUrl || '')}" placeholder="可粘贴 URL，或上传自动填充" /></td>
          <td><input data-img-sort="${idx}" type="number" value="${img.sortOrder ?? 0}" /></td>
          <td>
            <select data-img-cover="${idx}">
              <option value="false" ${img.cover ? '' : 'selected'}>false</option>
              <option value="true" ${img.cover ? 'selected' : ''}>true</option>
            </select>
          </td>
          <td><button class="danger" data-img-del="${idx}">删除</button></td>
        </tr>
      `;
    }).join('') || '<tr><td colspan="6" class="muted">暂无图片</td></tr>';
  }

  async function uploadImage(idx, file) {
    if (!file) return;
    Admin.toast('');
    const fd = new FormData();
    fd.append('file', file);

    const qs = id ? `?productId=${encodeURIComponent(id)}` : '';
    const resp = await Admin.api(`/admin/uploads/product-image${qs}`, { method: 'POST', body: fd });
    const url = resp && (resp.url || resp.imageUrl);
    if (!url) throw new Error('upload response missing url');
    images[idx].imageUrl = url;
    renderImages();
  }

  async function uploadAndAppend(file) {
    Admin.toast('');
    const fd = new FormData();
    fd.append('file', file);
    const qs = id ? `?productId=${encodeURIComponent(id)}` : '';
    const resp = await Admin.api(`/admin/uploads/product-image${qs}`, { method: 'POST', body: fd });
    const url = resp && (resp.url || resp.imageUrl);
    if (!url) throw new Error('upload response missing url');
    images.push({ imageUrl: url, sortOrder: 0, cover: images.length === 0 });
    renderImages();
  }

  async function loadCategories() {
    const cats = await Admin.api('/admin/categories');
    els.categoryId.innerHTML = cats
      .map((c) => `<option value="${c.id}">${c.id} · ${escapeHtml(c.nameZh)} / ${escapeHtml(c.nameEn)}${c.active ? '' : ' (inactive)'}</option>`)
      .join('');
  }

  async function loadDetail() {
    if (!id) {
      titleEl.textContent = '新建商品';
      images = [];
      renderImages();
      return;
    }
    titleEl.textContent = `编辑商品 #${id}`;
    const d = await Admin.api(`/admin/products/${encodeURIComponent(id)}`);
    els.categoryId.value = String(d.categoryId);
    els.price.value = d.price ?? '';
    els.discountPrice.value = d.discountPrice ?? '';
    els.hot.value = String(!!d.hot);
    els.active.value = String(!!d.active);
    els.zh_name.value = (d.zh && d.zh.name) || '';
    els.zh_brief.value = (d.zh && d.zh.brief) || '';
    els.zh_desc.value = (d.zh && d.zh.description) || '';
    els.en_name.value = (d.en && d.en.name) || '';
    els.en_brief.value = (d.en && d.en.brief) || '';
    els.en_desc.value = (d.en && d.en.description) || '';
    images = Array.isArray(d.images)
      ? d.images.map((x) => ({ imageUrl: x.imageUrl || '', sortOrder: x.sortOrder ?? 0, cover: !!x.cover }))
      : [];
    renderImages();
  }

  function buildPayload() {
    const categoryId = Number(els.categoryId.value);
    const price = (els.price.value || '').trim();
    const discountPrice = (els.discountPrice.value || '').trim();

    const zhName = (els.zh_name.value || '').trim();
    const enName = (els.en_name.value || '').trim();
    if (!categoryId || !price || !zhName || !enName) {
      throw new Error('categoryId / price / zh.name / en.name 为必填');
    }

    const payload = {
      categoryId,
      price,
      discountPrice: discountPrice || null,
      hot: els.hot.value === 'true',
      active: els.active.value === 'true',
      zh: {
        name: zhName,
        brief: (els.zh_brief.value || '').trim() || null,
        description: (els.zh_desc.value || '').trim() || null,
      },
      en: {
        name: enName,
        brief: (els.en_brief.value || '').trim() || null,
        description: (els.en_desc.value || '').trim() || null,
      },
      images: images
        .map((x) => ({
          imageUrl: (x.imageUrl || '').trim(),
          sortOrder: Number(x.sortOrder || 0),
          cover: !!x.cover,
        }))
        .filter((x) => !!x.imageUrl),
    };

    return payload;
  }

  async function save() {
    Admin.toast('');
    try {
      const payload = buildPayload();
      if (id) {
        await Admin.api(`/admin/products/${encodeURIComponent(id)}`, { method: 'PUT', body: payload });
        Admin.toast('已保存');
      } else {
        const created = await Admin.api('/admin/products', { method: 'POST', body: payload });
        Admin.toast('已创建');
        if (created && created.id) {
          location.href = `product-edit.html?id=${encodeURIComponent(created.id)}`;
          return;
        }
      }
    } catch (e) {
      Admin.toast(`保存失败：${e && e.message ? e.message : '未知错误'}`, 'danger');
    }
  }

  addImgBtn.addEventListener('click', () => {
    images.push({ imageUrl: '', sortOrder: 0, cover: false });
    renderImages();
  });

  if (pickUploadBtn && pickUploadInput) {
    pickUploadBtn.addEventListener('click', () => {
      pickUploadInput.click();
    });
    pickUploadInput.addEventListener('change', () => {
      const file = pickUploadInput.files && pickUploadInput.files[0] ? pickUploadInput.files[0] : null;
      if (!file) return;
      uploadAndAppend(file)
        .then(() => Admin.toast('上传成功'))
        .catch((e) => Admin.toast(`上传失败：${e && e.message ? e.message : '未知错误'}`, 'danger'))
        .finally(() => {
          pickUploadInput.value = '';
        });
    });
  }
  imgRowsEl.addEventListener('click', (ev) => {
    const t = ev.target;
    if (!(t instanceof HTMLElement)) return;
    const del = t.getAttribute('data-img-del');
    if (del != null) {
      const idx = Number(del);
      images.splice(idx, 1);
      renderImages();
    }

    const pick = t.getAttribute('data-img-pick');
    if (pick != null) {
      const idx = Number(pick);
      const input = imgRowsEl.querySelector(`input[data-img-file="${idx}"]`);
      if (input && input instanceof HTMLInputElement) {
        input.click();
      }
    }
  });
  imgRowsEl.addEventListener('input', (ev) => {
    const t = ev.target;
    if (!(t instanceof HTMLElement)) return;
    const urlIdx = t.getAttribute('data-img-url');
    const sortIdx = t.getAttribute('data-img-sort');
    const coverIdx = t.getAttribute('data-img-cover');
    if (urlIdx != null) images[Number(urlIdx)].imageUrl = t.value;
    if (sortIdx != null) images[Number(sortIdx)].sortOrder = Number(t.value || 0);
    if (coverIdx != null) images[Number(coverIdx)].cover = t.value === 'true';
  });

  imgRowsEl.addEventListener('change', (ev) => {
    const t = ev.target;
    if (!(t instanceof HTMLInputElement)) return;
    const fileIdx = t.getAttribute('data-img-file');
    if (fileIdx != null) {
      const idx = Number(fileIdx);
      const files = t.files;
      const file = files && files[0] ? files[0] : null;
      if (!file) return;
      uploadImage(idx, file)
        .then(() => Admin.toast('上传成功'))
        .catch((e) => Admin.toast(`上传失败：${e && e.message ? e.message : '未知错误'}`, 'danger'));
    }
  });
  saveBtn.addEventListener('click', save);

  Promise.resolve()
    .then(loadCategories)
    .then(loadDetail)
    .catch((e) => Admin.toast(`加载失败：${e && e.message ? e.message : '未知错误'}`, 'danger'));
})();
