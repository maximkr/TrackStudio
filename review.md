# Code Review: `ui-update` vs `main`

Date: 2026-03-05
Scope: diff from merge-base `c7e840305791774a7a61806588dee045686f2f86` to `HEAD`
Verification: `./gradlew compileJava` passed
Status: reviewed findings below are fixed

## Resolved Findings

### 1. Fixed: `?url=` validation in app shell was bypassable

Files:

- [src/main/webapp/app-shell.js](/home/maximkr/TrackStudio/src/main/webapp/app-shell.js)

Problem:

- protocol-relative URLs like `//host/...` could pass
- non-HTTP schemes like `javascript:` could pass
- same-origin validation only covered `scheme://...` inputs

Fix:

- removed the permissive regex-based check
- switched validation to `new URL(url, window.location.href)`
- now only `http:` and `https:` are allowed
- navigation is limited to the current origin
- navigation is additionally limited to the current application context path
- double-decoding of `url` was removed

Result:

- shell navigation via `?url=` now accepts only internal app URLs

### 2. Fixed: direct-open redirect dropped `location.hash`

Files:

- [src/main/webapp/jsp/layout/ListLayout.jsp](/home/maximkr/TrackStudio/src/main/webapp/jsp/layout/ListLayout.jsp)

Problem:

- when an inner page redirected itself into `app-shell.html`, only `pathname + search` was preserved
- deep links to anchors, message IDs, and other in-page targets were lost

Fix:

- redirect URL now preserves `self.location.hash` together with `pathname` and `search`

Result:

- direct links such as `/task/124?thisframe=true#456` survive the shell redirect correctly

### 3. Fixed: `url` parameter was decoded twice

Files:

- [src/main/webapp/app-shell.js](/home/maximkr/TrackStudio/src/main/webapp/app-shell.js)

Problem:

- `URLSearchParams.get('url')` already returns a decoded value
- extra `decodeURIComponent()` could corrupt valid encoded URLs or throw on malformed `%` input

Fix:

- removed the unconditional extra decode
- validation and iframe navigation now use the `URLSearchParams` value directly

Result:

- shell bootstrap is stable for encoded internal URLs and malformed double-decoding cases are eliminated

## Closed Question

### 4. WebDAV forwarding

File:

- [src/main/java/com/trackstudio/action/TaskLinkServlet.java](/home/maximkr/TrackStudio/src/main/java/com/trackstudio/action/TaskLinkServlet.java)

Decision:

- WebDAV support is not needed
- no action required for the removed `/task/webdav/*` forwarding path
