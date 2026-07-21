# Content Sources — rights analysis & channel manifest

This is the "why we can legally do this" document. If anyone ever asks whether Bharat One is allowed to stream what it streams, the answer is here.

---

## The one rule

**Only India's public broadcaster and government content.** That means **Prasar Bharati** (Doordarshan TV + AIR radio) and **Sansad TV** (Parliament, run via NIC). Nothing else has a legitimate, license-free, direct-stream path.

Private channels (Aaj Tak, Republic, NDTV, Zee, Times Now, TV9, etc.) are **excluded on purpose** — the only legitimate ways to carry them are (a) their own YouTube embed on YouTube's terms, or (b) a signed carriage license. We do neither. This exclusion is the feature: it's what makes us clean and Amazon-Appstore-approvable.

## Why not YouTube / IPTV?
- **YouTube:** Playing YouTube outside their IFrame player violates ToS; the IFrame player forbids the custom UI we want and Amazon rejects aggregators. So: no YouTube in any form.
- **IPTV m3u8 lists:** community lists (e.g. iptv-org) constantly break and carry no rights. Fine to *reference* for discovering an endpoint during dev, never to depend on.

---

## How the streams actually work

Prasar Bharati's official Live TV portal (`prasarbharati.gov.in/live-tv/`) now routes every DD channel through **WAVES** (`wavespb.com`), their official OTT. Under the hood these are **HLS (.m3u8)** streams — the exact format Media3/ExoPlayer plays natively. **There is no public developer API** and no published redistribution program on WAVES (checked). So there are three real ways to source a playable URL, in order of how legitimate/stable they are:

1. **Official Prasar Bharati content request (best).** Public broadcaster with a dissemination mandate; they grant feed/content access on request (email/apply; WAVES also runs a content framework). Gives a permissioned, stable HLS feed. Downside: form-and-wait. **→ Start this early (PLAN, parallel track).**
2. **Government NIC webcast (cleanest, use now).** `webcast.gov.in/lstvlive/` is a public government webcast of Sansad TV. Genuinely public — safe to point the player at today. Limited to parliamentary content.
3. **Publicly-reachable DD HLS endpoints (dev only, unstable).** DD channels have HLS endpoints on Prasar Bharati's CDN that community projects track. Reachable but **not licensed to us and they break often** (open iptv-org issue literally titled "Broken: Free to air DD channels of India"). Use only with `verified: false` for local dev; never the basis for a shipped app's reliability.

### Getting the genuine feed (Phase-1 action)
- Prasar Bharati: contact via `prasarbharati.gov.in` (DG:Doordarshan / WAVES content team). Ask for: permission + official HLS endpoints for DD News, DD India, and the regional bouquet for redistribution in a free Fire TV app.
- Sansad TV: NIC webcast is already public; still worth a courtesy note.
- Keep all correspondence + any grant in `docs/` for the Amazon review packet.

---

## Channel manifest

The live source of truth for the app is `app/src/main/assets/channels.json`. This table is the human reference for what should be in it.

### National / News (Doordarshan)
| Channel | Language | Notes |
|---|---|---|
| DD News | Hindi | 24/7 national news — flagship |
| DD India | English | International-facing English news |
| DD National | Hindi | GEC + news bulletins |
| DD Bharati | Hindi | Culture/arts |
| DD Kisan | Hindi | Agriculture news |
| DD Sports | Hindi/Eng | Live sports |
| DD Urdu | Urdu | Urdu news/GEC |

### Government webcast
| Channel | Source | Notes |
|---|---|---|
| Sansad TV | NIC `webcast.gov.in` | Lok Sabha + Rajya Sabha; **cleanest source of all** |

### Regional Doordarshan (each carries its own state news bulletins) — ~28 channels
DD Arunprabha · DD Assam · DD Bangla · DD Bihar · DD Chandana (Kannada) · DD Chhattisgarh · DD Girnar (Gujarati) · DD Goa · DD Haryana · DD Himachal · DD Jharkhand · DD Kashir · DD Madhya Pradesh · DD Malayalam · DD Manipur · DD Meghalaya · DD Mizoram · DD Nagaland · DD Odia · DD Podhigai (Tamil) · DD Punjabi · DD Rajasthan · DD Sahyadri (Marathi) · DD Saptagiri (Telugu) · DD Tripura · DD Uttar Pradesh · DD Uttarakhand · DD Yadagiri

### Audio (later)
AIR / Akashvani — 230+ live radio channels via the official NewsOnAir platform. Same rights basis. Candidate for a v2 "Radio" tab.

---

## `channels.json` schema
```jsonc
{
  "id":        "dd-news",              // stable slug
  "name":      "DD News",              // display name
  "language":  "Hindi",
  "category":  "News",                 // National | News | Regional | Sansad | Test
  "streamUrl": "https://…/master.m3u8",// HLS endpoint
  "logoUrl":   "",                     // optional; empty → placeholder
  "source":    "prasar-bharati|nic|community|test",
  "verified":  false,                  // true ONLY for officially-granted/confirmed-stable URLs
  "test":      false                   // true → dev-only, MUST be removed before release
}
```

## Sources (verified during research)
- Prasar Bharati Live TV — https://prasarbharati.gov.in/live-tv/
- WAVES (Prasar Bharati OTT) — https://prasarbharati.gov.in/waves/
- DD News (Prasar Bharati) — https://prasarbharati.gov.in/dd-news/
- Sansad TV Live — https://sansadtv.nic.in/live-tv
- NIC public webcast — https://webcast.gov.in/lstvlive/
- NewsOnAir (AIR/DD official) — https://play.google.com/store/apps/details?id=com.parsarbharti.airnews
- iptv-org "Broken: Free to air DD channels" issue — https://github.com/iptv-org/iptv/issues/14544
