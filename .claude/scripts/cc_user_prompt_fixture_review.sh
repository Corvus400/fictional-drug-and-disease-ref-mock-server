#!/bin/bash
# Description: UserPromptSubmit Hook - Fixture/Route未コミット変更を検出しLLMにレビューリマインダーを注入
# Usage: UserPromptSubmit hook in fictional-drug-and-disease-ref-mock-server

set -euo pipefail

# 未コミットの変更ファイルを取得
CHANGED_FILES=$(git diff --name-only 2>/dev/null || true)
STAGED_FILES=$(git diff --cached --name-only 2>/dev/null || true)
ALL_CHANGED="${CHANGED_FILES}${STAGED_FILES:+$'\n'${STAGED_FILES}}"

# 変更ファイルがなければ素通し
if [ -z "$ALL_CHANGED" ]; then
    exit 0
fi

REMINDERS=""

# fixture/ or model/ 変更を検出
if echo "$ALL_CHANGED" | grep -qE 'fixture/|model/'; then
    REMINDERS="${REMINDERS}  1. Consider running the \`fixture-review\` skill before committing\n"
fi

# routes/ 変更を検出 → 対応テストファイルの存在確認
ROUTE_FILES=$(echo "$ALL_CHANGED" | grep 'routes/' | grep '\.kt$' || true)
if [ -n "$ROUTE_FILES" ]; then
    for ROUTE_FILE in $ROUTE_FILES; do
        # ルートファイル名からテストファイル名を推測
        BASENAME=$(basename "$ROUTE_FILE" .kt)
        TEST_FILE=$(find src/test/ -name "${BASENAME}Test.kt" 2>/dev/null | head -1)
        if [ -z "$TEST_FILE" ]; then
            REMINDERS="${REMINDERS}  3. No test file found for ${BASENAME}.kt — consider creating ${BASENAME}Test.kt\n"
        fi
    done
fi

# リマインダーがあれば出力
if [ -n "$REMINDERS" ]; then
    UNIQUE_REMINDERS=$(printf "%b" "$REMINDERS" | sort -u)
    cat <<EOF
REVIEW_REMINDER: Fixture/Route changes detected. Please verify:
${UNIQUE_REMINDERS}
EOF
fi

exit 0
