const { t, getLang } = require('../../utils/i18n')
const { getContact } = require('../../utils/api')

Page({
  data: {
    lang: getLang(),
    loading: false,
    contact: {
      wechatId: '',
      qrcodeUrl: '',
    },
    labels: {
      title: t('pages.contact.title', 'Contact'),
      wechatId: t('pages.contact.wechatId', 'WeChat'),
      copy: t('pages.contact.copy', 'Copy'),
      copied: t('pages.contact.copied', 'Copied'),
      loading: t('common.loading', 'Loading'),
    },
  },
  onLoad() {
    this.refreshLabels()
    wx.setNavigationBarTitle({ title: this.data.labels.title })
    this.load()
  },
  onShow() {
    this.refreshLabels()
    wx.setNavigationBarTitle({ title: this.data.labels.title })
  },
  onPullDownRefresh() {
    this.load().finally(() => wx.stopPullDownRefresh())
  },
  refreshLabels() {
    this.setData({
      lang: getLang(),
      labels: {
        title: t('pages.contact.title', 'Contact'),
        wechatId: t('pages.contact.wechatId', 'WeChat'),
        copy: t('pages.contact.copy', 'Copy'),
        copied: t('pages.contact.copied', 'Copied'),
        loading: t('common.loading', 'Loading'),
      },
    })
  },
  load() {
    this.setData({ loading: true })
    return getContact()
      .then((raw) => {
        const data = raw || {}
        this.setData({
          contact: {
            wechatId: data.wechatId || data.wechat_id || '',
            qrcodeUrl: data.qrcodeUrl || data.qrcode_url || '',
          },
        })
      })
      .catch(() => {
        wx.showToast({ title: t('common.networkError', 'Network error'), icon: 'none' })
      })
      .finally(() => {
        this.setData({ loading: false })
      })
  },
  onCopy() {
    const text = this.data.contact.wechatId
    if (!text) return
    wx.setClipboardData({
      data: text,
      success: () => {
        wx.showToast({ title: this.data.labels.copied, icon: 'none' })
      },
    })
  },
})
