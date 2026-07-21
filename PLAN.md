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
- ✅ **Cinematic Spotlight**: focus-driven hero, aurora gradient-mesh background, live clock + count, glass filter rail
- ✅ **Easy filtering** by genre and by language (each language chip shows its own script)
- ✅ **Live TV Guide** (per feedback — make it feel like a set-top box, not an app): left category column that switches instantly as you arrow through it, a live preview pane that auto-plays the focused channel, channel grid, OK → fullscreen. Verified category-switch + live preview + fullscreen on emulator.
- ✅ Subtle animations: pulsing LIVE dot + crossfade on the preview
- ✅ Region/language-aware "Featured for you" banner — leads the preview/focus with a channel in the viewer's device language (declared `locales_config`, read from live config). Verified end-to-end for English; mapping unit-tested.
- ✅ Bundled a TV-readable typeface — **Poppins** (Latin + Devanagari, Indian foundry); other Indic scripts fall back to Noto. Verified no tofu.
- ⬜ Real app icon + TV banner art (placeholders in place)

**Exit criteria:** it looks and feels like a flagship OTT app, not a demo. Verified on emulator: Cinematic Spotlight home, language/genre filtering, and playback all working.

## Phase 3 — Robustness 🚧
- ✅ Network loss + reconnect handling (5 backoff retries → "Reconnecting", then "off air" with OK-to-retry). Verified with a dead-URL stream: 6 attempts logged, OK triggers a fresh attempt, good URL recovers.
- ✅ ExoPlayer tuning (live-tuned DefaultLoadControl buffers for faster start/recovery)
- ✅ Remember last-watched / continue on relaunch (SharedPreferences → Home focuses the last channel; verified across a force-stop + relaunch)
- ✅ Basic tests (Channel parsing + playable flag) — `./gradlew testDebugUnitTest` green
- ⬜ Analytics-lite (local logging) — deferred, low priority

Note: couldn't sever the emulator's NAT (production image, no root) so live network-drop wasn't exercised end-to-end; the recovery path is the same `prepare()` that a dead→good URL swap confirmed.

**Phase 3 is effectively done** (only optional analytics remains).

## Phase 4 — Remote manifest (optional) ⬜
- ⬜ Host `channels.json` (any static host); fetch at startup, fall back to bundled
- ⬜ Lets us add/fix channels without shipping an app update
- ⬜ Simple schema versioning

## Phase 5 — Release to Amazon Appstore ⬜
- ✅ Release signing config (keystore-driven, keystore untracked) + `docs/RELEASE.md` guide
- ✅ Content-rights request drafted (`docs/PRASAR_BHARATI_REQUEST.md`) — ready to send
- ⬜ Real app icon + banner + screenshots + store copy
- ⬜ Remove all `test: true` channels (hidden from UI; strip from manifest before release)
- ⬜ Release signing config (`keystore`, not committed)
- ⬜ Amazon Developer account + app submission
- ⬜ Content-rights documentation ready for review (public-broadcaster provenance)
- ⬜ **Ideally: official Prasar Bharati feed/permission secured** (see CONTENT_SOURCES → "Getting the genuine feed") — de-risks review + stability

**Exit criteria:** approved + live on the Amazon Appstore for Fire TV.

---

## Parallel track — the genuine feed (fire-and-forget, NOT a launch gate)
**Decided 2026-07-21:** don't block launch on Prasar Bharati approval (slow govt process, may be ignored). Send the request (`docs/PRASAR_BHARATI_REQUEST.md`) as a low-effort parallel, launch without waiting, treat a reply as upside. Enforcement risk on free public-broadcaster content is low; the real work is **stream reliability** + a clean **store listing**. Keep the app **free** — monetising flips the risk. Full reasoning in `docs/CONTENT_SOURCES.md`.

### Refocused near-term priorities (in place of "wait for approval")
1. **Stream reliability** — dead-stream handling is in; add a hosted remote manifest (Phase 4) so broken URLs can be swapped without an app update.
2. **Store listing** — real icon + banner, screenshots, copy, signing (Phase 5).
3. **Send the request** — 10 min, then forget it.

## Open decisions
- App name: "Bharat One" working — confirm before Phase 5 branding.
- Regional channel breadth for v1: all ~28 DD regionals, or a curated subset first?
- Logos: bundle in-app vs host remotely.
