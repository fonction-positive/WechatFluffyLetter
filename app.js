// app.js
const { CONFIG, setBaseUrlOverride } = require('./utils/config')
const { ensureLogin, setUserToken } = require('./utils/auth')
const { getLang, setLang } = require('./utils/i18n')

App({
  onLaunch() {
    // 清理历史遗留的本地 baseUrl 覆盖值，避免开发者工具继续请求 localhost 导致 ERR_CONNECTION_REFUSED。
    // 如需临时切回本地联调，可在控制台手动设置：
    // https://fluffyletter.miette.top/api/favorites?page=1&size=20&lang=zhwx.setStorageSync('baseUrlOverride', 'http://localhost:7070')
    const override = wx.getStorageSync(CONFIG.STORAGE_KEYS.BASE_URL_OVERRIDE) || ''
    if (override === 'http://localhost:7070' || override === 'http://localhost:8080'
      || override === 'http://127.0.0.1:7070' || override === 'http://127.0.0.1:8080') {
      setBaseUrlOverride('')
    }

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
