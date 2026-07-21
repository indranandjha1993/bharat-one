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

## Notes
- Fire OS is Android-based; the same APK targets Fire TV. Test on a real Fire Stick over ADB before submitting (see `CLAUDE.md`).
- The **Prasar Bharati official feed** (`docs/PRASAR_BHARATI_REQUEST.md`) is the thing that most de-risks review and long-term stability — start that request before submitting.
