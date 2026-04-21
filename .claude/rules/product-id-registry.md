<!-- TEMPLATE_PLACEHOLDER_MARKER
置換対象プレースホルダー:
- {{LAYER_1}} → getItems API 担当の Fixture クラス名
- {{LAYER_2}} → 検索/カテゴリ API 担当のレジストリクラス名
- {{LAYER_3}} → カート操作担当のカタログクラス名
- {{ID_PATTERN_EXAMPLE}} → サービス固有 ID の例 (例: item-1234)
置換後にこのブロックを削除すること
-->

---
paths:
  - "src/main/kotlin/**/fixture/**/*.kt"
  - "src/main/kotlin/**/catalog/**/*.kt"
---

# Product ID Registry Rules

Rules for product ID management, derived from failure patterns C1-C3.

1. **3-layer registration**: New product IDs must be registered in ALL 3 layers:
   - `{{LAYER_1}}` → entries for the primary item fetch API
   - `{{LAYER_2}}` → included via a Fixture in the `allFixtures` list (for search/category APIs)
   - `{{LAYER_3}}` → catalog list entries (for cart add/remove/quantity operations)
   One missing layer causes silent failures — cart operations silently ignore unregistered IDs.

2. **ID format**: Use the service-specific ID format (e.g., `{{ID_PATTERN_EXAMPLE}}`). Do not append weekly/date suffixes. Prefer long-lived product IDs that won't be discontinued.

3. **Verification command**: Run `grep -rn "{{ID_PATTERN_EXAMPLE}}" src/main/kotlin/ --include="*.kt"` and verify hits in all 3 files: `{{LAYER_1}}`, `{{LAYER_2}}` (indirect via Fixture objects), and `{{LAYER_3}}`.
