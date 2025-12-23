const { t, getLang } = require('../../utils/i18n')

Page({
  data: {
    lang: getLang(),
    labels: {
      title: t('pages.about.title', 'About'),
      desc: t('pages.about.desc', 'FluffyLetter'),
    },
  },
  onLoad() {
    this.refreshLabels()
    wx.setNavigationBarTitle({ title: this.data.labels.title })
  },
  onShow() {
    this.refreshLabels()
    wx.setNavigationBarTitle({ title: this.data.labels.title })
  },
  refreshLabels() {
    this.setData({
      lang: getLang(),
      labels: {
        title: t('pages.about.title', 'About'),
        desc: t('pages.about.desc', 'FluffyLetter'),
      },
    })
  },
})
