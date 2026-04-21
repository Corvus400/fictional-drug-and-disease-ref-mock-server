<!-- TEMPLATE_PLACEHOLDER_MARKER
置換対象プレースホルダー:
- {{API_CLIENT_REPO}} → Android アプリリポジトリ名
- {{APP_DEBUG_DEEPLINK}} → キャッシュクリア用 deeplink スキーム (例: myapp-debug://clearcache)
- {{APP_PACKAGE_ID}} → Android debug ビルドの package ID (例: com.example.myapp.debug)
置換後にこのブロックを削除すること
-->

# Android Emulator Connection

## Prerequisites

The {{API_CLIENT_REPO}} app must be built from a branch that:
- adds `network_security_config.xml` to allow cleartext HTTP to localhost (debug builds only)
- defines `{{APP_DEBUG_DEEPLINK}}` URL scheme for cache clearing via DeepLink

## Connection Methods

`scripts/start.sh` prints connection URLs on startup. Use one of these methods:

### Method 1: 10.0.2.2 (recommended)

Android emulator maps `10.0.2.2` to the host machine's `localhost`.

1. Start Mock Server: `./scripts/start.sh`
2. In the {{API_CLIENT_REPO}} debug server settings UI, enter `http://10.0.2.2:8080`
3. Restart the app

### Method 2: adb reverse

```bash
adb reverse tcp:8080 tcp:8080
```

Then configure the app to connect to `http://localhost:8080`.

## Port Mapping Constraint

Apple Container's `-p 8080:8080` port mapping may not work via `localhost` in some environments. When this happens, `start.sh` displays the container's direct IP address (`http://192.168.64.x:8080`) as a fallback.

## Debug Build Restriction

Only debug builds allow HTTP (cleartext) communication. This is enforced by `network_security_config.xml` in the {{API_CLIENT_REPO}} project. Release/production builds will not connect to the mock server.

## Cache Clearing

After switching scenarios via Admin API, clear the app cache to see updated responses:

```bash
# Using the helper script
./clear_cache.sh android                # Cache clear only
./clear_cache.sh android --refresh-login # Cache clear + re-fetch login API (updates SharedPreferences)

# Using adb directly
adb shell am start -a android.intent.action.VIEW \
  -d "{{APP_DEBUG_DEEPLINK}}" {{APP_PACKAGE_ID}}

# With login refresh
adb shell am start -a android.intent.action.VIEW \
  -d "{{APP_DEBUG_DEEPLINK}}?refresh_login=true" {{APP_PACKAGE_ID}}
```
