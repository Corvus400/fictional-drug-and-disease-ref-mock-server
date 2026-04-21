# fictional-drug-and-disease-ref-mock-server

架空の医薬品・疾患リファレンスを返す Scenario-based Mock Server。

## Build & Test

- Build: `./gradlew build`
- Test: `./gradlew test`
- Code style check: `./gradlew spotlessCheck`
- Code style fix: `./gradlew spotlessApply`
- Fat JAR: `./gradlew buildFatJar` → `build/libs/mock-server-all.jar`

## Server Start/Stop (Apple Container required)

Always use the scripts below. Local startup via `./gradlew run` is **forbidden** (causes zombie processes in Ghostty, increasing memory consumption).

- Setup: `scripts/setup.sh` (first time only)
- Start: `scripts/start.sh` (build + start in Apple Container)
- Stop: `scripts/stop.sh` (stop + delete Apple Container)

## Verification

1. Start the container with `scripts/start.sh`
2. Verify API responses with curl
3. Stop the container with `scripts/stop.sh` when done

## Code Style

Enforced by .editorconfig + ktlint (Spotless). Key rules:
- No wildcard imports
- Use named arguments
- Max line length: 120 characters

## Architecture Notes

- `ScenarioManager` uses Mutex-protected thread-safe state management
- Scenario resolution priority: X-Mock-Scenario header > Admin API override > default
- Admin API (`/__admin/`) enables runtime scenario switching
- No auth verification — always returns a response

## Skills, Agents & Rules Reference

- Adding endpoints: use `add-fixture` skill
- Reviewing fixtures before commit: use `fixture-review` skill
- Fixture quality (dates, fields, types): `.claude/rules/fixture-quality.md`
- Dynamic state management (POST/GET linkage): `.claude/rules/dynamic-state.md`
- Reference integrity (drug ↔ disease cross references): `.claude/rules/product-id-registry.md`
- Endpoint implementation (routing, conflicts): `.claude/rules/endpoint-implementation.md`
- Test conventions & Admin API: `.claude/rules/test-conventions.md`
- Client (debug) connection: `.claude/rules/emulator-connection.md`
- Reviewing Renovate dependency update PRs: use `check-update-all-dependencies` skill
