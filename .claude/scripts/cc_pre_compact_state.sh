#!/bin/bash
# Description: PreCompact Hook - コンテキスト圧縮前に作業中ファイルに応じたルールをリマインド
# Usage: PreCompact hook in fictional-drug-and-disease-ref-mock-server

set -euo pipefail

# 変更中のファイルを取得（staged + unstaged）
CHANGED_FILES=$(git diff --name-only 2>/dev/null || true)
STAGED_FILES=$(git diff --cached --name-only 2>/dev/null || true)
ALL_CHANGED="${CHANGED_FILES}${STAGED_FILES:+$'\n'${STAGED_FILES}}"

# 変更ファイルがなければ素通し
if [ -z "$ALL_CHANGED" ]; then
    exit 0
fi

RULES=""

# fixture/ 配下の変更を検出
if echo "$ALL_CHANGED" | grep -q 'fixture/'; then
    RULES="${RULES}  - .claude/rules/fixture-quality.md (Fixture editing)\n"
    RULES="${RULES}  - .claude/rules/product-id-registry.md (Product ID management)\n"
fi

# model/ 配下の変更を検出
if echo "$ALL_CHANGED" | grep -q 'model/'; then
    RULES="${RULES}  - .claude/rules/fixture-quality.md (Model editing)\n"
fi

# routes/ 配下の変更を検出
if echo "$ALL_CHANGED" | grep -q 'routes/'; then
    RULES="${RULES}  - .claude/rules/dynamic-state.md (Route/state management)\n"
    RULES="${RULES}  - .claude/rules/endpoint-implementation.md (Endpoint conventions)\n"
fi

# scenario/ 配下の変更を検出
if echo "$ALL_CHANGED" | grep -q 'scenario/'; then
    RULES="${RULES}  - .claude/rules/dynamic-state.md (Scenario management)\n"
fi

# test/ 配下の変更を検出
if echo "$ALL_CHANGED" | grep -q 'test/'; then
    RULES="${RULES}  - .claude/rules/test-conventions.md (Admin API test conventions)\n"
fi

# catalog/ 配下の変更を検出
if echo "$ALL_CHANGED" | grep -q 'catalog/'; then
    RULES="${RULES}  - .claude/rules/product-id-registry.md (Product ID management)\n"
fi

# ルールがあれば出力
if [ -n "$RULES" ]; then
    # 重複行を除去
    UNIQUE_RULES=$(printf "%b" "$RULES" | sort -u)
    cat <<EOF
CONTEXT_REMINDER: The following rule files apply to the current work:
${UNIQUE_RULES}
EOF
fi

exit 0
