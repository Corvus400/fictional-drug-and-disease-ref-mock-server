<!-- TEMPLATE_PLACEHOLDER_MARKER
置換対象プレースホルダー:
- {{REPO_NAME}} / {{GITHUB_REPO}} / {{PURPOSE}} → 関連リポジトリ情報の各行を実値で埋めること
置換後にこのブロックを削除すること
-->

# Cross-Repository References

## Related Repositories

Refer to these repositories when implementing or investigating endpoints:

| Repository      | GitHub           | Purpose  |
|-----------------|------------------|----------|
| {{REPO_NAME}}   | {{GITHUB_REPO}}  | {{PURPOSE}} |

## Repository Resolution

1. Search for the repository in the parent directory of mock-server-base
2. If found, read its contents directly
3. If not found, clone from `https://github.com/<organization>/<repo-name>` into the parent directory

## Context Conservation

Delegate cross-repo investigations to Agent / Task sub-processes to minimize main context window consumption.
