# Bharat One — Build Plan

Phased roadmap. Keep it honest: mark things done only when they actually work on a device/emulator.

Legend: ✅ done · 🚧 in progress · ⬜ not started

---

## Phase 0 — Scaffold & context 🚧
Goal: a real, openable Android TV project + organized context so future work is fast.
- ✅ Project directory + git init
- ✅ `CLAUDE.md` (context source of truth)
- ✅ `docs/CONTENT_SOURCES.md` (rights analysis + channel manifest)
- ✅ Gradle setup (version catalog, module config)
- ✅ AndroidManifest (LEANBACK_LAUNCHER, banner, INTERNET)
- ✅ Data layer: `Channel` model + `ChannelRepository` (loads `assets/channels.json`)
- ✅ Minimal UI: HomeScreen (channel grid) + PlayerScreen (ExoPlayer HLS)
- ✅ `channels.json` seed (DD/Sansad entries + one clearly-marked TEST stream)
- ⬜ **Open in Android Studio → Gradle sync → run in TV emulator** (user does this; generates gradle wrapper)
- ⬜ Confirm the TEST stream plays end-to-end on emulator

**Exit criteria:** app launches on a TV emulator, shows the channel grid, and the TEST channel plays.

## Phase 1 — Real content wired up 🚧
- ✅ Verify which DD/Sansad HLS URLs actually resolve today; fill `channels.json` (13 CloudFront feeds confirmed HTTP 200, `verified:false` since not officially granted)
- ✅ Group channels (National · News · Regional · Sansad) into rows on Home
- ✅ Handle dead streams gracefully (error overlay instead of black screen)
- ⬜ Retry action on the error overlay (currently Back-only)
- ⬜ Channel logos/thumbnails — folded into Phase 2 (branded monogram cards)
- ⬜ Confirm each regional stream plays (spot-checked DD News; rest resolve but unplayed)

**Exit criteria:** a real lineup of DD + Sansad channels browsable and playable. ✅ DD News verified playing on the emulator.

## Phase 2 — World-class UX pass 🚧
This is the "wow" the whole project is about.
- ✅ Design system: warm-ink base, saffron-gold + broadcast-red accents, focus scale + glow (`ui/theme/Brand.kt`)
- ✅ Signature: broadcast tiles, each wearing its language's own script (हिं / বাং / த …), bilingual row labels, tricolor rule, live count
- ✅ Smooth focus (scale 1.06 + saffron border + glow); fixed header with rows scrolling under it
- ✅ Custom player overlay: LIVE bug + channel name, "Tuning in…" / "off air" states
- ✅ D-pad channel up/down inside the player (surf without leaving playback)
- ⬜ Hero/spotlight band (optional — header + tiles already carry the identity)
- ⬜ Bundle a display typeface (Roboto for now; must be a bundled font — no Google Fonts, since Fire TV lacks Play Services)
- ⬜ Real app icon + TV banner art (placeholders in place)

**Exit criteria:** it looks and feels like a flagship OTT app, not a demo. Verified on emulator: home + player both look premium.

## Phase 3 — Robustness ⬜
- ⬜ Remember last-watched / continue on relaunch
- ⬜ Network loss + reconnect handling
- ⬜ ExoPlayer tuning (buffer sizes, adaptive HLS, low-latency where offered)
- ⬜ Analytics-lite (local logging) to see which channels are used
- ⬜ Basic tests (repository parse, viewmodel state)

## Phase 4 — Remote manifest (optional) ⬜
- ⬜ Host `channels.json` (any static host); fetch at startup, fall back to bundled
- ⬜ Lets us add/fix channels without shipping an app update
- ⬜ Simple schema versioning

## Phase 5 — Release to Amazon Appstore ⬜
- ⬜ Real app icon + banner + screenshots + store copy
- ⬜ Remove all `test: true` channels
- ⬜ Release signing config (`keystore`, not committed)
- ⬜ Amazon Developer account + app submission
- ⬜ Content-rights documentation ready for review (public-broadcaster provenance)
- ⬜ **Ideally: official Prasar Bharati feed/permission secured** (see CONTENT_SOURCES → "Getting the genuine feed") — de-risks review + stability

**Exit criteria:** approved + live on the Amazon Appstore for Fire TV.

---

## Parallel track — the genuine feed (do early, it has lead time)
Requesting official access from Prasar Bharati is a form-and-wait process. Start it during Phase 1 so it's ready by Phase 5. Draft/notes in `docs/CONTENT_SOURCES.md`.

## Open decisions
- App name: "Bharat One" working — confirm before Phase 5 branding.
- Regional channel breadth for v1: all ~28 DD regionals, or a curated subset first?
- Logos: bundle in-app vs host remotely.
