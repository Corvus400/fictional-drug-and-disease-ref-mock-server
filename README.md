# fictional-drug-and-disease-ref-mock-server

架空医薬品・疾病 データを返す Flutter/iOS/Android アプリ開発用のシナリオベース Mock Server。

## クイックスタート

1. 初回セットアップ:
   ```bash
   ./scripts/setup.sh
   ```
2. サーバー起動:
   ```bash
   ./scripts/start.sh
   ```
3. ブラウザで確認:
   - [対応画面・シナリオ・Fixture概要カタログ](http://localhost:8080/__admin/catalog)
   - [Swagger UI](http://localhost:8080/swagger) / [ReDoc](http://localhost:8080/redoc)
4. サーバー停止:
   ```bash
   ./scripts/stop.sh
   ```

## 動作環境

- macOS（Apple Silicon）
- JDK 21+
- Apple Container

## ドキュメント

サーバー起動後、以下のURLで詳細情報を確認できる。コードから自動生成されるため手動メンテナンス不要。

| パス                 | 説明                              |
|--------------------|---------------------------------|
| `/__admin/catalog` | 対応画面・シナリオ・Fixture概要カタログ         |
| `/swagger`         | Swagger UI（API仕様・リクエスト/レスポンス詳細） |
| `/redoc`           | ReDoc（リファレンス形式のAPI仕様）           |
| `/openapi.json`    | OpenAPI仕様（JSON形式）               |

## アーキテクチャ

シナリオベースの Mock Server。Admin API または `X-Mock-Scenario` ヘッダーでレスポンスを動的に切り替える。

### シナリオ解決の優先順位

1. `X-Mock-Scenario` ヘッダー（最優先）
2. Admin API override
3. デフォルトシナリオ

### エンドポイント追加パターン

Model → Fixture（`FixtureProvider<T>`）→ Route（`scenarioRoute<T>()`）→ Routing.kt 登録の4層構成。
詳細は Claude Code の `add-fixture` スキルを参照。

## Admin API

シナリオ切り替え・状態管理のための管理用API。

```bash
# 全エンドポイントのシナリオを確認
curl -s http://localhost:8080/__admin/configs | jq

# 特定エンドポイントのシナリオ切り替え
curl -X POST http://localhost:8080/__admin/configs/drugs \
  -H "Content-Type: application/json" \
  -d '{"state": "empty"}'

# 全状態リセット
curl -X POST http://localhost:8080/__admin/reset
```

### X-Mock-Scenario ヘッダー

```bash
curl -H "X-Mock-Scenario: empty" http://localhost:8080/api/drugs
```

## 開発

```bash
# ビルド
./gradlew build

# テスト
./gradlew test

# コードスタイル確認・修正
./gradlew spotlessCheck
./gradlew spotlessApply

# 静的解析
./gradlew detektMain

# Fat JAR
./gradlew buildFatJar
```

### コミット前ゲート (pre-commit)

CI の lint job はローカルゲートに移管している。初回のみ pre-commit をインストールし、Git hook を有効化する。

```bash
brew install pre-commit
pre-commit install --hook-type pre-commit --hook-type pre-push
```

`core.hooksPath` を設定している環境では `pre-commit install` が拒否される。その場合は既存のグローバル hook から以下を呼び出す。

```bash
pre-commit run --hook-stage pre-commit
pre-commit run --hook-stage pre-push
```

- `pre-commit` stage: `git fetch origin main` 後に `./gradlew spotlessCheck -Pspotless.ratchet=true`
- `pre-push` stage: `./gradlew detektMain` / `./gradlew detektTest`
- 全件確認: `pre-commit run --all-files`
- push gate の全件確認: `pre-commit run --hook-stage pre-push --all-files`
- 一時的に detekt をスキップ: `SKIP=gradle-detekt-main,gradle-detekt-test git push`

Spotless は `origin/main` からの差分に ratchet する。古い base を参照しないよう、pre-commit stage は Spotless 実行前に `git fetch origin main` を自動実行する。

## ドキュメント方針

> 本 README にはコードから導出できる情報・陳腐化する情報を記載しない。
> 詳細は [ADR: README に陳腐化する情報を書かない](docs/adr/20260316_READMEに陳腐化する情報を書かない.md) を参照。

## 技術スタック

- Kotlin / Ktor / JDK 21+
- kotlinx.serialization（JSON処理）
- ktor-openapi-tools（OpenAPI / Swagger UI / ReDoc）
- Apple Container（コンテナ実行環境）
- Spotless + ktlint（コードスタイル）

バージョン詳細は [`gradle/libs.versions.toml`](gradle/libs.versions.toml) を参照。
