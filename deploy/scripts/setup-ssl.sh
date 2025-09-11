#!/bin/bash

DOMAIN="api.krocs.life"
EMAIL="63wlsthgml@gmail.com"

echo "🔒 SSL 인증서 설정을 시작합니다..."
echo "도메인: $DOMAIN"
echo "이메일: $EMAIL"

# 필요한 디렉토리 생성
mkdir -p nginx/conf.d nginx certbot/www certbot/conf

# 임시 nginx 설정으로 시작 (SSL 없이)
cat > nginx/conf.d/default.conf << 'EOF'
server {
    listen 80;
    server_name api.krocs.life;

    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    location / {
        proxy_pass http://krocs-backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
EOF

# Docker Compose로 서비스 시작 (SSL 없이)
echo "📦 임시로 서비스를 시작합니다..."
docker-compose -f docker-compose.prod.yml up -d nginx krocs-backend redis

# SSL 인증서 발급
echo "📜 SSL 인증서를 발급받습니다..."
docker run --rm \
  -v /home/ubuntu/krocs-deploy/certbot/www:/var/www/certbot \
  -v /home/ubuntu/krocs-deploy/certbot/conf:/etc/letsencrypt \
  certbot/certbot:latest \
  certonly --webroot --webroot-path=/var/www/certbot \
  --email $EMAIL --agree-tos --no-eff-email \
  -d $DOMAIN

# SSL 설정이 포함된 nginx 설정으로 교체
echo "🔄 SSL 설정을 적용합니다..."
cat > nginx/conf.d/default.conf << 'EOF'
# HTTP to HTTPS 리다이렉트
server {
    listen 80;
    server_name api.krocs.life;

    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    location / {
        return 301 https://$server_name$request_uri;
    }
}

# HTTPS 서버 설정
server {
    listen 443 ssl http2;
    server_name api.krocs.life;

    ssl_certificate /etc/nginx/ssl/live/api.krocs.life/fullchain.pem;
    ssl_certificate_key /etc/nginx/ssl/live/api.krocs.life/privkey.pem;

    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;

    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

    client_max_body_size 50M;

    location / {
        proxy_pass http://krocs-backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Host $server_name;

        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;

        proxy_buffering on;
        proxy_buffer_size 4k;
        proxy_buffers 8 4k;
    }

    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
        proxy_pass http://krocs-backend:8080;
    }

    location /health {
        access_log off;
        proxy_pass http://krocs-backend:8080/actuator/health;
    }
}
EOF

# nginx 재시작
docker-compose exec nginx nginx -s reload

echo "✅ SSL 인증서 설정이 완료되었습니다!"
echo "🌐 https://$DOMAIN 에서 확인해보세요."