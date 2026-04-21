#!/bin/bash
# Description: Mock Serverコンテナをビルドして起動する
# Usage: ./scripts/start.sh

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
CONTAINER_NAME="mock-server-base"
IMAGE_NAME="mock-server-base:latest"

cd "$PROJECT_DIR"

echo "=== Mock Server 起動スクリプト ==="
echo ""

# SDKMAN_DIR を設定（resolve_java21 が参照する）
if [ -z "${SDKMAN_DIR:-}" ]; then
    export SDKMAN_DIR="$HOME/.sdkman"
fi

# JDK 21 解決: SDKMAN current → SDKMAN 21系 → /usr/libexec/java_home の順
resolve_java21() {
    local sdk_root="${SDKMAN_DIR:-$HOME/.sdkman}"
    local candidates_dir="$sdk_root/candidates/java"

    if [ -L "$candidates_dir/current" ]; then
        local current_target
        current_target=$(basename "$(readlink "$candidates_dir/current")")
        if [[ "$current_target" =~ ^21([.\-]|$) ]]; then
            export JAVA_HOME="$candidates_dir/$current_target"
            export PATH="$JAVA_HOME/bin:$PATH"
            echo "JDK 21 (SDKMAN current): $JAVA_HOME"
            return 0
        fi
    fi

    if [ -d "$candidates_dir" ]; then
        local sdk_jdk21=""
        local -a jdk21_dirs=()
        for dir in "$candidates_dir"/21*/; do
            [ -d "$dir" ] && jdk21_dirs+=("$(basename "$dir")")
        done
        if [ "${#jdk21_dirs[@]}" -gt 0 ]; then
            sdk_jdk21=$(printf '%s\n' "${jdk21_dirs[@]}" | sort -V | tail -1)
        fi
        if [ -n "$sdk_jdk21" ]; then
            export JAVA_HOME="$candidates_dir/$sdk_jdk21"
            export PATH="$JAVA_HOME/bin:$PATH"
            echo "JDK 21 (SDKMAN): $JAVA_HOME"
            return 0
        fi
    fi

    if /usr/libexec/java_home -v 21 &> /dev/null; then
        JAVA_HOME=$(/usr/libexec/java_home -v 21)
        export JAVA_HOME
        export PATH="$JAVA_HOME/bin:$PATH"
        echo "JDK 21 (system): $JAVA_HOME"
        return 0
    fi

    return 1
}

# JDK 21確認
if ! resolve_java21; then
    echo "ERROR: JDK 21がインストールされていません"
    echo ""
    echo "セットアップを実行してください:"
    echo "  ./scripts/setup.sh"
    exit 1
fi

# Apple Container確認
if ! command -v container &> /dev/null; then
    echo "ERROR: Apple Containerがインストールされていません"
    echo ""
    echo "セットアップを実行してください:"
    echo "  ./scripts/setup.sh"
    exit 1
fi

# システムサービス確認・自動起動
if ! container system status &> /dev/null; then
    echo "Apple Containerシステムサービスを起動しています..."
    container system start
    sleep 2
fi

# container-runtime-linux プロセスの重複チェックとクリーンアップ
check_and_cleanup_duplicate_processes() {
    local process_count
    process_count=$( (pgrep -f "container-runtime-linux" 2>/dev/null || true) | wc -l | tr -d ' ')

    if [ "$process_count" -gt 1 ]; then
        echo ""
        echo "WARNING: container-runtime-linux プロセスが ${process_count} 個検出されました"
        echo "ゾンビプロセスを自動クリーンアップしています..."
        echo ""

        # プロセス停止
        pkill -f "container-runtime-linux" 2>/dev/null || true
        sleep 2

        # 残存プロセスの強制終了
        if pgrep -f "container-runtime-linux" > /dev/null 2>&1; then
            echo "強制終了を実行しています..."
            pkill -9 -f "container-runtime-linux" 2>/dev/null || true
            sleep 1
        fi

        echo "クリーンアップ完了"
        echo ""
        echo "システムサービスを再起動しています..."
        container system start
        sleep 2
    fi
}

