const CACHE_NAME = 'ibo-signals-v1';
const ASSETS = [
  '/',
  '/IBO.TradingSignals/',
  '/IBO.TradingSignals/index.html',
  '/IBO.TradingSignals/style.css',
  '/IBO.TradingSignals/app.js',
  '/IBO.TradingSignals/manifest.json'
];

self.addEventListener('install', (e) => {
  e.waitUntil(
    caches.open(CACHE_NAME).then((cache) => cache.addAll(ASSETS))
  );
  self.skipWaiting();
});

self.addEventListener('activate', (e) => {
  e.waitUntil(
    caches.keys().then((keys) =>
      Promise.all(keys.filter((k) => k !== CACHE_NAME).map((k) => caches.delete(k)))
    )
  );
  self.clients.claim();
});

self.addEventListener('fetch', (e) => {
  e.respondWith(
    caches.match(e.request).then((cached) => cached || fetch(e.request))
  );
});
