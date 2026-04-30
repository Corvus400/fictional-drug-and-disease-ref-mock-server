---
paths:
  - "src/main/kotlin/**/routes/**/*.kt"
  - "src/main/kotlin/**/plugins/Routing.kt"
---

# Endpoint Implementation Rules

Rules for route/endpoint creation, derived from failure patterns D1-D7.

1. **Route conflict check**: Before adding a new endpoint, run `grep -r "path-pattern" src/` to verify no existing route handles the same path. Query-parameter-based dispatch must be done inside the route handler, not via separate routes.
2. **Full endpoint coverage**: Before implementing a screen's endpoints, list ALL API calls the screen makes (use Charles/Proxyman network trace or inspect the ViewModel/UseCase call chain). A screen with a working main GET but missing sub-endpoints (action APIs, WebView content) will break on user interaction.
3. **Image route coverage**: When fixtures reference new image path patterns, verify that an image route module such as `DosageFormImageModule` has a matching route. Missing coverage → 404 → broken images.
4. **Legacy API encoding**: JSP/Servlet-based backend APIs may use Shift_JIS encoding with double URL-encoding. Verify the actual request/response encoding via Charles/backend source before implementing. Use Ktor's raw query string for Shift_JIS parameter extraction.
5. **Complex nested APIs**: For APIs with deeply nested structures (categories with subcategories with product links), first obtain the full real API response JSON, then build the Fixture field-by-field. Do not guess the structure.
6. **Module responsibility**: Place routes in the Module that matches the domain responsibility. When unsure, check the existing Module's KDoc and confirm with the user before implementing.
