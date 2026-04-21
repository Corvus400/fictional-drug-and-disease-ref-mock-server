---
paths:
  - "src/**/*.kt"
  - "*.kts"
---

# Kotlin Inspection Rules

## detekt

This project uses detekt for static analysis. Verify after changes:

```bash
./gradlew detektMain
```

Config: `config/detekt/detekt.yml`

## Non-null assertion operator (`!!`) prohibition

### Main source

`!!` is prohibited. Use one of the following alternatives:

- `requireNotNull(value) { "message" }` — when null is an error condition
- `value ?: defaultValue` — when a default value is acceptable
- `value?.let { ... }` — safe call + scope function

### Test source

`!!` is prohibited. Use `assertNotNull()` + smart cast:

```kotlin
val result = response.nullableField
assertNotNull(result) // smart casts to non-null type
assertEquals("expected", result.field)
```

## Nullable collection handling

| Prohibited       | Use instead  |
|------------------|--------------|
| `?: emptyList()` | `.orEmpty()` |
| `?: emptyMap()`  | `.orEmpty()` |
| `?: listOf()`    | `.orEmpty()` |
| `?: mapOf()`     | `.orEmpty()` |

## Collection empty checks

| Prohibited      | Use instead     |
|-----------------|-----------------|
| `.size > 0`     | `.isNotEmpty()` |
| `.size == 0`    | `.isEmpty()`    |
| `.count() > 0`  | `.isNotEmpty()` |
| `.count() == 0` | `.isEmpty()`    |

## Unused code

- Delete unused code. Do not suppress with `@Suppress`.
- Use `@Suppress` only when there is a legitimate reason not to delete, and always add KDoc explaining why.

## `@Suppress` annotations

- Prohibited by default.
- Only add `@Suppress` when an IntelliJ Inspection report (XML) has been provided by the user. Do not add preemptive `@Suppress` without a report.
- For detekt-detectable warnings, verify with `./gradlew detektMain`.
- When necessary, always include KDoc documenting the suppression reason.

```kotlin
/**
 * Used by kotlinx.serialization via reflection — appears unused but is required.
 */
@Suppress("unused")
val field: String = ""
```

## FixtureProvider implementation `@Suppress`

Every `object` implementing `FixtureProvider<T>` must have `@Suppress("unused", "RedundantSuppression")` with KDoc explaining both reasons. Interface-level `@Suppress` does **not** propagate to implementations — always annotate each implementation individually.

## RedundantSuppression handling

IntelliJ's `RedundantSuppression` produces false positives — removing `@Suppress("unused")` causes actual unused warnings to reappear. When reported, change to `@Suppress("unused", "RedundantSuppression")` instead of deleting.

## Project dictionary

Add HTML/JavaScript standard terms and domain-specific terms to `.idea/dictionaries/project.xml` for spell-check false positives.
