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

Before writing any code, complete these checks to prevent the most common failure pattern (70% of all fixes):

1. **DTO field inventory**: Search {{API_CLIENT_REPO}} for the corresponding DTO:
   ```bash
   grep -rn "class XxxResponse\|class XxxJson\|class XxxBean" ../{{API_CLIENT_REPO}}/ --include="*.kt" | head -10
   ```
   Read the DTO file and list ALL fields with their types and nullability.

2. **Endpoint route check**: Verify no existing route handles the same path:
   ```bash
   grep -r "path-pattern" src/main/kotlin/ --include="*.kt"
   ```

3. **Screen endpoint coverage**: If implementing for a specific screen, list ALL API calls the screen makes (inspect ViewModel/UseCase or use network trace). Missing sub-endpoints break user interactions.

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
- Product IDs: use long-lived items in `{{ID_PATTERN_EXAMPLE}}` format, no date/week suffix
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

### Product ID Consistency
- Verify product IDs (`{{ID_PATTERN_EXAMPLE}}`) exist in all 3 layers:
  1. `{{LAYER_1}}` (getItems API)
  2. `{{LAYER_2}}` (search/category APIs)
  3. `{{LAYER_3}}` (cart operations)
- Prefer long-lived product IDs

### Parameter Handling
- Process all query/form parameters the client sends in the Route
- Check for missing scenario branches when unprocessed parameters exist

## Post-implementation Checks

After completing implementation, verify:

1. **3-layer product ID registration** (if new IDs were added):
   ```bash
   grep -rn "{{ID_PATTERN_EXAMPLE}}" src/main/kotlin/ --include="*.kt"
   ```
   Must hit `{{LAYER_1}}`, `{{LAYER_2}}` (indirect), and `{{LAYER_3}}`.

2. **POST → GET state reflection** (if POST route was added):
   Verify `scenarioManager.setOverride()` is called for all related GET endpoints.

3. **Test file**: Create a corresponding test file in `src/test/kotlin/.../routes/<domain>/`.

4. **Catalog metadata**: Set `scenarioTitles` and implement `describeFixture()`.
