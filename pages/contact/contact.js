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
      scanHint: t('pages.contact.scanHint', 'Scan the QR code'),
      processTitle: t('pages.contact.processTitle', 'Purchase process'),
      step1: t('pages.contact.step1', 'Scan the QR code to contact us'),
      step2: t('pages.contact.step2', 'Choose the product and confirm details'),
      step3: t('pages.contact.step3', 'Pay and provide shipping information'),
      step4: t('pages.contact.step4', 'We ship after confirmation'),
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
        scanHint: t('pages.contact.scanHint', 'Scan the QR code'),
        processTitle: t('pages.contact.processTitle', 'Purchase process'),
        step1: t('pages.contact.step1', 'Scan the QR code to contact us'),
        step2: t('pages.contact.step2', 'Choose the product and confirm details'),
        step3: t('pages.contact.step3', 'Pay and provide shipping information'),
        step4: t('pages.contact.step4', 'We ship after confirmation'),
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
})
