// utils/request.js
const { CONFIG, getBaseUrl } = require('./config')

function buildUrl(path) {
  if (!path) return getBaseUrl()
  if (/^https?:\/\//i.test(path)) return path
  const base = (getBaseUrl() || '').replace(/\/$/, '')
  const p = String(path).startsWith('/') ? path : `/${path}`
  return `${base}${p}`
}

function request(options) {
  const app = getApp ? getApp() : null

  const {
    url,
    path,
    method = 'GET',
    data,
    header = {},
    needAuth = false,
    showLoading = false,
    loadingText = '加载中',
    timeout = 15000,
  } = options || {}

  const finalUrl = buildUrl(url || path)

  const lang = (app && app.globalData && app.globalData.lang) || wx.getStorageSync(CONFIG.STORAGE_KEYS.LANG) || CONFIG.DEFAULT_LANG
  const userToken = (app && app.globalData && app.globalData.userToken) || wx.getStorageSync(CONFIG.STORAGE_KEYS.USER_TOKEN) || ''

  const finalHeader = {
    'Content-Type': 'application/json',
    ...header,
  }

  if (lang) {
    finalHeader['Accept-Language'] = lang
  }

  // 后端接口主要通过 query 参数接收 lang，这里对 GET 请求自动补齐
  if ((method || 'GET').toUpperCase() === 'GET' && data && typeof data === 'object' && !Array.isArray(data)) {
    if (!Object.prototype.hasOwnProperty.call(data, 'lang') && lang) {
      data.lang = lang
    }
  }

  if (needAuth && userToken) {
    finalHeader['Authorization'] = `Bearer ${userToken}`
  }

  if (showLoading) {
    wx.showLoading({ title: loadingText, mask: true })
  }

  return new Promise((resolve, reject) => {
    wx.request({
      url: finalUrl,
      method,
      data,
      header: finalHeader,
      timeout,
      success(res) {
        const status = res.statusCode
        if (status >= 200 && status < 300) {
          resolve(res.data)
          return
        }
        const serverMsg = res && res.data && (res.data.message || res.data.msg || res.data.error)
        const err = new Error(serverMsg ? String(serverMsg) : `Request failed: ${status}`)
        err.statusCode = status
        err.response = res
        err.data = res && res.data
        reject(err)
      },
      fail(err) {
        reject(err)
      },
      complete() {
        if (showLoading) wx.hideLoading()
      },
    })
  })
}

module.exports = {
  request,
}
