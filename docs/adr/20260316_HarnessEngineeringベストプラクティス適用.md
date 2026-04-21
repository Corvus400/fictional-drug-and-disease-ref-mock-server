# コンテキスト

記事「Claude Code / Codex ユーザーのための誰でもわかるHarness Engineeringベストプラクティス」
（https://nyosegawa.github.io/posts/harness-engineering-best-practices-2026/）の7つの推奨事項を
本プロジェクト の現状と比較し、客観的に評価した。

プロジェクト現状:
- CI: GitHub Actions（spotlessCheck → build → test の2段階パイプライン）
- コード品質: Spotless + ktlint（.editorconfig ベース）
- CLAUDE.md: 50行（記事推奨値に一致）
- ADR: 導入済み
- Claude Code Hooks: PreToolUse（Mock Server ヘルスチェック）のみ
- テスト: Ktor testApplication パターン、57テストファイル

# 決定

以下の3項目を採用する。

## 1. PostToolUse Hook による自動フォーマット

Write/Edit 後に ktlint CLI で .kt ファイルを自動フォーマットする。
Gradle 経由の spotlessApply（5-10秒）ではなく、ktlint CLI 直接実行（1-2秒）を採用し、
フィードバック速度を最速レイヤー（秒単位）に配置する。

## 2. リンター設定ファイルの保護

.editorconfig の編集を settings.json の deny リストでブロックし、
エージェントがリントエラーを回避するために設定を緩和することを構造的に防止する。

## 3. Hook エラーメッセージの WHY/FIX 形式化

既存のヘルスチェック Hook のエラーメッセージを「何が間違いか」だけでなく
「なぜこのルールがあるか（WHY）」「具体的な修正手順（FIX）」を含む形式に改善し、
エージェントの自己修正能力を向上させる。

以下の7項目は不採用とする。

| 項目                        | 不採用理由                                                              |
|---------------------------|--------------------------------------------------------------------|
| Lefthook/pre-commit hooks | CI で spotlessCheck 実行済み。PostToolUse Hook で即時修正されるためコミット時点で違反が残らない  |
| Stop Hook（テスト通過強制）        | ./gradlew test は15-30秒。completion 毎に実行するには遅すぎる。CI の test ジョブが品質ゲート |
| E2E テスト変更                 | Mock Server にブラウザテストは不要。API テストが適切                                 |
| CLAUDE.md 圧縮              | 50行で記事推奨値に一致                                                       |
| JSON 進捗ファイル               | Markdown プランファイルが Claude Code との相性が良い                              |
| Observability hooks       | Mock Server に可観測性フックは過剰                                            |
| 依存ライブラリ自動更新               | [Renovate 導入フォローアップ Issue](https://github.com/Corvus400/mock-server-base/issues/1) で別途対応予定 |

# 影響

**メリット:**
- フィードバックループの高速化: CI（分単位）に加えて PostToolUse（秒単位）でコードスタイル違反を即時修正
- 設定ファイル保護: .editorconfig の意図しない変更を機械的に防止
- エラーメッセージ改善: エージェントが自己修正しやすくなり、手戻りが減少

**デメリット:**
- PostToolUse Hook の実行により、.kt ファイル編集時に1-2秒のオーバーヘッドが発生
- ktlint CLI JAR が Gradle キャッシュに依存するため、キャッシュクリア後は初回のみ Gradle フォールバックが発生

# コンプライアンス

- **PostToolUse Hook**: settings.json に登録済み。Write/Edit のたびに自動実行される
- **設定保護**: settings.json の deny リストで .editorconfig の編集をブロック
- **CI**: spotlessCheck → build → test の既存パイプラインがフォールバックとして機能
- **ADR**: 本文書により意思決定の根拠を記録
