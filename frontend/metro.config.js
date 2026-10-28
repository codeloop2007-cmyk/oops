const { getDefaultConfig } = require("expo/metro-config");
const http = require("http");

const config = getDefaultConfig(__dirname);

const backendUrl = process.env.EXPO_PUBLIC_BASE_URL || "http://localhost:8080";
let parsedUrl;
try {
  parsedUrl = new URL(backendUrl);
} catch {
  parsedUrl = new URL("http://localhost:8080");
}

const originalEnhanceMiddleware = config.server.enhanceMiddleware;

config.server.enhanceMiddleware = (metroMiddleware, server) => {
  const enhanced = originalEnhanceMiddleware
    ? originalEnhanceMiddleware(metroMiddleware, server)
    : metroMiddleware;

  return (req, res, next) => {
    const url = req.url || "";
    if (
      url.startsWith("/api/") ||
      url.startsWith("/api?") ||
      url === "/api" ||
      url.startsWith("/v3/api-docs")
    ) {
      // Handle OPTIONS preflight directly with CORS headers
      if (req.method === "OPTIONS") {
        res.writeHead(204, {
          "access-control-allow-origin": "*",
          "access-control-allow-methods": "GET, POST, PUT, DELETE, OPTIONS, PATCH",
          "access-control-allow-headers": "*",
        });
        res.end();
        return;
      }

      const options = {
        hostname: parsedUrl.hostname,
        port: parsedUrl.port || (parsedUrl.protocol === "https:" ? 443 : 80),
        path: url,
        method: req.method,
        headers: {
          ...req.headers,
          host: `${parsedUrl.hostname}:${parsedUrl.port || 80}`,
        },
      };

      const proxyReq = http.request(options, (proxyRes) => {
        // Ensure CORS headers are present so web browsers accept responses
        const headers = { ...proxyRes.headers };
        headers["access-control-allow-origin"] = "*";
        headers["access-control-allow-headers"] = "*";
        headers["access-control-allow-methods"] = "GET, POST, PUT, DELETE, OPTIONS, PATCH";
        res.writeHead(proxyRes.statusCode, headers);
        proxyRes.pipe(res, { end: true });
      });

      proxyReq.on("error", (err) => {
        console.warn("[Metro Proxy] Backend unreachable on", backendUrl, "-", err.message);
        res.writeHead(502, {
          "Content-Type": "application/json",
          "access-control-allow-origin": "*",
        });
        res.end(
          JSON.stringify({
            error: "BackendUnreachable",
            message: `Could not reach backend at ${backendUrl}. Please ensure the Spring Boot server is running on port 8080.`,
          })
        );
      });

      req.pipe(proxyReq, { end: true });
      return;
    }

    return enhanced(req, res, next);
  };
};

module.exports = config;

