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

## テンプレート化チェックリスト (Claude Code 向け)

このリポジトリを新サービス用にフォークした直後、以下の手順でサービス固有の値に差し替えること。

**`{{...}}` プレースホルダーの検索**: `.claude/` 配下のファイルに残存。
`grep -r "{{" .claude/` で対象ファイルを確認できる。

1. **Gradle / Kotlin 設定の変更**（プレースホルダーなし、手動変更が必要）:
   - `build.gradle.kts`: `group`, `mainClass`, `archiveFileName`
   - `settings.gradle.kts`: `rootProject.name`
   - `src/main/resources/application.yaml`: mainClass
   - `scripts/{start,stop,setup}.sh`: `CONTAINER_NAME`, `IMAGE_NAME`

2. **Kotlin パッケージ移動**:
   - `src/main/kotlin/io/github/corvus400/mockserverbase/` を対象パッケージのディレクトリに移動
   - 全 .kt ファイルの `package` と `import` を更新

3. **`.claude/` 配下のプレースホルダー置換**:
   - `rules/` および `skills/` 内の `{{API_CLIENT_REPO}}`, `{{ID_PATTERN}}`, `{{LAYER_1}}` 等を置換
   - `agents/dto-verifier.md` の `{{PRIMARY_CLIENT_REPO}}` 等を置換

4. **サンプルコードの差し替え**:
   - `routes/sample/`, `fixture/sample/`, `model/sample/` をサービス固有モジュールで置換
   - `ApiTag` に業務タグ追加、`ScreenTag` に画面タグ追加、`ScreenMapping` 更新

5. **完了後**: `<!-- TEMPLATE_PLACEHOLDER_MARKER -->` ブロックとこのセクションを削除
