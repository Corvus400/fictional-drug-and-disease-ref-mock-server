#!/bin/bash
# Description: PostToolUse Hook - Write/Edit後に.ktファイルをktlintで自動フォーマット
# Usage: PostToolUse hook for Write|Edit tool in mock-server-base
# Reference: Harness Engineering Best Practices - 決定論的ツールとガードレール

set -euo pipefail

# PostToolUse hooks receive tool input via stdin
INPUT=$(cat)

# file_path を抽出（Write は file_path、Edit も file_path）
FILE_PATH=$(echo "$INPUT" | jq -r '.tool_input.file_path // empty')

# ファイルパスが空なら素通し
if [ -z "$FILE_PATH" ]; then
    exit 0
fi

# .kt ファイル以外は素通し
if [[ "$FILE_PATH" != *.kt ]]; then
    exit 0
fi

# build/ 配下は素通し
if [[ "$FILE_PATH" == */build/* ]]; then
    exit 0
fi

# ファイルが存在しない場合は素通し（削除されたファイル等）
if [ ! -f "$FILE_PATH" ]; then
    exit 0
fi

# プロジェクトルートを特定（.claude/scripts/ から2階層上）
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

# libs.versions.toml から ktlint バージョンを取得
VERSIONS_FILE="$PROJECT_ROOT/gradle/libs.versions.toml"
if [ ! -f "$VERSIONS_FILE" ]; then
    exit 0
fi

KTLINT_VERSION=$(grep '^ktlint' "$VERSIONS_FILE" | sed 's/.*=.*"\(.*\)".*/\1/')
if [ -z "$KTLINT_VERSION" ]; then
    exit 0
fi

# Gradle キャッシュから ktlint-cli JAR を検索
KTLINT_JAR=$(find ~/.gradle/caches/modules-2/files-2.1/com.pinterest.ktlint/ktlint-cli/"$KTLINT_VERSION" \
    -name "ktlint-cli-${KTLINT_VERSION}.jar" 2>/dev/null | head -1)

if [ -z "$KTLINT_JAR" ]; then
    # JAR が見つからない場合は Gradle フォールバック
    cd "$PROJECT_ROOT"
    ./gradlew spotlessApply --quiet 2>/dev/null || true
    exit 0
fi

# ktlint --format を実行（.editorconfig を自動読み込み）
cd "$PROJECT_ROOT"
KTLINT_OUTPUT=$(java -jar "$KTLINT_JAR" --format "$FILE_PATH" 2>&1) || {
    EXIT_CODE=$?
    if [ $EXIT_CODE -ne 0 ] && [ -n "$KTLINT_OUTPUT" ]; then
        # 自動修正不能な違反がある場合
        cat >&2 <<EOF
LINT: ktlint が自動修正できない違反を検出しました
  FILE: $FILE_PATH
  WHY: .editorconfig のルールに基づくコードスタイル違反です。
  FIX: コード側を修正してください。.editorconfig のルール変更は禁止されています。
  DETAILS:
$(echo "$KTLINT_OUTPUT" | sed 's/^/    /')
EOF
        exit 1
    fi
}

exit 0
