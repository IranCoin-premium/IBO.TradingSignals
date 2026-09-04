# ==============================================================================
# IBO TRADING SIGNALS - MULTI-STAGE SECURE DOCKERFILE
# ==============================================================================

# Stage 1: Dependency Installation and TypeScript compilation
FROM node:20-alpine AS builder

WORKDIR /usr/src/app

COPY package*.json tsconfig.json ./

RUN npm ci

COPY src ./src

RUN npm run build && npm prune --production

# Stage 2: Runtime Environment
FROM node:20-alpine AS runner

WORKDIR /usr/src/app

ENV NODE_ENV=production
ENV APP_PORT=3000

COPY package*.json ./
COPY --from=builder /usr/src/app/node_modules ./node_modules
COPY --from=builder /usr/src/app/dist ./dist
COPY --from=builder /usr/src/app/src/database/migrations ./dist/database/migrations

EXPOSE 3000

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD node -e "fetch('http://localhost:' + (process.env.APP_PORT || 3000) + '/api/v1/health').then(r => r.ok ? process.exit(0) : process.exit(1)).catch(() => process.exit(1))"

CMD ["node", "dist/index.js"]
