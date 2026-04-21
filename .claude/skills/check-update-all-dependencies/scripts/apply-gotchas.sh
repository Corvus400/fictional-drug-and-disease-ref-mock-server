#!/usr/bin/env bash
# Phase A gotchas detector. stdin: facts JSON. stdout: warnings JSON.
set -u -o pipefail

LIBS_TOML="${1:?libs.versions.toml path required}"
RENOVATE_JSON="${2:?renovate.json path required}"

FACTS="$(cat)"

warnings='[]'
add() {
  local code="$1"; local pkg="$2"; local note="$3"
  warnings="$(jq --arg c "$code" --arg p "$pkg" --arg n "$note" \
    '. + [{code:$c, package:$p, note:$n}]' <<<"$warnings")"
}

# PRE_RELEASE
in_versions=false
while IFS= read -r line; do
  case "$line" in
    "[versions]") in_versions=true; continue ;;
    "["*"]")     in_versions=false; continue ;;
  esac
  $in_versions || continue
  if printf '%s' "$line" | grep -qE '=[[:space:]]*"[^"]*-(alpha|beta|rc|SNAPSHOT|M[0-9]+|dev)[^"]*"'; then
    key="$(printf '%s' "$line" | sed -E 's/^([A-Za-z0-9_.-]+)[[:space:]]*=.*/\1/')"
    ver="$(printf '%s' "$line" | sed -E 's/.*=[[:space:]]*"([^"]+)".*/\1/')"
    add "PRE_RELEASE" "$key" "version=$ver is a pre-release"
  fi
done < "$LIBS_TOML"

# INLINE_VERSION
in_libs=false
while IFS= read -r line; do
  case "$line" in
    "[libraries]") in_libs=true; continue ;;
    "["*"]")       in_libs=false; continue ;;
  esac
  $in_libs || continue
  if printf '%s' "$line" | grep -qE 'version[[:space:]]*=[[:space:]]*"' \
     && ! printf '%s' "$line" | grep -q 'version.ref'; then
    key="$(printf '%s' "$line" | sed -E 's/^([A-Za-z0-9_.-]+)[[:space:]]*=.*/\1/')"
    add "INLINE_VERSION" "$key" "inline version; consolidate into [versions]"
  fi
done < "$LIBS_TOML"

# MAJOR_BUMP
while read -r row; do
  [ -z "$row" ] && continue
  coord="$(jq -r '.coordinate' <<<"$row")"
  old="$(jq -r '.old_version' <<<"$row")"
  new="$(jq -r '.new_version' <<<"$row")"
  om="$(printf '%s' "$old" | sed -E 's/^v?([0-9]+).*/\1/')"
  nm="$(printf '%s' "$new" | sed -E 's/^v?([0-9]+).*/\1/')"
  [[ -z "$om" || -z "$nm" ]] && continue
  if [ "$om" != "$nm" ]; then
    add "MAJOR_BUMP" "$coord" "major: $old → $new"
  fi
done < <(jq -c '.packages[]' <<<"$FACTS")

# GROUP_DRIFT
pr_group="$(jq -r '.group_name // empty' <<<"$FACTS")"
if [ -n "$pr_group" ]; then
  known="$(jq -r '.packageRules[].groupName // empty' "$RENOVATE_JSON" | sort -u)"
  if ! printf '%s\n' "$known" | grep -Fxq "$pr_group"; then
    case "$pr_group" in
      *"update "*|*"Update "*) : ;;
      *) add "GROUP_DRIFT" "" "PR group '$pr_group' not in renovate.json" ;;
    esac
  fi
fi

# VULN_ALERT
if [ "$(jq -r '.is_vulnerability_alert' <<<"$FACTS")" = "true" ]; then
  add "VULN_ALERT" "" "security advisory; urgent merge recommended"
fi

# MISSING_RELEASE_NOTES
empty_count="$(jq '[.packages[] | select(.release_notes_raw == "")] | length' <<<"$FACTS")"
total_count="$(jq '.packages | length' <<<"$FACTS")"
if [ "$total_count" -gt 0 ] && [ "$empty_count" -eq "$total_count" ]; then
  add "MISSING_RELEASE_NOTES" "" "all packages empty; fallback required"
fi

jq -n --argjson w "$warnings" '{warnings: $w}'
