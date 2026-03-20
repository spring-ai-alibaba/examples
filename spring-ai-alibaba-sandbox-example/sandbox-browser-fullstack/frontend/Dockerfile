# Multi-stage build for React + Vite frontend
# Stage 1: Build
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package files first for better layer caching
COPY package*.json ./

# Install dependencies
RUN npm ci --only=production=false

# Copy source code
COPY . .

# Build the application
RUN npm run build

# Stage 2: Production
FROM nginx:alpine

# Create non-root user for security
RUN addgroup -g 1000 sandbox && \
    adduser -u 1000 -G sandbox -s /bin/sh -D sandbox

# Copy built assets from builder stage
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Set proper permissions
RUN chown -R sandbox:sandbox /usr/share/nginx/html && \
    chown -R sandbox:sandbox /var/cache/nginx && \
    chown -R sandbox:sandbox /var/log/nginx && \
    chown -R sandbox:sandbox /etc/nginx/conf.d

# Switch to non-root user
USER sandbox

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80 || exit 1

# Run nginx
CMD ["nginx", "-g", "daemon off;"]
