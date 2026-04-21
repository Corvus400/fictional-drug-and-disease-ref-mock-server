# Client Connection (Android / iOS / Flutter)

Mock Server listens on HTTP `:8080` (started via `scripts/start.sh`, Apple Container).
This project is client-agnostic, so the specific client repo is not known; rules below cover Android, iOS, and Flutter.

Primary sources (verified):
- Android emulator `10.0.2.2` alias: https://developer.android.com/studio/run/emulator-networking-address
- `adb reverse` syntax: https://developer.android.com/develop/ui/views/layout/webapps/access-local-server
- Android cleartext policy: https://developer.android.com/training/articles/security-config
- iOS Simulator shares host network / iOS ATS: https://learn.microsoft.com/en-us/dotnet/maui/data-cloud/local-web-services and https://developer.apple.com/documentation/security/preventing_insecure_network_connections
- Flutter cleartext policy: https://docs.flutter.dev/release/breaking-changes/network-policy-ios-android

## URL cheat sheet

| Client                        | URL                                     | Extra setup                                     |
|-------------------------------|-----------------------------------------|-------------------------------------------------|
| Android emulator (AVD)        | `http://10.0.2.2:8080`                  | Android cleartext (below)                       |
| Android device via USB        | `http://localhost:8080` + `adb reverse` | Android cleartext                               |
| Android device on same LAN    | `http://<host-LAN-IP>:8080`             | Android cleartext for that domain               |
| iOS Simulator                 | `http://localhost:8080`                 | iOS ATS relaxation (below)                      |
| iOS device on same LAN        | `http://<host-LAN-IP>:8080`             | ATS relaxation + iOS 14+ local-network consent  |
| Flutter (Android target)      | Same as Android emulator row            | Debug-only config under `android/app/src/debug` |
| Flutter (iOS target)          | Same as iOS Simulator row               | Debug-only `Info-debug.plist` ATS block         |

`scripts/start.sh` prints these URLs on startup. If Apple Container's `-p 8080:8080` is not reachable via `localhost`, the script also prints the container's direct IP (`http://192.168.64.x:8080`) as fallback.

## Android emulator: `10.0.2.2`

The emulator sits behind a virtual router and cannot see the host network directly. `10.0.2.2` is a reserved alias for the host loopback (`127.0.0.1` on the dev machine). Apps in the emulator must target `http://10.0.2.2:8080` to reach the Mock Server.

## Android device / WebView: `adb reverse`

USB-connected devices (and WebView cases) can use reverse port forwarding:

```bash
adb reverse tcp:8080 tcp:8080
```

After this, the app reaches the host via `http://localhost:8080`. Works for the emulator too.

## iOS Simulator: host network

The iOS Simulator reuses the host macOS network stack, so `http://localhost:8080` (or `http://127.0.0.1:8080`) inside the Simulator reaches the Mock Server directly — no alias or port forwarding needed.

## Cleartext HTTP (debug builds only)

Android 9+ (API 28+) and iOS 9+ reject cleartext HTTP by default. Enable only in debug builds; never in release.

### Android (native and Flutter)

Reference a `network_security_config.xml` from `<application>` in `AndroidManifest.xml`.

Domain-scoped (preferred — covers emulator alias and `adb reverse`):

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
  <domain-config cleartextTrafficPermitted="true">
    <domain includeSubdomains="true">10.0.2.2</domain>
    <domain includeSubdomains="true">localhost</domain>
  </domain-config>
</network-security-config>
```

Whole-app (only if domain-scoped is impractical):

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
  <base-config cleartextTrafficPermitted="true" />
</network-security-config>
```

Flutter: place the manifest reference and the XML under `android/app/src/debug/...` so release builds are unaffected (Flutter docs, "Insecure HTTP connections are disabled by default").

### iOS (native and Flutter)

Add to `Info.plist` (native) or `Info-debug.plist` (Flutter debug flavor).

Minimal — local networking only (Apple-recommended for dev):

```xml
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSAllowsLocalNetworking</key>
    <true/>
</dict>
```

Broad — allow arbitrary loads (debug only):

```xml
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSAllowsArbitraryLoads</key>
    <true/>
</dict>
```

On iOS 14+ physical devices connecting to a host on the same LAN, the OS may additionally require `NSLocalNetworkUsageDescription` (and `NSBonjourServices` for mDNS discovery) plus a one-time user consent prompt.

## Cache clearing

After switching scenarios via the Admin API, the client may serve cached responses. Each client app is responsible for providing its own cache-clear mechanism (debug menu, deep link, or reinstall). Concrete deep-link schemes are out of scope here because they depend on the client app identity, which this project does not fix.
