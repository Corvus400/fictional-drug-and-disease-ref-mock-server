<!-- TEMPLATE_PLACEHOLDER_MARKER
置換対象プレースホルダー:
- {{LAYER_1}} → getItems API 担当の Fixture クラス名
- {{LAYER_2}} → 検索/カテゴリ API 担当のレジストリクラス名
- {{LAYER_3}} → カート操作担当のカタログクラス名
置換後にこのブロックを削除すること
-->

---
paths:
  - "src/main/kotlin/**/fixture/**/*.kt"
  - "src/main/kotlin/**/model/**/*.kt"
---

# Fixture Quality Rules

Mandatory checks derived from 72+ fix commits. For the full workflow, invoke the `add-fixture` skill.

1. **Date format**: Use `yyyy/MM/dd`. `yyyy-MM-dd` is forbidden. With time: `yyyy/MM/dd HH:mm:ss`. Use `MockDateHelper` for relative/future dates — hardcoded dates become stale over time.
2. **Image paths**: Use `/`-prefixed relative paths or `http://` absolute URLs. `http://localhost` is forbidden (unreachable from Android emulator). Verify new image path patterns are covered by `PlaceholderImageModule`.
3. **DTO field completeness**: Before creating a Fixture, check the corresponding DTO in the API client repository and include ALL fields. Missing fields cause deserialization failures or zero-value display.
4. **null / `{}` / `[]` distinction**: Match client-side deserialization expectations. Kotlin `T?` → `null`, non-null `T` with default → `{}` or `[]`. Each element in a list must have the same field structure.
5. **Type matching**: Field types must exactly match client DTO types (Int/Double/Long, nullable/non-nullable). Kotlin `@Serializable` does not perform implicit type conversion — `Int` field with `Double` value crashes.
6. **Subscription product prices**: When a product has a subscription/membership price flag set to `true`, the `prices` map must contain the corresponding member key. `emptyMap()` is forbidden.
7. **Special state fields**: For `cancelled`/`paused` states, verify ALL fields that the app's screen-transition logic checks (ViewModel/UseCase). Cross-check the ViewModel/UseCase in the API client repository to enumerate all required fields.
8. **Legacy + new field pairs**: Set both legacy and new fields when a field was renamed across API versions. iOS/Android version differences mean one side may rely on legacy fields.
9. **Field name casing**: Match client-side `@SerialName` exactly. All-caps fields like `RESULT`, `RESULT_DETAIL` must use the exact casing — JSON key case mismatch causes deserialization failure.
10. **POST success → GET state reflection**: When a route returns a POST/PUT success response, switch the related GET endpoint's scenario via `scenarioManager.setOverride()`.
11. **3-layer product ID registration**: New product IDs must be registered in all 3 layers: `{{LAYER_1}}` (getItems API) + `{{LAYER_2}}` (search/category APIs) + `{{LAYER_3}}` (cart operations).
12. **Scenario map default**: Always include a `"default"` key in the scenario map that matches the `defaultScenario` parameter of `scenarioRoute`.
13. **Catalog metadata**: Set `scenarioTitles` (Japanese titles for all scenarios) and implement `describeFixture()` (key field values: item count, amount, etc.) for the catalog HTML display.
14. **MockDateHelper for relative dates**: Use `MockDateHelper` utility functions for future/relative dates (delivery dates, deadlines). Hardcoded dates become "past dates" over time, breaking test results.
15. **copy() usage verification**: When using `copy()` to create fixture variants, verify all inherited fields are correct for the new scenario. grep for the product ID across the fixture file, check `allFixtures` list order (the last-put entry wins for duplicate keys), and confirm the last-put fixture's fields are as intended.
16. **Default fixture change impact**: Before modifying default fixtures (e.g., `itemsDefault`), grep all references (`grep -rn "itemsDefault\|allFixtures" *.kt`) and verify changes don't affect unrelated test scenarios. Create dedicated scenario fixtures for test-specific values instead of modifying defaults.
