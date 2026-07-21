# Releasing Bharat One to the Amazon Appstore

## 1. Create a release keystore (one time)
```bash
keytool -genkey -v -keystore release.jks -alias bharatone \
  -keyalg RSA -keysize 2048 -validity 10000
```
Keep `release.jks` safe and **out of git** (it's gitignored). Losing it means you can't update the app.

Then copy the template and fill it in:
```bash
cp keystore.properties.template keystore.properties   # then edit the passwords
```
`keystore.properties` is untracked. With it present, `./gradlew assembleRelease` produces a signed APK.

## 2. Build the release APK
```bash
./gradlew assembleRelease
# -> app/build/outputs/apk/release/app-release.apk
```

## 3. Pre-submission checklist
- [ ] **Remove the test channel** — delete the `test-pattern` entry from `app/src/main/assets/channels.json` (it's already hidden from the UI, but strip it for release).
- [ ] Bump `versionCode` / `versionName` in `app/build.gradle.kts`.
- [ ] Real **app icon** and **TV banner** (320×180) — replace the vector placeholders in `res/`.
- [ ] Store listing: name, description, screenshots (guide + player), category = "Movies & TV" / "News".
- [ ] **Rights documentation** ready — see `docs/PRASAR_BHARATI_REQUEST.md`. Have the public-broadcaster provenance (and any Prasar Bharati permission) on hand; Amazon asks about content rights.
- [ ] Confirm every shipped stream is HTTPS and resolves.

## 4. Submit
- Amazon Developer account → **Appstore** → add a new app → upload the signed APK.
- Fire TV requires the **Leanback launcher** intent + a **banner** (both already in the manifest).
- Amazon reviews content rights: lead with "public-broadcaster / government channels only, no private or YouTube content."

## CI/CD (GitHub Actions)
Two workflows under `.github/workflows/`:

- **`ci.yml`** — on every push / PR to `main`: runs unit tests + lint + builds the debug APK, and uploads the APK and lint report as artifacts. This is the gate for merging.
- **`release.yml`** — on a `v*` tag (or manual dispatch): builds a **signed** release APK and publishes a GitHub Release with it.

### Secrets to add (repo → Settings → Secrets → Actions)
For `release.yml` to sign the build:
| Secret | What it is |
|---|---|
| `KEYSTORE_BASE64` | `base64 -i release.jks` (the whole keystore, base64-encoded) |
| `STORE_PASSWORD` | keystore password |
| `KEY_ALIAS` | `bharatone` (or your alias) |
| `KEY_PASSWORD` | key password |

Cut a release: `git tag v0.1.0 && git push origin v0.1.0`.

### Continuous deployment to the Amazon Appstore
`deploy.yml` runs **only after the CI workflow succeeds** on `main` (via `workflow_run`),
then auto-submits a new build for review. CI skips docs-only pushes, so those never
deploy. It builds a signed APK with an auto-incrementing versionCode
(from the commit count) and runs `scripts/amazon_submit.sh`, which drives Amazon's
**App Submission API**: get token → open/create an edit → replace the APK →
validate → commit (submit for review).

**Important truths:**
- "Deploy" = *submit for review*. Amazon reviews every version; it goes live after
  approval (same-day to ~2 days). No store allows code live without review.
- The **first submission is manual** (content rating + availability + submit in the
  console). The API automates version *updates* after the app is live.
- Every push triggers a review submission. To make it less chatty, change the
  trigger in `deploy.yml` to tags (`on: push: tags: ['v*']`).

**One-time setup:**
1. **Create a Login with Amazon (LWA) security profile:** developer.amazon.com →
   Login with Amazon → Create a Security Profile. Note the **Client ID** and
   **Client Secret**. (This is what grants API access to the Appstore.)
2. Add these repo secrets (in addition to the signing ones above):
   | Secret | Value |
   |---|---|
   | `AMAZON_CLIENT_ID` | LWA security profile client id |
   | `AMAZON_CLIENT_SECRET` | LWA security profile client secret |
   | `AMAZON_APP_ID` | the app id from the console URL (`amzn1.devportal.mobileapp.…`) |
3. Do the first submission by hand, then future pushes auto-submit updates.

The submission script is based on the documented Submission API; watch the first
CI run's logs and adjust endpoints if Amazon has changed anything.

## Notes
- Fire OS is Android-based; the same APK targets Fire TV. Test on a real Fire Stick over ADB before submitting (see `CLAUDE.md`).
- The **Prasar Bharati official feed** (`docs/PRASAR_BHARATI_REQUEST.md`) is the thing that most de-risks review and long-term stability — but it's fire-and-forget, not a launch gate.
