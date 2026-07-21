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

## Phase 1 — Real content wired up ⬜
- ⬜ Verify which DD/Sansad HLS URLs actually resolve today; fill `channels.json` (`verified` flags honest)
- ⬜ Group channels (National · News · Regional · Sansad) into rows on Home
- ⬜ Channel logos/thumbnails (bundled or hosted); graceful placeholder
- ⬜ Handle dead streams gracefully (error state + retry, don't crash)
- ⬜ Sansad TV via the NIC public webcast as the guaranteed-clean channel

**Exit criteria:** a real lineup of DD + Sansad channels browsable and playable.

## Phase 2 — World-class UX pass ⬜
This is the "wow" the whole project is about.
- ⬜ Design system: color, type scale, spacing, focus treatment (see `frontend-design` guidance)
- ⬜ Hero/spotlight area on Home (featured channel, live now)
- ⬜ Smooth focus animations (scale/elevation) that stay light on Fire Stick hardware
- ⬜ Custom player overlay: channel name, now-playing, controls, D-pad channel up/down
- ⬜ Splash + app icon + real TV banner (320×180)
- ⬜ Loading/buffering states that feel premium, not janky

**Exit criteria:** it looks and feels like a flagship OTT app, not a demo.

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
