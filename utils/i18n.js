// utils/i18n.js
// WeChat Mini Program require() does not reliably resolve directory index files.
// Provide a stable entry so callers can `require('../../utils/i18n')`.
module.exports = require('./i18n/index')
