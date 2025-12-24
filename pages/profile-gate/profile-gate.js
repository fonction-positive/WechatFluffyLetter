const { loginAndSyncProfile } = require('../../utils/auth')

const defaultAvatarUrl = 'https://mmbiz.qpic.cn/mmbiz/icTdbqWNOwNRna42FI242Lcia07jQodd2FJGIYQfG0LAJGFxM4FbnQP6yfMxBgJ0F3YRqJCJ1aPAK2dQagdusBZg/0'
const USERINFO_KEY = 'userInfo'

Page({
  data: {
    userInfo: {
      avatarUrl: defaultAvatarUrl,
      nickName: '',
    },
    hasUserInfo: false,
    canIUseGetUserProfile: wx.canIUse('getUserProfile'),
    canIUseNicknameComp: wx.canIUse('input.type.nickname'),
  },

  onLoad() {
    this.loadCachedUserInfo()
    if (this.data.hasUserInfo) {
      // 已有缓存则直接进主页
      this.goHome()
    }
  },

  loadCachedUserInfo() {
    try {
      const cached = wx.getStorageSync(USERINFO_KEY)
      if (cached && (cached.nickName || cached.nickname)) {
        const nickName = cached.nickName || cached.nickname || ''
        const has = !!nickName
        this.setData({
          userInfo: { avatarUrl: defaultAvatarUrl, nickName },
          hasUserInfo: has,
        })
      }
    } catch (e) {}
  },

  onChooseAvatar(e) {
    const avatarUrl = e && e.detail ? e.detail.avatarUrl : ''
    const nickName = this.data.userInfo.nickName || ''
    const has = !!(nickName && avatarUrl && avatarUrl !== defaultAvatarUrl)
    this.setData({
      userInfo: { avatarUrl: avatarUrl || defaultAvatarUrl, nickName },
      hasUserInfo: has,
    })

    if (has) this.onConfirm()
  },

  onInputChange(e) {
    const nickName = e && e.detail ? e.detail.value : ''
    const avatarUrl = (this.data.userInfo && this.data.userInfo.avatarUrl) || defaultAvatarUrl
    const has = !!(nickName && avatarUrl && avatarUrl !== defaultAvatarUrl)
    this.setData({
      userInfo: { avatarUrl, nickName },
      hasUserInfo: has,
    })

    if (has) this.onConfirm()
  },

  getUserProfile() {
    if (!wx.getUserProfile) {
      wx.showToast({ title: '请升级微信版本', icon: 'none' })
      return
    }
    wx.getUserProfile({
      desc: '用于完善用户资料（昵称/头像）',
      success: (res) => {
        const ui = res && res.userInfo ? res.userInfo : {}
        const nickName = ui.nickName || ui.nickname || ''
        const avatarUrl = ui.avatarUrl || ui.avatarURL || defaultAvatarUrl
        const has = !!(nickName && avatarUrl && avatarUrl !== defaultAvatarUrl)
        this.setData({
          userInfo: { avatarUrl, nickName },
          hasUserInfo: has,
        })
        if (has) this.onConfirm()
      },
    })
  },

  onConfirm() {
    if (this._confirming) return
    this._confirming = true

    const nickName = (this.data.userInfo && this.data.userInfo.nickName) || ''

    try {
      // 只缓存昵称；头像不持久化、不上传
      wx.setStorageSync(USERINFO_KEY, { nickName })
    } catch (e) {}

    // 只同步昵称；头像不写入 DB。
    const backendProfile = {
      nickname: nickName,
      avatarUrl: '',
    }

    wx.showLoading({ title: '进入中', mask: true })
    return loginAndSyncProfile(backendProfile)
      .then(() => this.goHome())
      .finally(() => {
        wx.hideLoading()
        this._confirming = false
      })
  },

  goHome() {
    wx.redirectTo({ url: '/pages/index/index' })
  },
})
