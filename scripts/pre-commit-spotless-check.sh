#!/bin/bash
# pre-commit stage で Spotless の ratchet base を最新化してから実行する。
set -euo pipefail

git fetch --quiet origin main
./gradlew spotlessCheck -Pspotless.ratchet=true --quiet --configuration-cache
