const http = require('http');
const fs = require('fs');
const path = require('path');

const rootDir = '/app/out';
const port = 80;
const runtimeApiBase = (process.env.API_BASE_URL || '/api').trim();
const gatewayTarget = (() => {
  if (/^https?:\/\//i.test(runtimeApiBase)) {
    try {
      return new URL(runtimeApiBase);
    } catch {
      return new URL('http://localhost:8080');
    }
  }

  return new URL('http://localhost:8080');
})();
const browserApiBase = /^https?:\/\//i.test(runtimeApiBase) ? '/api' : runtimeApiBase;
const mimeTypes = {
  '.css': 'text/css; charset=utf-8',
  '.html': 'text/html; charset=utf-8',
  '.ico': 'image/x-icon',
  '.jpeg': 'image/jpeg',
  '.jpg': 'image/jpeg',
  '.js': 'application/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.map': 'application/json; charset=utf-8',
  '.png': 'image/png',
  '.svg': 'image/svg+xml',
  '.txt': 'text/plain; charset=utf-8'
};

function sendFile(res, filePath) {
  const ext = path.extname(filePath).toLowerCase();
  res.setHeader('Content-Type', mimeTypes[ext] || 'application/octet-stream');
  res.setHeader('Cache-Control', 'no-cache');
  fs.createReadStream(filePath).pipe(res);
}

function resolveFilePath(requestPath, callback) {
  const safePath = path.normalize(requestPath).replace(/^(\.\.[/\\])+/, '');
  const candidatePaths = [
    path.join(rootDir, safePath),
    path.join(rootDir, 'index.html'),
    path.join(rootDir, 'index.csr.html')
  ];

  const tryNext = (index) => {
    if (index >= candidatePaths.length) {
      callback(null, null);
      return;
    }

    const candidatePath = candidatePaths[index];
    if (!candidatePath.startsWith(rootDir)) {
      callback(null, null);
      return;
    }

    fs.stat(candidatePath, (err, stats) => {
      if (!err && stats.isFile()) {
        callback(null, candidatePath);
        return;
      }
      tryNext(index + 1);
    });
  };

  tryNext(0);
}

function proxyRequest(req, res) {
  const targetUrl = new URL(req.url, gatewayTarget);
  const proxyReq = http.request(
    {
      hostname: targetUrl.hostname,
      port: targetUrl.port || (targetUrl.protocol === 'https:' ? 443 : 80),
      path: `${targetUrl.pathname}${targetUrl.search}`,
      method: req.method,
      headers: {
        ...req.headers,
        host: targetUrl.host
      }
    },
    (proxyRes) => {
      res.writeHead(proxyRes.statusCode || 502, proxyRes.headers);
      proxyRes.pipe(res);
    }
  );

  proxyReq.on('error', () => {
    res.writeHead(502, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ error: 'Unable to reach gateway' }));
  });

  req.pipe(proxyReq);
}

const server = http.createServer((req, res) => {
  const requestPath = decodeURIComponent(new URL(req.url, 'http://localhost').pathname);

  if (requestPath === '/app-config.json') {
    res.writeHead(200, { 'Content-Type': 'application/json', 'Cache-Control': 'no-store' });
    res.end(JSON.stringify({ apiBase: browserApiBase, environment: 'production' }));
    return;
  }

  if (requestPath.startsWith('/api')) {
    proxyRequest(req, res);
    return;
  }

  resolveFilePath(requestPath === '/' ? '/' : requestPath, (err, filePath) => {
    if (err || !filePath) {
      res.statusCode = 404;
      res.end('Not Found');
      return;
    }

    sendFile(res, filePath);
  });
});

server.listen(port, () => {
  console.log(`Static server listening on port ${port}`);
  console.log(`Runtime API base: ${runtimeApiBase}`);
  console.log(`Browser API base: ${browserApiBase}`);
  console.log(`Proxying /api requests to ${gatewayTarget.toString()}`);
});
