---
name: endpoint-viability-check
description: >-
  **Use this skill proactively** when implementing a new API endpoint for the mock-server-base.
  Always use this skill BEFORE add-fixture when:
  - 新しいエンドポイントの実装を開始するとき（Issue番号が提示された場合）
  - エンドポイントがまだクライアント側で使用されているか確認が必要なとき
  - User says: "Issue #XX を実装", "エンドポイントの生存確認", "まだ使われているか確認",
    "このエンドポイントは必要か", "実装する意味があるか確認"
  Check if an endpoint is still actively used in {{API_CLIENT_REPO}} before investing implementation effort.
---

# Endpoint Viability Check

Verify that a target API endpoint is still actively used in {{API_CLIENT_REPO}} before implementation.

## Workflow

### Step 1: Resolve {{API_CLIENT_REPO}} Repository

Follow the instructions in `.claude/rules/cross-repo.md` to locate the {{API_CLIENT_REPO}} repository.

### Step 2: Search for Endpoint Usage

Search the `develop` branch:

1. **API client definition**: Search `*ApiClient.kt` files for the endpoint path
2. **Callers**: Search UseCase / Repository / ViewModel for references to the API client method
3. **Judgment**:
   - Definition exists + callers found → endpoint is actively used
   - Definition exists + zero callers → "defined but unused"
   - No definition found → endpoint has been removed

### Step 3: Check GitHub for Removal/Deprecation

Use `gh` CLI to search {{API_CLIENT_REPO}} Issues and PRs for deletion or deprecation of the endpoint:

```bash
gh search issues --repo {{API_CLIENT_REPO}} "<endpoint-keyword>" --state all --limit 10
gh search prs --repo {{API_CLIENT_REPO}} "<endpoint-keyword>" --state all --limit 10
```

### Step 4: Report Result

- **VIABLE**: Endpoint is actively used. Report file paths and function names of callers. Proceed with implementation.
- **NOT_VIABLE**: Endpoint is unused or removed. Report evidence. Propose closing the Issue with an investigation comment.
