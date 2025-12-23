const { t, setLang, getLang } = require('../../utils/i18n')
const { getProducts } = require('../../utils/api')
const { ensureLogin } = require('../../utils/auth')

Page({
  data: {
    lang: getLang(),
    labels: {
      brand: t('pages.index.title', 'FluffyLetter'),
      about: t('pages.about.title', 'About'),
      search: t('pages.index.search', 'Search'),
      favorites: t('pages.favorites.title', 'Favorites'),
      contact: t('pages.contact.title', 'Contact'),
      recommend: t('pages.index.recommend', 'Recommended'),
      empty: t('pages.index.empty', 'No products'),
      loading: t('common.loading', 'Loading'),
    },
    banners: [],
    recommended: [],
    loading: false,
  },
  onLoad() {
    wx.setNavigationBarTitle({ title: t('pages.index.title', 'FluffyLetter') })
    this.refreshLabels()
    this.loadHome()
  },
  onShow() {
    // 语言切换后返回首页，刷新文案/数据
    wx.setNavigationBarTitle({ title: t('pages.index.title', 'FluffyLetter') })
    this.refreshLabels()
  },
  onPullDownRefresh() {
    this.loadHome().finally(() => wx.stopPullDownRefresh())
  },
  onToggleLang() {
    const next = this.data.lang === 'zh' ? 'en' : 'zh'
    setLang(next)
    this.refreshLabels()
    wx.setNavigationBarTitle({ title: t('pages.index.title', 'FluffyLetter') })
    this.loadHome()
  },
  refreshLabels() {
    this.setData({
      lang: getLang(),
      labels: {
        brand: t('pages.index.title', 'FluffyLetter'),
        about: t('pages.about.title', 'About'),
        search: t('pages.index.search', 'Search'),
        favorites: t('pages.favorites.title', 'Favorites'),
        contact: t('pages.contact.title', 'Contact'),
        recommend: t('pages.index.recommend', 'Recommended'),
        empty: t('pages.index.empty', 'No products'),
        loading: t('common.loading', 'Loading'),
      },
    })
  },
  onTapBrand() {
    wx.navigateTo({ url: '/pages/about/about' })
  },
  onTapSearch() {
    wx.navigateTo({ url: '/pages/category/category?focus=1' })
  },
  onTapFavorites() {
    wx.navigateTo({ url: '/pages/favorites/favorites' })
  },
  onTapContact() {
    wx.navigateTo({ url: '/pages/contact/contact' })
  },
  onTapProduct(e) {
    const id = e.currentTarget.dataset.id
    if (!id) return
    wx.navigateTo({ url: `/pages/product-detail/product-detail?id=${id}` })
  },
  loadHome() {
    this.setData({ loading: true })
    return ensureLogin()
      .catch(() => '')
      .then(() => getProducts({ page: 1, size: 20 }))
      .then((list) => {
        const items = (list || []).map(normalizeProductListItem)
        const banners = items
          .map((x) => x.coverImage)
          .filter(Boolean)
          .slice(0, 5)

        this.setData({
          recommended: items.slice(0, 6),
          banners,
        })
      })
      .catch(() => {
        wx.showToast({ title: t('common.networkError', 'Network error'), icon: 'none' })
      })
      .finally(() => {
        this.setData({ loading: false })
      })
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
