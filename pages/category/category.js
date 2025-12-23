const { t, getLang } = require('../../utils/i18n')
const { getCategories, getProducts } = require('../../utils/api')
const { ensureLogin } = require('../../utils/auth')

Page({
  data: {
    lang: getLang(),
    labels: {
      title: t('pages.category.title', 'Category'),
      all: t('pages.index.all', 'All'),
      empty: t('pages.index.empty', 'No products'),
      loading: t('common.loading', 'Loading'),
      noMore: t('pages.index.noMore', 'No more'),
      searchPlaceholder: t('pages.category.searchPlaceholder', 'Search products'),
    },
    keyword: '',
    keywordFocus: false,
    categories: [],
    activeCategoryId: 0,
    products: [],
    displayProducts: [],
    page: 1,
    size: 10,
    hasMore: true,
    loading: false,
  },
  onLoad(query) {
    const focus = query && String(query.focus || '') === '1'
    this.refreshLabels()
    wx.setNavigationBarTitle({ title: this.data.labels.title })
    this.setData({ keywordFocus: focus })
    this.bootstrap()
  },
  onShow() {
    this.refreshLabels()
    wx.setNavigationBarTitle({ title: this.data.labels.title })
  },
  onPullDownRefresh() {
    this.reload().finally(() => wx.stopPullDownRefresh())
  },
  onReachBottom() {
    this.loadMore()
  },
  refreshLabels() {
    this.setData({
      lang: getLang(),
      labels: {
        title: t('pages.category.title', 'Category'),
        all: t('pages.index.all', 'All'),
        empty: t('pages.index.empty', 'No products'),
        loading: t('common.loading', 'Loading'),
        noMore: t('pages.index.noMore', 'No more'),
        searchPlaceholder: t('pages.category.searchPlaceholder', 'Search products'),
      },
    })
  },
  bootstrap() {
    return ensureLogin()
      .catch(() => '')
      .then(() => this.loadCategories())
      .then(() => this.reload())
  },
  loadCategories() {
    return getCategories()
      .then((list) => {
        const categories = [{ id: 0, name: this.data.labels.all }].concat(list || [])
        this.setData({ categories })
      })
      .catch(() => {
        this.setData({ categories: [{ id: 0, name: this.data.labels.all }] })
      })
  },
  onSelectCategory(e) {
    const id = Number(e.currentTarget.dataset.id || 0)
    if (id === this.data.activeCategoryId) return
    this.setData({ activeCategoryId: id })
    this.reload()
  },
  onKeywordInput(e) {
    const keyword = (e && e.detail && e.detail.value) || ''
    this.setData({ keyword })
    this.applyFilter()
  },
  onKeywordConfirm() {
    // 后端暂未提供关键词检索参数，这里做前端过滤；重新拉取第一页更贴近“搜索”的体验
    this.reload()
  },
  reload() {
    this.setData({ page: 1, products: [], displayProducts: [], hasMore: true })
    return this.loadMore(true)
  },
  loadMore(isReload = false) {
    if (this.data.loading) return Promise.resolve()
    if (!this.data.hasMore && !isReload) return Promise.resolve()

    this.setData({ loading: true })

    const categoryId = this.data.activeCategoryId || 0
    return getProducts({
      categoryId: categoryId ? categoryId : undefined,
      page: this.data.page,
      size: this.data.size,
    })
      .then((list) => {
        const items = (list || []).map(normalizeProductListItem)
        const next = this.data.products.concat(items)
        const hasMore = items.length >= this.data.size
        this.setData({
          products: next,
          page: this.data.page + 1,
          hasMore,
        })
        this.applyFilter()
      })
      .catch(() => {
        wx.showToast({ title: t('common.networkError', 'Network error'), icon: 'none' })
      })
      .finally(() => {
        this.setData({ loading: false })
      })
  },
  applyFilter() {
    const keyword = (this.data.keyword || '').trim().toLowerCase()
    if (!keyword) {
      this.setData({ displayProducts: this.data.products })
      return
    }

    const filtered = (this.data.products || []).filter((p) => {
      const name = String(p.name || '').toLowerCase()
      return name.includes(keyword)
    })
    this.setData({ displayProducts: filtered })
  },
  onTapProduct(e) {
    const id = e.currentTarget.dataset.id
    if (!id) return
    wx.navigateTo({ url: `/pages/product-detail/product-detail?id=${id}` })
  },
})

function normalizeProductListItem(raw) {
  const item = raw || {}
  return {
    id: item.id,
    name: item.name || '',
    brief: item.brief || '',
    price: item.price,
    discountPrice: item.discountPrice != null ? item.discountPrice : item.discount_price,
    hot: !!(item.hot != null ? item.hot : item.is_hot),
    coverImage: item.coverImage || item.cover_image || '',
    favorited: !!(item.favorited != null ? item.favorited : item.is_favorited),
  }
}
