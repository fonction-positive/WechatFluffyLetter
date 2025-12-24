const { t, getLang } = require('../../utils/i18n')
const { getFavorites, removeFavorite } = require('../../utils/api')
const { ensureLogin } = require('../../utils/auth')
const { buildUrl } = require('../../utils/request')

const FAV_CHANGED_KEY = 'favChanged'

Page({
  data: {
    lang: getLang(),
    items: [],
    page: 1,
    size: 20,
    hasMore: true,
    loading: false,
    labels: {
      title: t('pages.favorites.title', 'Favorites'),
      empty: t('pages.favorites.empty', 'No favorites'),
      loading: t('common.loading', 'Loading'),
      noMore: t('pages.index.noMore', 'No more'),
      remove: '取消',
    },
  },
  onLoad() {
    this.refreshLabels()
    wx.setNavigationBarTitle({ title: this.data.labels.title })
    this.reload()
  },
  onShow() {
    this.refreshLabels()
    wx.setNavigationBarTitle({ title: this.data.labels.title })
    this.applyFavChanged()
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
        title: t('pages.favorites.title', 'Favorites'),
        empty: t('pages.favorites.empty', 'No favorites'),
        loading: t('common.loading', 'Loading'),
        noMore: t('pages.index.noMore', 'No more'),
        remove: this.data.lang === 'zh' ? '取消' : 'Remove',
      },
    })
  },
  reload() {
    this.setData({ page: 1, items: [], hasMore: true })
    return this.loadMore(true)
  },
  loadMore(isReload = false) {
    if (this.data.loading) return Promise.resolve()
    if (!this.data.hasMore && !isReload) return Promise.resolve()

    this.setData({ loading: true })
    return ensureLogin().then((token) => {
      if (!token) {
        wx.showToast({ title: t('common.needLogin', 'Please login'), icon: 'none' })
        return []
      }
      return getFavorites({ page: this.data.page, size: this.data.size })
    })
      .then((list) => {
        const items = (list || []).map(normalizeProductListItem)
        const next = this.data.items.concat(items)
        const hasMore = items.length >= this.data.size
        this.setData({
          items: next,
          page: this.data.page + 1,
          hasMore,
        })
      })
      .catch((err) => {
        if (err && err.statusCode === 401) {
          wx.showToast({ title: 'Unauthorized', icon: 'none' })
          return
        }
        wx.showToast({ title: t('common.networkError', 'Network error'), icon: 'none' })
      })
      .finally(() => {
        this.setData({ loading: false })
      })
  },
  onTapItem(e) {
    const id = e.currentTarget.dataset.id
    if (!id) return
    wx.navigateTo({ url: `/pages/product-detail/product-detail?id=${id}` })
  },
  onRemove(e) {
    const id = e.currentTarget.dataset.id
    if (!id) return

    // 阻止 card click
    if (e && e.stopPropagation) e.stopPropagation()

    return ensureLogin().then((token) => {
      if (!token) {
        wx.showToast({ title: t('common.needLogin', 'Please login'), icon: 'none' })
        return
      }
      return removeFavorite(id)
    })
      .then(() => {
        const next = this.data.items.filter((x) => String(x.id) !== String(id))
        this.setData({ items: next })

        try {
          wx.setStorageSync(FAV_CHANGED_KEY, { id, favorited: false, ts: Date.now() })
        } catch (e) {}
      })
      .catch(() => {
        wx.showToast({ title: t('common.networkError', 'Network error'), icon: 'none' })
      })
  },

  applyFavChanged() {
    let changed = null
    try {
      changed = wx.getStorageSync(FAV_CHANGED_KEY)
    } catch (e) {
      changed = null
    }
    if (!changed || !changed.id) return
    if (changed.favorited !== false) return

    const idKey = String(changed.id)
    const next = (this.data.items || []).filter((x) => String(x.id) !== idKey)
    if (next.length !== (this.data.items || []).length) this.setData({ items: next })
  },
})

function normalizeProductListItem(raw) {
  const item = raw || {}
  const cover = item.coverImage || item.cover_image || ''
  return {
    id: item.id,
    name: item.name || '',
    brief: item.brief || '',
    price: item.price,
    discountPrice: item.discountPrice != null ? item.discountPrice : item.discount_price,
    coverImage: cover ? buildUrl(cover) : '',
  }
}
