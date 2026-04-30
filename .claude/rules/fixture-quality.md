---
paths:
  - "src/main/kotlin/**/fixture/**/*.kt"
  - "src/main/kotlin/**/model/**/*.kt"
---

# Fixture Quality Rules

Mandatory checks derived from 72+ fix commits. For the full workflow, invoke the `add-fixture` skill.

1. **Date format**: ISO 8601. Date only uses the spec notation `YYYY-MM-DD` (example `2026-04-23`); date-time uses `YYYY-MM-DDThh:mm:ss`. Always emit values through `IsoDateFormatter` — never hand-write date strings or `DateTimeFormatter.ofPattern`. Inside Kotlin patterns the year letter is lowercase `yyyy`; uppercase `YYYY` is week-based-year and will mis-render the last week of December. `yyyy/MM/dd` is forbidden. For a date you own in code, build it as `IsoDateFormatter.formatDate(date = LocalDate.of(...))` so the format is exercised by tests and parseable by iOS `ISO8601DateFormatter` / Flutter `DateTime.parse` / Kotlin `LocalDate.parse`.
2. **Image paths**: Use `/`-prefixed relative paths or `http://` absolute URLs. `http://localhost` is forbidden (unreachable from Android emulator). Verify new image path patterns are covered by `DosageFormImageModule` or another explicit image route module.
3. **DTO field completeness**: Before creating a Fixture, check the corresponding DTO in the API client repository and include ALL fields. Missing fields cause deserialization failures or zero-value display.
4. **null / `{}` / `[]` distinction**: Match client-side deserialization expectations. Kotlin `T?` → `null`, non-null `T` with default → `{}` or `[]`. Each element in a list must have the same field structure.
5. **Type matching**: Field types must exactly match client DTO types (Int/Double/Long, nullable/non-nullable). Kotlin `@Serializable` does not perform implicit type conversion — `Int` field with `Double` value crashes.
6. **Subscription product prices**: When a product has a subscription/membership price flag set to `true`, the `prices` map must contain the corresponding member key. `emptyMap()` is forbidden.
7. **Special state fields**: For `cancelled`/`paused` states, verify ALL fields that the app's screen-transition logic checks (ViewModel/UseCase). Cross-check the ViewModel/UseCase in the API client repository to enumerate all required fields.
8. **Legacy + new field pairs**: Set both legacy and new fields when a field was renamed across API versions. iOS/Android version differences mean one side may rely on legacy fields.
9. **Field name casing**: Match client-side `@SerialName` exactly. All-caps fields like `RESULT`, `RESULT_DETAIL` must use the exact casing — JSON key case mismatch causes deserialization failure.
10. **POST success → GET state reflection**: When a route returns a POST/PUT success response, switch the related GET endpoint's scenario via `scenarioManager.setOverride()`.
11. **Cross-catalog reference integrity**: Drug ↔ disease cross-references must stay referentially consistent. Every ID referenced from one side (`drug_NNNN` or `disease_NNNN`) must exist as a registered fixture on the other side. See `.claude/rules/product-id-registry.md`.
12. **Scenario map default**: Always include a `"default"` key in the scenario map that matches the `defaultScenario` parameter of `scenarioRoute`.
13. **Catalog metadata**: Set `scenarioTitles` (Japanese titles for all scenarios) and implement `describeFixture()` (key field values: item count, amount, etc.) for the catalog HTML display.
14. **copy() usage verification**: When using `copy()` to create fixture variants, verify all inherited fields are correct for the new scenario. grep for the product ID across the fixture file, check `allFixtures` list order (the last-put entry wins for duplicate keys), and confirm the last-put fixture's fields are as intended.
15. **Default fixture change impact**: Before modifying default fixtures (e.g., `itemsDefault`), grep all references (`grep -rn "itemsDefault\|allFixtures" *.kt`) and verify changes don't affect unrelated test scenarios. Create dedicated scenario fixtures for test-specific values instead of modifying defaults.
