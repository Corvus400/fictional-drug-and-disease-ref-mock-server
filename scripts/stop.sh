#!/bin/bash
# Description: Mock Serverコンテナを停止・削除する
# Usage: ./scripts/stop.sh

set -euo pipefail

CONTAINER_NAME="fictional-drug-and-disease-ref-mock-server"

echo "=== Mock Server 停止スクリプト ==="
echo ""

# Apple Container確認
if ! command -v container &> /dev/null; then
    echo "ERROR: Apple Containerがインストールされていません"
    exit 1
fi

# コンテナ停止・削除（実行中/停止済み問わず常にクリーンアップ）
echo "コンテナを停止・削除しています..."
container stop "$CONTAINER_NAME" 2>/dev/null || true
container delete "$CONTAINER_NAME" 2>/dev/null || true

# コンテナ停止後にゾンビプロセスをチェック・クリーンアップ
cleanup_zombie_processes() {
    local process_count
    process_count=$( (pgrep -f "container-runtime-linux" 2>/dev/null || true) | wc -l | tr -d ' ')

    if [ "$process_count" -gt 1 ]; then
        echo ""
        echo "WARNING: container-runtime-linux のゾンビプロセスが検出されました (${process_count} 個)"
        echo "クリーンアップしています..."

        pkill -f "container-runtime-linux" 2>/dev/null || true
        sleep 2

        if pgrep -f "container-runtime-linux" > /dev/null 2>&1; then
            pkill -9 -f "container-runtime-linux" 2>/dev/null || true
        fi

        echo "ゾンビプロセスをクリーンアップしました"
    fi
}

# ゾンビプロセスのクリーンアップを実行
cleanup_zombie_processes

echo ""
echo "=== 停止完了 ==="
echo "コンテナ '$CONTAINER_NAME' を停止・削除しました"
