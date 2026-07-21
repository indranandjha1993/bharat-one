# Bharat One

Free, elegant live TV for India's public broadcaster channels (Doordarshan + Sansad TV), built natively for Amazon Fire TV / Fire Stick.

- **Stack:** Kotlin · Jetpack Compose for TV · Media3/ExoPlayer (HLS)
- **Content:** public broadcaster only — no YouTube, no private channels, no scraping. See [`docs/CONTENT_SOURCES.md`](docs/CONTENT_SOURCES.md).
- **Context & decisions:** [`CLAUDE.md`](CLAUDE.md)
- **Roadmap:** [`PLAN.md`](PLAN.md)

## Run it

1. Open this folder in **Android Studio** (it generates the Gradle wrapper and syncs).
2. Create a **Television emulator** (Android TV 1080p, API 30+) or connect a Fire TV Stick over ADB.
3. Press **Run**. Navigate with the D-pad / arrow keys.

The **Test Pattern** channel plays a public HLS stream so you can confirm playback on first launch. Real DD/Sansad streams get wired up in Phase 1.

Full build/run details, including sideloading to a real Fire Stick, are in [`CLAUDE.md`](CLAUDE.md).
