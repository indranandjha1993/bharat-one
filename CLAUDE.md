# Bharat One — Fire TV app

> Free, elegant live TV for India's **public broadcaster** channels (Doordarshan + Sansad TV), built for Amazon Fire TV / Fire Stick.
> Working brand: **Bharat One** ("all of Bharat's free live TV, in one place"). Easy to rename — see [Renaming](#renaming-the-app).

This file is the **single source of truth for context**. Read it first every session. It exists so we never re-derive the same decisions.

---

## 1. What this is (and what it is deliberately NOT)

**Is:** A native Fire TV app that plays **live HLS streams of India's public broadcaster channels** in a custom, world-class 10-foot UI. Content = Doordarshan (DD News, DD India, ~28 regional DD channels), Sansad TV, and AIR audio. All of it is public-broadcaster / government content with a legitimate direct-stream path.

**Is NOT:**
- ❌ NOT a YouTube aggregator. We do **not** embed YouTube, use the IFrame player, or scrape yt-dlp stream URLs. (YouTube ToS forbids the app shape we want, and Amazon rejects aggregators.)
- ❌ NOT a re-streamer of **private** channels (Aaj Tak, Republic, NDTV, Zee, etc.). Those have **zero** legitimate direct-stream path without a signed license. They are out of scope by design.
- ❌ NOT an IPTV playlist app. We curate a known, documented channel manifest, not arbitrary m3u8 lists.

**Why this shape:** it's the only version that (a) needs no licensing beyond a public-broadcaster content request, (b) can pass Amazon Appstore review, and (c) lets us own a fully custom player UI. See `docs/CONTENT_SOURCES.md` for the full rights analysis and the manifest.

---

## 2. Tech stack (locked)

| Layer | Choice | Why |
|---|---|---|
| Language | **Kotlin** | Native Android; best perf on low-powered Fire Stick |
| UI | **Jetpack Compose for TV** (`androidx.tv:tv-material`) | Modern declarative UI, TV focus/D-pad handling, full custom design |
| Video | **Media3 / ExoPlayer** (`media3-exoplayer` + `media3-exoplayer-hls`) | Native HLS (.m3u8) playback, our own player chrome |
| Data | Local **`channels.json`** in `assets/`, parsed with **kotlinx.serialization** | Simple, maintainable manifest; no backend for v1 |
| Build | Gradle (Kotlin DSL) + version catalog (`gradle/libs.versions.toml`) | Standard modern Android |

**Fire TV = Android TV.** Everything here is standard Android TV development; Fire OS is a fork of Android. The only Fire-specific bits are: the `LEANBACK_LAUNCHER` intent filter, the TV **banner** image, and distribution via the **Amazon Appstore** (not Google Play).

**No backend in v1.** The channel manifest ships inside the app. If/when we need remote updates, add a hosted `channels.json` fetched at startup (see PLAN.md, Phase 4).

---

## 3. Project layout

```
bharat-one/
├─ CLAUDE.md                  ← you are here (context source of truth)
├─ PLAN.md                    ← phased roadmap + status
├─ README.md                  ← human-facing quickstart
├─ docs/
│  └─ CONTENT_SOURCES.md      ← rights analysis + full channel manifest + how streams work
├─ gradle/
│  ├─ libs.versions.toml      ← dependency versions (single place to bump)
│  └─ wrapper/…
├─ settings.gradle.kts, build.gradle.kts, gradle.properties
└─ app/
   ├─ build.gradle.kts        ← module config (SDK levels, deps, applicationId)
   └─ src/main/
      ├─ AndroidManifest.xml   ← LEANBACK_LAUNCHER, banner, INTERNET permission
      ├─ assets/channels.json  ← THE channel manifest (source of truth for content)
      ├─ res/                  ← strings, theme, launcher icon, TV banner
      └─ java/com/bharatone/tv/
         ├─ MainActivity.kt         ← entry; hosts Home ⇄ Player navigation
         ├─ data/
         │  ├─ Channel.kt           ← data model (@Serializable)
         │  └─ ChannelRepository.kt  ← loads + parses channels.json from assets
         └─ ui/
            ├─ theme/               ← Compose-for-TV dark theme + type
            ├─ home/HomeScreen.kt   ← channel grid (D-pad focusable cards)
            └─ player/PlayerScreen.kt ← ExoPlayer HLS full-screen player
```

Package / applicationId: **`com.bharatone.tv`**.

---

## 4. How to build & run (first-timer friendly)

> **You have never built for Fire TV before — this section is written for that.** These are the exact steps.

### One-time setup
1. Install **Android Studio** (latest stable). On first open it installs the Android SDK.
2. `File → Open` → select this `bharat-one/` folder. Android Studio will **generate the Gradle wrapper** and sync. (This is why there's no `gradlew` binary committed yet — AS creates it. Alternatively run `gradle wrapper` if you have Gradle installed.)
3. Let the Gradle sync finish. If it flags a newer library version, accept — see [versions note](#versions).

### Run on the desktop first (fastest loop)
- Create a **Television emulator**: `Device Manager → Add Device → TV → Android TV (1080p)`, API 30+.
- Press **Run ▶**. The app launches in the TV emulator; use the on-screen D-pad or arrow keys to navigate.

### Run on a real Fire TV Stick
1. On the Fire Stick: `Settings → My Fire TV → Developer Options → ADB Debugging = ON` and `Apps from Unknown Sources = ON`. (If Developer Options is hidden, click the device Serial Number 7×.)
2. Note the Fire TV's IP: `Settings → My Fire TV → About → Network`.
3. From your Mac:
   ```bash
   adb connect <fire-tv-ip>:5555
   ./gradlew installDebug        # builds + sideloads
   ```
   The app appears under **Your Apps & Channels** on the Fire TV home. Launch with the remote.

### Common gradle commands
```bash
./gradlew assembleDebug     # build debug APK
./gradlew installDebug      # build + install to connected device/emulator
./gradlew installRelease    # release build (needs signing config — see PLAN Phase 5)
./gradlew lint              # static checks
```
APK output: `app/build/outputs/apk/debug/app-debug.apk`.

---

## 5. Content rules (READ before touching channels — this is what keeps us legal + in the Appstore)

1. **Only public-broadcaster / government sources.** Every channel in `channels.json` must be Doordarshan (Prasar Bharati), Sansad TV (NIC), or AIR. Never add a private channel's stream.
2. **Never add YouTube.** No `youtube.com`, no IFrame, no yt-dlp URLs. Not even "temporarily."
3. **Stream URLs must be documented.** Every entry carries `source` and `verified` fields. Unverified/community URLs are allowed for **dev only** and must stay `verified: false`. Before release, the goal is an **official Prasar Bharati feed** (see `docs/CONTENT_SOURCES.md` → "Getting the genuine feed").
4. **Any entry flagged `test: true` must be removed before a release build.** (The test HLS stream exists only so the player demonstrably works on first launch.)
5. When in doubt about a source's legitimacy, it does **not** go in. The whole value of this project is being the clean, review-safe one.

---

## 6. Fire TV / Android TV gotchas (so we don't relearn them)

- **Focus is everything.** There is no touch — every interactive element must be `focusable` and reachable by D-pad (up/down/left/right + center=select). Compose-for-TV `Card`/`Surface` handle focus visuals; test the full nav path.
- **10-foot UI:** large type (min ~body 18–24sp), high contrast, generous spacing, safe-area margins (~5% inset — TVs overscan). Assume the user is 3 metres away with a remote.
- **Banner required:** TV apps need a `320×180` banner (`android:banner`) or they won't show on the Fire TV home row. Placeholder is in `res/drawable/`; replace with real art (PLAN Phase 5).
- **Leanback launcher:** the launcher activity needs `<category android:name="android.intent.category.LEANBACK_LAUNCHER" />` or Fire TV won't list it.
- **Low-end hardware:** entry Fire Sticks are weak. Keep the home screen light (lazy lists, no heavy blur/animation storms), and let ExoPlayer do adaptive HLS. Avoid loading giant images.
- **Back button** should exit the player back to Home, and from Home exit the app — handle `onBackPressed` deliberately.
- **Keep the screen on** during playback (`FLAG_KEEP_SCREEN_ON` / `keepScreenOn`) or the player dims.

<a name="versions"></a>
## 7. Versions
Dependency versions live **only** in `gradle/libs.versions.toml`. They are pinned to **known-good** releases; if Android Studio offers upgrades, bump there and re-sync. If a build fails right after opening, it's almost always a version/AGP mismatch — align AGP with your installed Android Studio, then sync.

<a name="renaming-the-app"></a>
## 8. Renaming the app
Working name "Bharat One" touches only:
- `app/src/main/res/values/strings.xml` → `app_name`
- `applicationId` in `app/build.gradle.kts` (only if you also want a new package id)
- This file's title + `README.md`
The Kotlin package (`com.bharatone.tv`) can stay even if the brand name changes.

---

## 9. Working agreements (from the user's global prefs)
- **Keep it simple. Don't overengineer.** Single-module app, no premature clean-architecture layering, no backend until a phase actually needs it.
- **No `Co-Authored-By` in commits.** Commit/push only when explicitly asked.
- Prefer clear, boring, idiomatic Android over clever abstractions.

## 10. Status
See **PLAN.md** for the phased roadmap and what's done vs pending. Current: **Phase 0 — scaffold + context (in progress).**
