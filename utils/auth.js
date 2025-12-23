// utils/auth.js
const { CONFIG, getBaseUrl } = require('./config')
const { request } = require('./request')

function setUserToken(token) {
  const app = typeof getApp === 'function' ? getApp() : null
  if (app && app.globalData) {
    app.globalData.userToken = token || ''
  }
  wx.setStorageSync(CONFIG.STORAGE_KEYS.USER_TOKEN, token || '')
}

function getUserToken() {
  const app = typeof getApp === 'function' ? getApp() : null
  const inMemory = app && app.globalData ? app.globalData.userToken : ''
  return inMemory || wx.getStorageSync(CONFIG.STORAGE_KEYS.USER_TOKEN) || ''
}

function loginWithWechatCode(code) {
  if (!getBaseUrl()) {
    // 没配置后端域名时，不阻塞开发（但收藏/用户相关接口会不可用）
    return Promise.resolve({
      userToken: '',
      openid: '',
      msg: 'baseUrl is empty',
    })
  }

  return request({
    path: CONFIG.API.WECHAT_LOGIN,
    method: 'POST',
    data: { code },
    showLoading: false,
  })
}

function ensureLogin() {
  const existing = getUserToken()
  if (existing) return Promise.resolve(existing)

  return new Promise((resolve, reject) => {
    wx.login({
      success(res) {
        if (!res.code) {
          reject(new Error('wx.login returned empty code'))
          return
        }

        loginWithWechatCode(res.code)
          .then((data) => {
            const token = data && (data.userToken || data.token)
            if (token) setUserToken(token)
            resolve(token || '')
          })
          .catch((err) => {
            // 开发期常见：appid/secret 不匹配或 code 无效，后端会返回 4xx + message
            const msg = (err && err.message) || 'login failed'
            wx.showToast({ title: msg, icon: 'none' })
            resolve('')
          })
      },
      fail(err) {
        wx.showToast({ title: 'wx.login failed', icon: 'none' })
        resolve('')
      },
    })
  })
}

module.exports = {
  ensureLogin,
  getUserToken,
  setUserToken,
}
