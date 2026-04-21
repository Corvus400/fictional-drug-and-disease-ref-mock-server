#!/bin/bash
# Description: Mock Server起動確認 - curl/テストコマンド実行前にヘルスチェック
# Usage: PreToolUse hook for Bash tool in mock-server-base

set -euo pipefail

COMMAND=$(echo "$TOOL_INPUT" | jq -r '.command // empty')

# コマンドが空なら素通し
if [ -z "$COMMAND" ]; then
    exit 0
fi

# curl または テストスクリプトを含むかチェック
IS_CURL=false
IS_TEST_SCRIPT=false

if echo "$COMMAND" | grep -q 'curl'; then
    IS_CURL=true
fi

if echo "$COMMAND" | grep -qE 'scripts/.*test|\.\/test'; then
    IS_TEST_SCRIPT=true
fi

# 対象外コマンドは素通し
if [ "$IS_CURL" = false ] && [ "$IS_TEST_SCRIPT" = false ]; then
    exit 0
fi

# ヘルスチェック自体や start/stop スクリプトは素通し
if echo "$COMMAND" | grep -qE 'localhost:8080/health|scripts/start\.sh|scripts/stop\.sh|scripts/setup\.sh'; then
    exit 0
fi

# gradlew コマンド（ビルド・テスト）は素通し
if echo "$COMMAND" | grep -q 'gradlew'; then
    exit 0
fi

# Mock Serverの到達性確認
if curl -s --max-time 2 http://localhost:8080/health > /dev/null 2>&1; then
    exit 0
fi

# 到達不能 → ブロック
cat >&2 <<'EOF'
BLOCKED: Mock Server に到達できません (localhost:8080)
  WHY: curl/テストスクリプトは起動中の Mock Server に対して実行する必要があります。
       Apple Container 内で Mock Server が稼働していないか、ポートフォワードが切れています。
  FIX: scripts/start.sh を実行して Mock Server を起動してください。
       既に起動済みの場合は scripts/stop.sh → scripts/start.sh で再起動してください。
EOF
exit 2
