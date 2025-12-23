// app.js
const { CONFIG } = require('./utils/config')
const { ensureLogin, setUserToken } = require('./utils/auth')
const { getLang, setLang } = require('./utils/i18n')

App({
  onLaunch() {
    // 初始化语言
    const lang = getLang()
    this.globalData.lang = lang

    // 从本地恢复 token
    const token = wx.getStorageSync(CONFIG.STORAGE_KEYS.USER_TOKEN) || ''
    if (token) {
      setUserToken(token)
    }

    // 启动时尝试登录（获取 userToken，内部绑定 openid）
    // 注意：未配置 CONFIG.BASE_URL 时不会阻塞开发，但收藏/用户接口不可用
    this.globalData.loginPromise = ensureLogin().catch(() => '')
  },
  globalData: {
    userInfo: null,
    lang: CONFIG.DEFAULT_LANG,
    userToken: '',
    loginPromise: null,
  }
})
