const { t, getLang } = require('../../utils/i18n')
const { getProductDetail, addFavorite, removeFavorite } = require('../../utils/api')
const { ensureLogin } = require('../../utils/auth')

const FAV_CHANGED_KEY = 'favChanged'

Page({
  data: {
    lang: getLang(),
    id: '',
    loading: false,
    favLoading: false,
    product: {},
    images: [],
    favorited: false,
    labels: {
      favorited: t('pages.product.favorited', 'Favorited'),
      unfavorited: t('pages.product.unfavorited', 'Favorite'),
      loading: t('common.loading', 'Loading'),
    },
  },
  onLoad(query) {
    const id = query && query.id
    this.setData({ id: id || '' })
    wx.setNavigationBarTitle({ title: t('pages.index.title', 'FluffyLetter') })
    this.refreshLabels()
    this.load()
  },
  onShow() {
    this.refreshLabels()
  },
  onPullDownRefresh() {
    this.load().finally(() => wx.stopPullDownRefresh())
  },
  refreshLabels() {
    this.setData({
      lang: getLang(),
      labels: {
        favorited: t('pages.product.favorited', 'Favorited'),
        unfavorited: t('pages.product.unfavorited', 'Favorite'),
        loading: t('common.loading', 'Loading'),
      },
    })
  },
  load() {
    const id = this.data.id
    if (!id) {
      wx.showToast({ title: 'Missing id', icon: 'none' })
      return Promise.resolve()
    }

    this.setData({ loading: true })
    return ensureLogin()
      .catch(() => '')
      .then(() => getProductDetail(id))
      .then((raw) => {
        const product = normalizeProductDetail(raw)
        const images = Array.isArray(product.images) ? product.images : []
        const favorited = !!product.favorited

        wx.setNavigationBarTitle({ title: product.name || t('pages.index.title', 'FluffyLetter') })

        this.setData({ product, images, favorited })
      })
      .catch(() => {
        wx.showToast({ title: t('common.networkError', 'Network error'), icon: 'none' })
      })
      .finally(() => {
        this.setData({ loading: false })
      })
  },
  onToggleFavorite() {
    if (this.data.favLoading) return

    const id = this.data.id
    if (!id) return

    this.setData({ favLoading: true })
    return ensureLogin().then((token) => {
      if (!token) {
        wx.showToast({ title: t('common.needLogin', 'Please login'), icon: 'none' })
        return
      }
        if (this.data.favorited) return removeFavorite(id)
        return addFavorite(id)
      })
      .then(() => {
        const next = !this.data.favorited
        this.setData({ favorited: next })
        try {
          wx.setStorageSync(FAV_CHANGED_KEY, { id, favorited: next, ts: Date.now() })
        } catch (e) {}
        wx.showToast({ title: next ? this.data.labels.favorited : this.data.labels.unfavorited, icon: 'none' })
      })
      .catch((err) => {
        if (err && err.statusCode === 401) {
          wx.showToast({ title: 'Unauthorized', icon: 'none' })
          return
        }
        wx.showToast({ title: t('common.networkError', 'Network error'), icon: 'none' })
      })
      .finally(() => {
        this.setData({ favLoading: false })
      })
  },
})

function normalizeProductDetail(raw) {
  const item = raw || {}
  return {
    id: item.id,
    name: item.name || '',
    brief: item.brief || '',
    description: item.description || item.desc || '',
    price: item.price,
    discountPrice: item.discountPrice != null ? item.discountPrice : item.discount_price,
    images: item.images || item.imageUrls || item.image_urls || [],
    coverImage: item.coverImage || item.cover_image || '',
    favorited: !!(item.favorited != null ? item.favorited : item.is_favorited),
  }
}
