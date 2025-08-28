#!/bin/bash
echo "🔄 Updating submodules to latest version..."

# 현재 브랜치 확인
CURRENT_BRANCH=$(git branch --show-current)
echo "Current branch: $CURRENT_BRANCH"

# 서브모듈을 최신 버전으로 업데이트
echo "Fetching latest submodule changes..."
git submodule update --remote --merge

# 서브모듈 상태 확인
echo "Submodule status:"
git submodule status

# 변경사항 확인
if [ -n "$(git status --porcelain)" ]; then
    echo ""
    echo "Submodule updates detected!"
    git status --short

    echo ""
    read -p "🤔 Do you want to commit these submodule updates? (y/n): " -n 1 -r
    echo

    if [[ $REPLY =~ ^[Yy]$ ]]; then
        git add .
        git commit -m "chore: update submodule to latest version

- Updated krocs-config submodule
- Branch: $CURRENT_BRANCH
- Updated at: $(date)"

        echo "✅ Submodule updates committed successfully!"
        echo "💡 Don't forget to push: git push origin $CURRENT_BRANCH"
    else
        echo "⏸️  Submodule updated but not committed"
        echo "💡 You can commit later with: git add . && git commit -m \"chore: update submodule\""
    fi
else
    echo "✅ Submodule already up to date!"
fi

echo ""
echo "🎉 Submodule update completed!"