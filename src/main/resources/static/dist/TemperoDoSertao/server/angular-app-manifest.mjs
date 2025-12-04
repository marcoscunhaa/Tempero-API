
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
    'index.csr.html': {size: 5363, hash: 'bf65b9024d170acffc488702535c5b1e08e7feb3a7de3dcadc1aff77885a85a4', text: () => import('./assets-chunks/index_csr_html.mjs').then(m => m.default)},
    'index.server.html': {size: 1335, hash: '40c6274faa462f4dde2e5bbaf5088a30166bd8147d6b59a0292a2fffa605f194', text: () => import('./assets-chunks/index_server_html.mjs').then(m => m.default)},
    'index.html': {size: 5410, hash: '3009a7d198217e69519e1d1d784fc7f0480989723beb5b535e04320f4505caba', text: () => import('./assets-chunks/index_html.mjs').then(m => m.default)},
    'styles-HR6D4WJL.css': {size: 315730, hash: 'q9j6xvPSaNQ', text: () => import('./assets-chunks/styles-HR6D4WJL_css.mjs').then(m => m.default)}
  },
};
