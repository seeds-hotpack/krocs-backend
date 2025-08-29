#!/bin/bash
echo "Setting up submodules for development..." 

# 서브모듈 초기화 및 업데이트
echo "Initializing submodules..."
git submodule update --init --recursive

# 현재 서브모듈 URL 확인
CURRENT_URL=$(git config --get submodule."backend/src/main/resources/krocs-config".url)

# 토큰이 설정되어 있지 않으면 입력 요청
if ! echo "$CURRENT_URL" | grep -q "@"; then
    echo ""
    echo "🔑 GitHub token for private submodule required."
    echo "This token needs read access to 'seeds-hotpack/krocs-submodule'"
    echo "Please enter your GitHub token (input will be hidden):"
    read -s GITHUB_TOKEN

    if [ -z "$GITHUB_TOKEN" ]; then
        echo "❌ Token cannot be empty!"
        exit 1
    fi

    echo "🔗 Configuring submodule with token..."
    git config submodule."backend/src/main/resources/krocs-config".url "https://${GITHUB_TOKEN}@github.com/seeds-hotpack/krocs-submodule.git"

    echo "🔄 Updating submodule with new configuration..."
    git submodule update --remote --merge

    echo "✅ Submodule configured successfully!"
else
    echo "✅ Submodule already configured with token"
    echo "🔄 Updating to latest version..."
    git submodule update --remote --merge
fi

echo ""
echo "Submodule setup completed!"
echo "Submodule location: backend/src/main/resources/krocs-config"
echo "To update in the future, run: ./scripts/update-submodules.sh"