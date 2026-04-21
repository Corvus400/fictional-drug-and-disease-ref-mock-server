---
name: add-fixture
description: >-
  **Use this skill proactively** when adding new API endpoints or fixtures to the mock-server-base.
  Always use this skill (instead of manually creating files) when:
  - mock-server-baseに新しいAPIエンドポイントを追加するとき
  - 新しいFixtureProviderを作成するとき
  - scenarioRouteを使用した新しいルートを定義するとき
  - 既存エンドポイントに新しいシナリオ（fixture）を追加するとき
  - User says: "フィクスチャを追加", "Fixtureを追加", "エンドポイントを追加", "新しいAPIを追加",
    "シナリオを追加", "mock-serverにAPIを追加"
  Model→FixtureProvider→scenarioRoute→Routing.kt registration in 4 steps.
---

# Add Fixture Workflow

## Pre-flight Checks (BEFORE implementation)

Before writing any code, complete these checks.

1. **Model shape**: Define or locate the Kotlin model under `src/main/kotlin/.../model/<domain>/` and
   list every field with its type and nullability. This project is client-agnostic, so derive the
   shape from the project's own model — not from an external client DTO.

2. **Endpoint route check**: Verify no existing route handles the same path:
   ```bash
   grep -r "path-pattern" src/main/kotlin/ --include="*.kt"
   ```

3. **Cross-reference impact**: If the new fixture references existing `drug_NNNN` / `disease_NNNN`
   IDs, confirm they are already defined on the opposite catalog (see
   `.claude/rules/product-id-registry.md`). If the fixture introduces a new ID that should be
   referenced from the opposite catalog, plan the reverse-side update in the same change set.

## New Endpoint (4 steps)

### Step 1: Model (`src/main/kotlin/.../model/<domain>/`)

```kotlin
@Serializable
data class XxxResponse(val field: String)
```

### Step 2: Fixture (`src/main/kotlin/.../fixture/<domain>/`)

```kotlin
object XxxFixtures : FixtureProvider<XxxResponse> {
    val default = XxxResponse(field = "value")
    val empty = XxxResponse(field = "")

    override fun getByScenario(scenario: String): XxxResponse = when (scenario) {
        "empty" -> empty
        else -> default
    }
}
```

### Step 3: Route (`src/main/kotlin/.../routes/<domain>/`)

**Static route (GET with fixed response):**
```kotlin
fun Application.xxxModule(scenarioManager: ScenarioManager) {
    scenarioRoute(
        path = "/api/xxx",
        endpointName = "xxx",
        defaultScenario = "default",
        fixtureProvider = XxxFixtures,
        scenarioManager = scenarioManager,
    )
}
```

**Dynamic route (POST with parameter parsing):**
```kotlin
fun Application.xxxModule(scenarioManager: ScenarioManager) {
    routing {
        post("/api/xxx") {
            val params = call.receiveParameters()
            // Use OperationParser for type-safe extraction
            val value = params["fieldName"] ?: "default"
            // Update state if needed
            scenarioManager.setOverride("related_get_endpoint", "updated_scenario")
            call.respond(XxxFixtures.default)
        }
    }
}
```

### Step 4: Register (`src/main/kotlin/.../plugins/Routing.kt`)

Add `xxxModule(scenarioManager)` call inside `configureRouting()`.

## Add Scenario to Existing Endpoint

1. Open the existing Fixture file
2. Add new val with scenario data
3. Add case to `getByScenario` when-expression

## Rules

- Always use `scenarioRoute<T>()` helper (fixed-value responses forbidden)
- Always implement `FixtureProvider<T>` interface
- Use named arguments in function calls
- Entity IDs: `drug_NNNN` / `disease_NNNN` (4-digit zero-padded), stable and long-lived, no date/week suffix
- Max line length: 120 chars
- Run `./gradlew spotlessCheck` after changes

## Fixture Quality Checklist

Post-implementation checks derived from 72+ past fix commits.

### Date Format
- Use `yyyy/MM/dd` (`yyyy-MM-dd` is forbidden)
- With time: `yyyy/MM/dd HH:mm:ss`
- Use MockDateHelper for relative dates

### Field Compatibility (iOS/Android)
- Include both legacy and new fields when a field was renamed across API versions
- Match field name casing to client-side DTO (e.g., `RESULT` / `RESULT_DETAIL` for all-caps cases)
- Never set null for Kotlin non-null typed fields

### Response Structure
- Match null / `{}` / `[]` to client-side deserialization expectations
- Match array vs object wrapper structure to the real API

### Image/URL Paths
- Use `/`-prefixed relative paths or `http://` absolute URLs
- `http://localhost` is forbidden (unreachable from Android emulator)
- Verify new image path patterns are covered by PlaceholderImageModule

### Dynamic State Management
- On POST/PUT success, switch related GET scenarios via `scenarioManager.setOverride()`
  - Example: updPlacement success → switch getPlacement to `placement_enabled`

### Cross-reference Integrity (drug ↔ disease)
- Whenever a fixture references `drug_NNNN` or `disease_NNNN`, confirm the referenced ID is
  defined on the opposite catalog.
- When introducing a new ID, update any reverse-side cross-reference lists in the same change set.
- Full rule: `.claude/rules/product-id-registry.md`.

### Parameter Handling
- Process all query/form parameters the client sends in the Route
- Check for missing scenario branches when unprocessed parameters exist

## Post-implementation Checks

After completing implementation, verify:

1. **Cross-reference integrity** (if new IDs were added or cross-references introduced):
   ```bash
   grep -rnE '(drug|disease)_[0-9]{4}' src/main/kotlin/ --include="*.kt"
   ```
   Every ID referenced from the opposite catalog must resolve to a fixture on its own side.

2. **POST → GET state reflection** (if POST route was added):
   Verify `scenarioManager.setOverride()` is called for all related GET endpoints.

3. **Test file**: Create a corresponding test file in `src/test/kotlin/.../routes/<domain>/`.

4. **Catalog metadata**: Set `scenarioTitles` and implement `describeFixture()`.
