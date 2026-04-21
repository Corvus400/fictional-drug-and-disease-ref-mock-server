---
paths:
  - "src/main/kotlin/**/scenario/**/*.kt"
  - "src/main/kotlin/**/routes/**/*.kt"
---

# Dynamic State Management Rules

Rules for POST/GET linkage and state management, derived from failure patterns B1-B5.

1. **POST parameter parsing**: Use `call.receiveParameters()` + a dedicated Operation Parser class for type-safe parameter extraction. Raw string parsing via `call.receive<String>()` is forbidden.
2. **POST → GET state reflection**: POST handlers must call `scenarioManager.setOverride()` to switch the related GET endpoint's scenario. Document the affected endpoint names in the POST handler's KDoc with `@see`.
3. **StateManager initialization**: `StateManager.initialize(scenario)` must only be called via Admin API. Direct `initialize()` calls inside Route handlers are forbidden. Always check `isActive` before accessing state.
4. **Cross-endpoint state consistency**: When multiple endpoints reference the same StateManager, ensure returned data is consistent across all related endpoints.
5. **Parameter semantic distinction**: `endpointName` (Admin API scenario identifier) and `path` (HTTP route path) are distinct concepts. `page=` query parameters are for scenario resolution, not routing.
6. **Mutex safety**: `ScenarioManager.mutex` and `StateManager.mutex` are independent. Never call StateManager suspend functions inside `ScenarioManager.mutex` lock — this causes deadlock. Follow the `ScenarioManager.reset()` implementation pattern.
7. **Transition chain design**: When using `ScenarioTransitionChain`, verify endpoint consistency at ALL transitioned states. Chains must terminate (no loops).
8. **Field override keys**: `scenarioManager.mergeFieldOverrides()` field names must be JSON keys, not Kotlin `@SerialName` values.
