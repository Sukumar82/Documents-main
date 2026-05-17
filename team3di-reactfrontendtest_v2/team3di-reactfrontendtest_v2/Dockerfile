# ─────────────────────────────────────────────────────────
# Stage 1: Build – Node 18 (matches README prerequisite)
# ─────────────────────────────────────────────────────────
FROM node:18-alpine AS build

WORKDIR /app

# Install dependencies first (cached layer)
COPY package*.json ./
# Use `npm install` to avoid lockfile/version mismatches inside CI containers
# and allow installing with peer dependency resolutions when building inside Docker.
RUN npm install --no-audit --no-fund --legacy-peer-deps

# Copy source and build production bundle
COPY . .
RUN npm run build

# ─────────────────────────────────────────────────────────
# Stage 2: Serve – nginx (proxies /api/* to backend)
# ─────────────────────────────────────────────────────────
FROM nginx:alpine

# Copy the React production build
COPY --from=build /app/build /usr/share/nginx/html

# Replace the default nginx config with our custom proxy config
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
