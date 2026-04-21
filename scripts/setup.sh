#!/bin/bash
# Description: Mock Server環境のセットアップを行う
# Usage: ./scripts/setup.sh
#
# このスクリプトは以下をセットアップします：
# 1. JDK 21の確認（SDKMAN経由でインストール）
# 2. Apple Containerのダウンロードとインストール案内
# 3. Apple Containerシステムサービスの起動案内

set -euo pipefail

CONTAINER_VERSION="0.8.0"
CONTAINER_PKG_URL="https://github.com/apple/container/releases/download/${CONTAINER_VERSION}/container-installer-signed.pkg"
CONTAINER_PKG_PATH="/tmp/container-installer-${CONTAINER_VERSION}.pkg"

echo "=== Mock Server 環境セットアップ ==="
echo ""

# macOSバージョン確認
echo "Step 1/6: macOSバージョンを確認しています..."
MACOS_VERSION=$(sw_vers -productVersion | cut -d. -f1)
echo "macOS ${MACOS_VERSION} が検出されました"
if [ "$MACOS_VERSION" -lt 26 ]; then
    echo "WARNING: Apple ContainerはmacOS 26（Tahoe）以上が必要です。"
    echo "macOSをアップグレードするか、ローカル起動（./gradlew run）を使用してください。"
    echo ""
fi

# Homebrew確認
echo ""
echo "Step 2/6: Homebrewを確認しています..."
if ! command -v brew &> /dev/null; then
    echo "WARNING: Homebrewがインストールされていません"
    echo "JDK 21が他の方法でインストールされていれば問題ありません。"
    echo ""
fi

# JDK 21確認・インストール
echo ""
echo "Step 3/6: JDK 21を確認しています..."

# SSOT: 必要な JDK バージョン（将来変更時はここだけ書き換える）
readonly REQUIRED_JDK_VERSION="21.0.6-tem"

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
            JDK21_PATH="$candidates_dir/$current_target"
            export JAVA_HOME="$JDK21_PATH"
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
            JDK21_PATH="$candidates_dir/$sdk_jdk21"
            export JAVA_HOME="$JDK21_PATH"
            return 0
        fi
    fi

    if /usr/libexec/java_home -v 21 &> /dev/null; then
        JDK21_PATH=$(/usr/libexec/java_home -v 21)
        export JAVA_HOME="$JDK21_PATH"
        return 0
    fi

    return 1
}

JDK21_PATH=""
if resolve_java21; then
    echo "OK: JDK 21 が見つかりました"
    echo "    パス: $JDK21_PATH"
else
    if [ ! -s "$SDKMAN_DIR/bin/sdkman-init.sh" ]; then
        echo "ERROR: SDKMAN がインストールされていません。"
        echo ""
        echo "SDKMAN をインストールしてから再度 ./scripts/setup.sh を実行してください。"
        echo "（インストール手順は https://sdkman.io/install を参照）"
        exit 1
    else
        # SDKMAN 導入済みだが 21 系がない → 自動インストール
        echo "SDKMAN は導入済みですが JDK 21 がインストールされていません。"
        echo "自動インストールを実行します: sdk install java $REQUIRED_JDK_VERSION"
        echo ""
        echo Y | sdk install java "$REQUIRED_JDK_VERSION"
        set +u
        # shellcheck source=/dev/null
        source "$SDKMAN_DIR/bin/sdkman-init.sh"
        set -u
        if resolve_java21; then
            echo "OK: JDK 21 が設定されました"
            echo "    パス: $JDK21_PATH"
        else
            echo "ERROR: JDK 21 のインストール後も検出できませんでした。"
            echo "手動で確認してください: sdk list java"
            exit 1
        fi
    fi
fi

# Apple Container確認・インストール
echo ""
echo "Step 4/6: Apple Containerを確認しています..."
if command -v container &> /dev/null; then
    INSTALLED_VERSION=$(container --version 2>/dev/null | grep -oE '[0-9]+\.[0-9]+\.[0-9]+' | head -1)
    echo "OK: Apple Container ${INSTALLED_VERSION} がインストールされています"
