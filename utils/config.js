// utils/config.js
// 后端服务基础配置

const CONFIG = {
  // 线上环境后端域名（默认：指向你已部署的服务）
  BASE_URL: 'https://fluffyletter.miette.top',

  // 本地/开发环境可用（微信开发者工具里可访问本机服务；真机一般不行）
  // 如果你在 DevTools 里跑后端：确保 DevTools 勾选“不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书”。
  BASE_URLS: {
    develop: 'https://fluffyletter.miette.top',
    trial: 'https://fluffyletter.miette.top',
    release: 'https://fluffyletter.miette.top',
  },

  STORAGE_KEYS: {
    LANG: 'lang',
    USER_TOKEN: 'userToken',
    BASE_URL_OVERRIDE: 'baseUrlOverride',
  },

  DEFAULT_LANG: 'zh',

  API: {
    WECHAT_LOGIN: '/api/login/wechat',
    CATEGORIES: '/api/categories',
    PRODUCTS: '/api/products',
    FAVORITES: '/api/favorites',
    CONTACT: '/api/contact',
  },
}

function getEnvVersion() {
  try {
    const info = wx.getAccountInfoSync && wx.getAccountInfoSync()
    return (info && info.miniProgram && info.miniProgram.envVersion) || ''
  } catch (e) {
    return ''
  }
}

function getBaseUrl() {
  const override = wx.getStorageSync(CONFIG.STORAGE_KEYS.BASE_URL_OVERRIDE) || ''
  if (override) return override

  const env = getEnvVersion()
  if (env && CONFIG.BASE_URLS && CONFIG.BASE_URLS[env]) return CONFIG.BASE_URLS[env]
  return CONFIG.BASE_URL || ''
}

function setBaseUrlOverride(url) {
  wx.setStorageSync(CONFIG.STORAGE_KEYS.BASE_URL_OVERRIDE, url || '')
}

module.exports = {
  CONFIG,
  getBaseUrl,
  setBaseUrlOverride,
}
