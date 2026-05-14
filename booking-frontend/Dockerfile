# Etapa 1: Build de la aplicación Angular
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build -- --configuration=production

# Etapa 2: Servidor Nginx para servir la app
FROM nginx:alpine

# Copiamos el build al directorio de Nginx
COPY --from=builder /app/dist/booking-frontend/browser /usr/share/nginx/html

# Configuración de Nginx para que Angular Router funcione correctamente
RUN printf 'server {\n\
    listen 4200;\n\
    root /usr/share/nginx/html;\n\
    index index.html;\n\
    location / {\n\
        try_files $uri $uri/ /index.html;\n\
    }\n\
}\n' > /etc/nginx/conf.d/default.conf

EXPOSE 4200
