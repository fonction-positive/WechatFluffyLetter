const { CONFIG } = require('./config')
const { request } = require('./request')

function getCategories() {
  return request({
    path: CONFIG.API.CATEGORIES,
    method: 'GET',
    data: {},
  })
}

function getProducts({ categoryId, page = 1, size = 10 } = {}) {
  const data = {
    page,
    size,
  }
  if (categoryId) data.category_id = categoryId

  return request({
    path: CONFIG.API.PRODUCTS,
    method: 'GET',
    needAuth: true,
    data,
  })
}

function getProductDetail(id) {
  return request({
    path: `${CONFIG.API.PRODUCTS}/${id}`,
    method: 'GET',
    needAuth: true,
    data: {},
  })
}

function getFavorites({ page = 1, size = 20 } = {}) {
  return request({
    path: CONFIG.API.FAVORITES,
    method: 'GET',
    needAuth: true,
    data: { page, size },
  })
}

function addFavorite(productId) {
  return request({
    path: `${CONFIG.API.FAVORITES}/${productId}`,
    method: 'POST',
    needAuth: true,
    data: {},
  })
}

function removeFavorite(productId) {
  return request({
    path: `${CONFIG.API.FAVORITES}/${productId}`,
    method: 'DELETE',
    needAuth: true,
    data: {},
  })
}

function getContact() {
  return request({
    path: CONFIG.API.CONTACT,
    method: 'GET',
    data: {},
  })
}

module.exports = {
  getCategories,
  getProducts,
  getProductDetail,
  getFavorites,
  addFavorite,
  removeFavorite,
  getContact,
}
