#!/bin/bash
# ShortX PRC Release 发布脚本
# 用法: ./scripts/release_prc.sh [version]
# 示例: ./scripts/release_prc.sh 1.7
# 如果不传 version，则从最近一次 git tag 推导（自动 +0.1）

set -eo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
CHANGELOG="$PROJECT_ROOT/CHANGELOG.md"

# 颜色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

info()  { echo -e "${GREEN}[INFO]${NC} $*"; }
warn()  { echo -e "${YELLOW}[WARN]${NC} $*"; }
error() { echo -e "${RED}[ERROR]${NC} $*"; exit 1; }

# ========== 前置检查 ==========
command -v gh >/dev/null 2>&1 || error "未安装 GitHub CLI，请执行: brew install gh"
gh auth status >/dev/null 2>&1 || error "GitHub CLI 未登录，请执行: gh auth login"
[ -f "$CHANGELOG" ] || error "CHANGELOG.md 不存在: $CHANGELOG"

cd "$PROJECT_ROOT"

# 确认工作区干净（tag 前不应有未提交的变更影响版本号）
if ! git diff --quiet HEAD 2>/dev/null; then
    warn "工作区有未提交的变更，androidGitVersion 会带 -dirty 后缀"
    read -rp "是否继续？(y/N) " confirm
    [[ "$confirm" =~ ^[Yy]$ ]] || exit 0
fi

# ========== 确定版本号 ==========
if [ -n "$1" ]; then
    VERSION="$1"
else
    # 从已有 tag 推导下一个版本（取最新 tag，去掉 v 前缀，minor +1）
    LATEST_TAG=$(git describe --tags --abbrev=0 2>/dev/null || echo "")
    if [ -z "$LATEST_TAG" ]; then
        error "未找到任何 git tag，请手动指定版本号: ./scripts/release_prc.sh <version>"
    fi
    LATEST_VER="${LATEST_TAG#v}"
    MAJOR="${LATEST_VER%%.*}"
    MINOR="${LATEST_VER#*.}"
    NEXT_MINOR=$((MINOR + 1))
    VERSION="${MAJOR}.${NEXT_MINOR}"
fi

TAG="v${VERSION}"
info "目标版本: $VERSION (tag: $TAG)"

# 检查 tag 是否已存在
if git rev-parse "$TAG" >/dev/null 2>&1; then
    error "Tag $TAG 已存在，请先删除或指定其他版本号"
fi

# ========== 创建并推送 tag ==========
info "创建 tag: $TAG"
git tag -a "$TAG" -m "Release $VERSION"

info "推送 tag 到远端"
git push origin "$TAG"

# ========== 构建 PRC Release APK ==========
info "开始构建 PRC Release APK..."
bash "$SCRIPT_DIR/build_prc.sh" || error "构建失败，请检查 gradle 日志"

# ========== 查找构建产物 ==========
APK_DIR="$PROJECT_ROOT/out/app/outputs/apk/prc/release"
[ -d "$APK_DIR" ] || error "APK 输出目录不存在: $APK_DIR"

# 找到最新的 APK（按修改时间排序，取第一个）
APK_FILE=$(find "$APK_DIR" -maxdepth 1 -name "*.apk" -type f -printf '%T@ %p\n' 2>/dev/null \
    | sort -rn | head -1 | cut -d' ' -f2-)

# macOS find 不支持 -printf，回退到 ls
if [ -z "$APK_FILE" ]; then
    APK_FILE=$(ls -t "$APK_DIR"/*.apk 2>/dev/null | head -1)
fi

[ -n "$APK_FILE" ] || error "未找到 APK 文件: $APK_DIR"
[ -f "$APK_FILE" ] || error "APK 文件无效: $APK_FILE"

APK_NAME=$(basename "$APK_FILE")
APK_SIZE=$(du -h "$APK_FILE" | cut -f1)
info "构建产物: $APK_NAME ($APK_SIZE)"

# ========== 提取最新 changelog 段落 ==========
# 读取 CHANGELOG.md，从第一个 ## 日期标题到下一个 ## 之间的内容作为本次更新说明
extract_changelog() {
    # macOS awk 不支持 {4} 量词，改用展开写法
    awk '
        /^## [0-9][0-9][0-9][0-9]-/ {
            if (found) exit
            found = 1
            print
            next
        }
        found { print }
    ' "$CHANGELOG"
}

RELEASE_NOTES=$(extract_changelog)

if [ -z "$RELEASE_NOTES" ]; then
    warn "CHANGELOG.md 中未找到有效的更新日志段落，将使用默认文案"
    RELEASE_NOTES="Release $VERSION"
fi

# 写入临时文件，供 gh 读取（避免 shell 转义问题）
NOTES_FILE=$(mktemp)
echo "$RELEASE_NOTES" > "$NOTES_FILE"
trap 'rm -f "$NOTES_FILE"' EXIT

info "更新日志摘要:"
echo "$RELEASE_NOTES" | head -20
echo "..."

# ========== 创建 GitHub Release ==========
info "创建 GitHub Release: $TAG"
gh release create "$TAG" \
    "$APK_FILE" \
    --title "ShortX $VERSION" \
    --notes-file "$NOTES_FILE"

info "发布完成!"
info "  Tag:     $TAG"
info "  APK:     $APK_NAME"
info "  Release: https://github.com/Tornaco/ShortX/releases/tag/$TAG"
