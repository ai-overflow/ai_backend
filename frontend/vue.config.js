const path = require('path');

module.exports = {
  publicPath: process.env.BASE_URL,
  transpileDependencies: [
    'vuetify'
  ],
  configureWebpack: {
    resolve: {
      alias: {
        '@shared': path.resolve(__dirname, 'src/shared'),
      },
    },
  }
}
