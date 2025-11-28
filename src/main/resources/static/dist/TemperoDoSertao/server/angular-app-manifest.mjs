
export default {
  bootstrap: () => import('./main.server.mjs').then(m => m.default),
  inlineCriticalCss: true,
  baseHref: '/',
  locale: undefined,
  routes: [
  {
    "renderMode": 2,
    "route": "/"
  }
],
  entryPointToBrowserMapping: undefined,
  assets: {
    'index.csr.html': {size: 5363, hash: '799f21c2f7e12918bca442730906129a6f47f58319ca5f4e3588caf7e6e19086', text: () => import('./assets-chunks/index_csr_html.mjs').then(m => m.default)},
    'index.server.html': {size: 1335, hash: '9c4d042e77341482ac46ae991446941b74fdd5b25a82bf272e5d1406df90b97c', text: () => import('./assets-chunks/index_server_html.mjs').then(m => m.default)},
    'index.html': {size: 5410, hash: '3a9ec15e583300ac9c9ea1b4513b6ee4d8469f2f873470931bd61b8970f17ba2', text: () => import('./assets-chunks/index_html.mjs').then(m => m.default)},
    'styles-HR6D4WJL.css': {size: 315730, hash: 'q9j6xvPSaNQ', text: () => import('./assets-chunks/styles-HR6D4WJL_css.mjs').then(m => m.default)}
  },
};
