
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
    'index.csr.html': {size: 5363, hash: '070c3fe3c76c4ce27ea41841a85dbded257764b60bf5ba92a5199b21588fecdb', text: () => import('./assets-chunks/index_csr_html.mjs').then(m => m.default)},
    'index.server.html': {size: 1335, hash: '4468ad3c3c612585352a1183799869e4d013564331210bf23e1b9c072363fdc9', text: () => import('./assets-chunks/index_server_html.mjs').then(m => m.default)},
    'index.html': {size: 5410, hash: '9577b9b6b315b51d5cdea4de20c2007fed546a8d26f70c5ac651a91d0f400126', text: () => import('./assets-chunks/index_html.mjs').then(m => m.default)},
    'styles-HR6D4WJL.css': {size: 315730, hash: 'q9j6xvPSaNQ', text: () => import('./assets-chunks/styles-HR6D4WJL_css.mjs').then(m => m.default)}
  },
};