else
    echo "Apple Containerがインストールされていません。"
    echo ""

    # インストーラーダウンロード
    if [ ! -f "$CONTAINER_PKG_PATH" ]; then
        echo "インストーラーをダウンロードしています..."
        curl -L -o "$CONTAINER_PKG_PATH" "$CONTAINER_PKG_URL"
    else
        echo "インストーラーは既にダウンロード済みです: $CONTAINER_PKG_PATH"
    fi

    echo ""
    echo "=============================================="
    echo "Apple Containerをインストールする必要があります。"
    echo ""
    echo "GUIインストーラーを開きます..."
    echo "インストーラーの指示に従ってインストールを完了してください。"
    echo ""
    echo "（または手動で: sudo installer -pkg $CONTAINER_PKG_PATH -target /）"
    echo ""
    echo "インストール完了後、Enterキーを押して続行してください..."
    echo "=============================================="

    # インストーラーを自動で開く
    open "$CONTAINER_PKG_PATH"

    read -r

    # 再確認
    if command -v container &> /dev/null; then
        INSTALLED_VERSION=$(container --version 2>/dev/null | grep -oE '[0-9]+\.[0-9]+\.[0-9]+' | head -1)
        echo "OK: Apple Container ${INSTALLED_VERSION} がインストールされました"
    else
        echo "ERROR: Apple Container のインストールに失敗しました。"
        echo "手動でインストーラーを実行してください: $CONTAINER_PKG_PATH"
        exit 1
    fi
fi

# Rosetta 2 確認・インストール（Apple Silicon でのみ必要）
echo ""
echo "Step 5/6: Rosetta 2を確認しています..."
if [ "$(uname -m)" = "arm64" ]; then
    # /usr/bin/true は universal binary (x86_64 + arm64e) のため Rosetta 有無の機能確認として信頼できる
    if arch -x86_64 /usr/bin/true 2>/dev/null; then
        echo "OK: Rosetta 2 はインストール済みです"
    else
        echo "Rosetta 2 がインストールされていません。"
        echo "Apple Container が x86_64 buildkit を実行するために必要です。"
        echo "自動インストールを実行します: softwareupdate --install-rosetta --agree-to-license"
        echo "（sudo は不要。管理者権限の Mac ユーザーで実行していれば自動完了します）"
        echo ""
        # --agree-to-license で EULA 同意プロンプト（"A" を入力して Enter）をスキップ
        # sudo 不要・管理者権限ユーザー前提。失敗時は ERROR + exit 1 で明示する
        if ! softwareupdate --install-rosetta --agree-to-license; then
            echo "ERROR: Rosetta 2 のインストールに失敗しました。"
            echo "手動で 'softwareupdate --install-rosetta' を実行してから再度 setup.sh を実行してください。"
            exit 1
        fi
        if arch -x86_64 /usr/bin/true 2>/dev/null; then
            echo "OK: Rosetta 2 がインストールされました"
        else
            echo "ERROR: Rosetta 2 のインストール後も検出できませんでした。"
            echo "手動で 'softwareupdate --install-rosetta' を実行してから再度 setup.sh を実行してください。"
            exit 1
        fi
    fi
else
    echo "OK: Intel Mac のため Rosetta 2 は不要です（スキップ）"
fi

# Apple Containerシステムサービス確認・起動
echo ""
echo "Step 6/6: Apple Containerシステムサービスを確認しています..."
if container system status &> /dev/null; then
    echo "OK: Apple Containerシステムサービスが起動しています"
else
    echo "Apple Containerシステムサービスが停止しています。"
    echo ""
    echo "=============================================="
    echo "システムサービスを開始する必要があります。"
    echo ""
    echo "別のターミナルウィンドウで以下のコマンドを実行してください："
    echo ""
    echo "  container system start"
    echo ""
    echo "初回実行時は対話的な入力が必要です："
    echo "  - カーネルのインストール確認が表示されたら: Y を入力してEnter"
    echo ""
    echo "完了後、このターミナルに戻ってEnterキーを押してください..."
    echo "=============================================="
    read -r

    # 再確認
    if container system status &> /dev/null; then
        echo "OK: Apple Containerシステムサービスが起動しました"
    else
        echo "WARNING: システムサービスの起動を確認できませんでした。"
        echo "手動で 'container system start' を実行してください。"
    fi
fi

echo ""
echo "=== セットアップ完了 ==="
echo ""
echo "環境が正常にセットアップされました。"
echo ""
echo "次のステップ:"
echo "  1. サーバー起動: ./scripts/start.sh"
echo "  2. サーバー停止: ./scripts/stop.sh"
echo ""
