---
paths:
  - "src/test/kotlin/**/*.kt"
---

# Test Conventions

## Test Framework

- Use Ktor test host: `testApplication { }` pattern
- Place test files in `src/test/kotlin/<package>/routes/<domain>/`
- Create a corresponding test file for each route module

## Pre-test Server Check

Before running curl commands or test scripts, verify Mock Server is running.
If not running, start it with `scripts/start.sh` (do not prompt the user — start it yourself).

## Entity IDs in Fixtures

Use the project's fixed ID formats; keep IDs stable across test runs.

- Drugs: `drug_NNNN` (4-digit zero-padded, e.g. `drug_0001`)
- Diseases: `disease_NNNN` (4-digit zero-padded, e.g. `disease_0001`)
- No date/week suffixes.

## Admin API Operations in Tests

Rules derived from failure patterns E5-E7.

1. **Dynamic mode vs static override**: Admin API provides two modes — StateManager initialization (dynamic: POST operations reflect on GET responses) and `configs/{endpointName}` override (static: display-only scenario switching). Use dynamic mode when testing stateful operations (add/remove/quantity change). Use static override when testing display-only scenarios.
2. **Explicit scenario config**: When switching scenarios in tests, always call `client.post("/__admin/configs/{endpointName}")`. Set scenarios explicitly at the beginning of each test to prevent state pollution between tests.
3. **Admin reset**: After tests that modify state, call `client.post("/__admin/reset")`. Since `@Test` method execution order is not guaranteed, each test must be independently runnable. Exception: when `testApplication` is recreated for every test case, implicit reset occurs.

## EXPECTED Values in Tests

1. **EXPECTED text must come from resource files**: UI text assertions must reference `strings.xml` or equivalent resource files. Inferring UI text from ViewModel method names or field names is forbidden. Internal resource names (e.g., `ic_recipe_exists`, `text_check_the_recipe`) must not appear in EXPECTED values — use the actual user-facing string.

## Fixture-Test Co-update

1. **Simultaneous update requirement**: When modifying fixtures (scenario names, product data, response structure), update related test shells in the same commit. "Update tests later" is forbidden — broken tests must never be committed.

## Tautological Test Prohibition

1. **No self-referencing assertions**: Tests that compare a fixture's return value against the same fixture value are meaningless (they only prove the fixture returns what it returns). Tests must verify one of: API input→output transformation, scenario-switching response changes, error handling, or cross-API state consistency.

## GAP Analysis Verification

1. **GAP analysis verification procedure**: When verifying `gap_analysis.md` output: (a) resolve Japanese screen titles to Activity/Fragment class names first, (b) cross-reference with test shells using both Activity names and UI button labels/navigation triggers, (c) before concluding "GAP" (uncovered), directly read all related test shells to confirm the scenario is truly missing.