# 重複プロセスのチェックとクリーンアップを実行
check_and_cleanup_duplicate_processes

# 既存コンテナの停止・削除（実行中/停止済み問わず常にクリーンアップ）
container stop "$CONTAINER_NAME" 2>/dev/null || true
container delete "$CONTAINER_NAME" 2>/dev/null || true

# Fat JARビルド
echo ""
echo "Step 1/3: Fat JARをビルドしています..."
./gradlew buildFatJar --console=plain -q

# イメージビルド
echo ""
echo "Step 2/3: コンテナイメージをビルドしています..."
container build -t "$IMAGE_NAME" .

# コンテナ起動
echo ""
echo "Step 3/3: コンテナを起動しています..."
container run -d -p 8080:8080 --name "$CONTAINER_NAME" "$IMAGE_NAME"

# 起動待機
echo ""
echo "サーバーの起動を待機しています..."
sleep 3

# コンテナ情報取得
CONTAINER_IP=$(container list | grep "$CONTAINER_NAME" | awk '{print $6}' | cut -d'/' -f1)

echo ""
echo "=== 起動完了 ==="
echo ""
echo "コンテナ名: $CONTAINER_NAME"
echo "コンテナIP: $CONTAINER_IP"
echo ""
echo "アクセスURL:"
echo "  - http://localhost:8080 (ポートマッピング)"
echo "  - http://$CONTAINER_IP:8080 (コンテナIP直接)"
echo ""
echo "ヘルスチェック:"
echo "  curl -s http://$CONTAINER_IP:8080/health"
echo ""
echo "停止するには: ./scripts/stop.sh"

# Androidエミュレーター接続案内
echo ""
echo "--- Androidエミュレーター接続 ---"
echo "方法1: http://10.0.2.2:8080 (エミュレーターからホストへのアクセス)"
echo "方法2: adb reverse tcp:8080 tcp:8080 → http://localhost:8080"

# ヘルスチェック実行
echo ""
echo "--- ヘルスチェック ---"
if curl -s --connect-timeout 5 "http://$CONTAINER_IP:8080/health"; then
    echo ""
    echo "サーバーは正常に動作しています"
else
    echo ""
    echo "WARNING: ヘルスチェックに失敗しました。ログを確認してください:"
    echo "  container logs $CONTAINER_NAME"
fi

# OpenAPIドキュメント確認
echo ""
echo "--- OpenAPI ドキュメント ---"
# Swagger UIはリダイレクト（302→/swagger/index.html）をフォローして確認
SWAGGER_STATUS=$(curl -sL -o /dev/null -w "%{http_code}" --connect-timeout 5 "http://$CONTAINER_IP:8080/swagger/index.html")
if [ "$SWAGGER_STATUS" = "200" ]; then
    echo "Swagger UI: OK (http://localhost:8080/swagger)"
else
    echo "WARNING: Swagger UI アクセス失敗 (status: $SWAGGER_STATUS)"
fi

# ReDocはリダイレクト（302→/redoc/index.html）をフォローして確認
REDOC_STATUS=$(curl -sL -o /dev/null -w "%{http_code}" --connect-timeout 5 "http://$CONTAINER_IP:8080/redoc/index.html")
if [ "$REDOC_STATUS" = "200" ]; then
    echo "ReDoc: OK (http://localhost:8080/redoc)"
else
    echo "WARNING: ReDoc アクセス失敗 (status: $REDOC_STATUS)"
fi

OPENAPI_STATUS=$(curl -s -o /dev/null -w "%{http_code}" --connect-timeout 5 "http://$CONTAINER_IP:8080/openapi.json")
if [ "$OPENAPI_STATUS" = "200" ]; then
    echo "OpenAPI JSON: OK (http://localhost:8080/openapi.json)"
else
    echo "WARNING: OpenAPI JSON アクセス失敗 (status: $OPENAPI_STATUS)"
fi
