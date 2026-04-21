---
name: fixture-review
description: >-
  **Use this skill proactively** when reviewing fixture changes before commit.
  Always use this skill when:
  - Fixtureファイルの変更をコミットする前のレビュー
  - fixture-quality.md のルール適合確認
  - 商品IDの3層登録確認
  - POST->GET状態反映の確認
  - User says: "Fixtureレビュー", "フィクスチャ確認", "コミット前確認",
    "fixture review", "変更のレビュー"
  Automated review checklist for fixture changes.
---

# Fixture Review Workflow

Automated review checklist for fixture/route/model changes before commit.

## Step 1: Identify Changed Files

```bash
git diff --name-only
git diff --cached --name-only
```

Categorize changes: Fixture new/modified | Route added | Model changed | Test added/modified

## Step 2: Fixture Quality Check (A1-A7)

Run these grep checks on all changed fixture/model files:

### Date format (A1)
```bash
grep -n 'yyyy-MM-dd' <changed-files>
```
Expected: 0 matches. All dates must use `yyyy/MM/dd`.

### Image paths (A2)
```bash
grep -n 'localhost' <changed-files>
```
Expected: 0 matches. No `http://localhost` in image paths.

### Null/empty consistency (A4)
List all `null`, `emptyMap()`, `emptyList()` in changed files and verify against client-side DTO nullability.

### Type matching (A5)
For each new/modified field, verify the type matches the corresponding {{API_CLIENT_REPO}} DTO field exactly (Int vs Double, nullable vs non-nullable).

## Step 3: Product ID 3-Layer Check (C1-C3)

Extract all `{{ID_PATTERN}}` patterns from changed files:
```bash
grep -oE '{{ID_PATTERN}}[0-9]+' <changed-files> | sort -u
```

For each ID, verify 3-layer registration:
```bash
grep -rn "<ID>" src/main/kotlin/ --include="*.kt"
```
Must appear in: `{{LAYER_1}}`, `{{LAYER_2}}` (indirect via Fixture objects), `{{LAYER_3}}`.

## Step 4: Dynamic State Check (B1-B5)

If POST/PUT routes were added or modified:

1. Verify `call.receiveParameters()` or OperationParser usage (not raw string parsing)
2. Verify `scenarioManager.setOverride()` exists for related GET endpoints
3. If a StateManager is involved, verify `isActive` check before state access

## Step 5: Test Coverage Check (E5-E7)

For each changed Route file, verify:

1. Corresponding test file exists in `src/test/kotlin/.../routes/<domain>/`
2. If dynamic state operations are tested: StateManager initialization (not static `configs/{name}`) is used
3. Tests that modify state include `/__admin/reset` or use independent `testApplication` blocks

## Step 6: Review Summary

Report for each check:
- **PASS**: Check passed
- **WARN**: Potential issue found — manual verification recommended
- **FAIL**: Definite issue found — must fix before commit

If all checks pass, report "Ready to commit."
