const CACHE_NAME = 'legal-recall-v2';
const ASSETS_TO_CACHE = [
  './',
  './index.html',
  './manifest.json',
  './app_logo_web.jpg',
  './jsondata.json',
  'https://cdn.tailwindcss.com',
  'https://cdn.tailwindcss.com/',
  'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css'
];

// Install Event
self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      console.log('[Service Worker] Caching app shell and static assets');
      return cache.addAll(ASSETS_TO_CACHE);
    }).then(() => self.skipWaiting())
  );
});

// Activate Event - Clean up old cache versions
self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames.map((cache) => {
          if (cache !== CACHE_NAME) {
            console.log('[Service Worker] Clearing old cache:', cache);
            return caches.delete(cache);
          }
        })
      );
    }).then(() => self.clients.claim())
  );
});

// Fetch Event - Resilient Cache-first with Runtime caching and Navigation fallback
self.addEventListener('fetch', (event) => {
  if (event.request.method !== 'GET') return;

  const requestUrl = new URL(event.request.url);

  event.respondWith(
    caches.match(event.request).then((cachedResponse) => {
      if (cachedResponse) {
        return cachedResponse;
      }

      // 1. Normalize trailing slash for matching (resolves Tailwind URL and other endpoint differences)
      const isTailwind = requestUrl.hostname === 'cdn.tailwindcss.com';
      const normalizedUrl = event.request.url.replace(/\/$/, "");
      
      return caches.match(normalizedUrl).then((normResponse) => {
        if (normResponse) {
          return normResponse;
        }

        // If not in cache, fetch from network and dynamically cache (resolves Google Fonts & general runtime, Gaps 2 & 3)
        return fetch(event.request).then((networkResponse) => {
          // Dynamic caching criteria: valid response status, correct origins, or cors/opaque resources (e.g. google fonts, icons)
          const isSuccessful = networkResponse && (networkResponse.status === 200 || networkResponse.status === 0);
          const isEligible = isSuccessful && (
            requestUrl.origin === self.location.origin || 
            requestUrl.hostname.includes('googleapis.com') || 
            requestUrl.hostname.includes('gstatic.com') ||
            requestUrl.hostname.includes('tailwindcss.com') ||
            requestUrl.hostname.includes('cloudflare.com')
          );

          if (isEligible) {
            const responseToCache = networkResponse.clone();
            caches.open(CACHE_NAME).then((cache) => {
              cache.put(event.request, responseToCache);
              console.log('[Service Worker] Dynamically cached:', event.request.url);
            });
          }

          return networkResponse;
        }).catch((error) => {
          // 4. Offline fallback for navigation requests
          if (event.request.mode === 'navigate') {
            console.log('[Service Worker] Navigation request failed offline, serving index.html fallback');
            return caches.match('./') || caches.match('./index.html');
          }
          throw error;
        });
      });
    })
  );
});
