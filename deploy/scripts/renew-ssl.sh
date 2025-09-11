#!/bin/bash

echo "🔄 SSL 인증서 갱신을 시작합니다..."

# 인증서 갱신
docker compose run --rm certbot renew

# nginx 리로드
docker compose exec nginx nginx -s reload

echo "✅ SSL 인증서 갱신이 완료되었습니다!"