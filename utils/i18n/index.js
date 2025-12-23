// utils/i18n/index.js
const { CONFIG } = require('../config')
const zh = require('./zh')
const en = require('./en')

const dict = { zh, en }

function getLang() {
  const app = getApp()
  return (app && app.globalData && app.globalData.lang) || wx.getStorageSync(CONFIG.STORAGE_KEYS.LANG) || CONFIG.DEFAULT_LANG
}

function t(key, fallback) {
  const lang = getLang()
  const table = dict[lang] || dict[CONFIG.DEFAULT_LANG] || {}

  if (!key) return fallback || ''

  const parts = String(key).split('.')
  let cur = table
  for (const p of parts) {
    if (!cur || typeof cur !== 'object') return fallback || ''
    cur = cur[p]
  }

  if (typeof cur === 'string') return cur
  return fallback || ''
}

function setLang(lang) {
  const next = lang === 'en' ? 'en' : 'zh'
  const app = getApp()
  app.globalData.lang = next
  wx.setStorageSync(CONFIG.STORAGE_KEYS.LANG, next)
  return next
}

module.exports = {
  t,
  getLang,
  setLang,
}
