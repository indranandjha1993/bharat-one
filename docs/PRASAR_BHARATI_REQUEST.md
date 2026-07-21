# Prasar Bharati — content-access request

Goal: get **official permission + stream access** for the Doordarshan / Sansad TV / Gyan Darshan
channels Bharat One carries, so the app ships on the Amazon Appstore with documented rights
instead of the unofficial community feeds it uses today.

This has **lead time** (it's a form-and-wait process), so send it early. Everything below is a
draft — review and adjust the commercial details before sending.

---

## Who to contact
Prasar Bharati has no public developer API, so this goes through their content/partnerships route.
Try these, in order:

1. **Prasar Bharati (HQ)** — via the contact form / addresses on <https://prasarbharati.gov.in> (Contact Us). Address it to the **Directorate General: Doordarshan** and cc the **WAVES / New Media** team.
2. **WAVES OTT content team** — Prasar Bharati's OTT platform runs a content-onboarding framework; a redistribution/carriage ask fits there. See <https://prasarbharati.gov.in/waves/>.
3. **Doordarshan Commercial / DD Direct+ (Free Dish)** — they handle carriage of DD channels.
4. **Sansad TV** — separately, a courtesy note to <https://sansadtv.nic.in> (the NIC webcast is already public, but confirm redistribution is fine).

Keep every reply and any written grant in this `docs/` folder for the Amazon review packet.

---

## Email draft

> **Subject:** Request for permission to carry Doordarshan & Sansad TV live channels in a free Fire TV app (Bharat One)
>
> Respected Sir/Madam,
>
> I am building **Bharat One**, a **free, ad-free** app for Amazon Fire TV / Fire Stick that gives
> viewers in India and abroad a single, elegant place to watch India's **public-broadcaster** live
> channels — Doordarshan (DD News, DD India, and the regional DD network), Sansad TV, and Gyan
> Darshan. The app carries **only public-broadcaster and government content**; it does not host or
> re-stream any private channel.
>
> The goal is to widen the reach of Doordarshan's public-service programming on the large-screen
> platforms people increasingly use, in a modern interface with browsing by language and genre. The
> app plays each channel's official stream directly, keeping the broadcaster's on-screen branding
> intact.
>
> I am writing to request:
>
> 1. **Written permission** to carry Prasar Bharati's live channels (Doordarshan national + regional,
>    and Gyan Darshan) in the app; and
> 2. The **official HLS stream endpoints** (or an approved feed/API) for those channels, so the app
>    uses a stable, sanctioned source rather than third-party mirrors.
>
> I am happy to comply with any conditions — attribution, non-alteration of the broadcast,
> geo-restrictions, reporting, a formal agreement, or a nominal carriage arrangement. I can share a
> demo build, the channel list, and the app's technical details on request.
>
> Please let me know the right process and contact for this. Thank you for your time and for the
> public service Prasar Bharati provides.
>
> Warm regards,
> **Indra Nand Jha**
> Bharat One · <email> · <phone>

---

## Attachments to include
- **One-page app brief**: what it is, the full channel list (see `docs/CONTENT_SOURCES.md`),
  screenshots of the guide + player, target platform (Amazon Fire TV Appstore), audience.
- **Provenance note**: the app carries public-broadcaster/government channels only; no private or
  YouTube content.

## Honest points to settle before sending
- **Monetisation**: the draft says "free, ad-free". If you intend ads/subscription later, say so
  now — it changes the ask from "permission" to "carriage/commercial agreement".
- **Today's streams**: the app currently uses reachable-but-unofficial CloudFront HLS feeds
  (`verified:false` in `channels.json`). Don't claim official access you don't have; this request is
  what makes it official.
- **Sansad TV**: the NIC webcast (`playhls.media.nic.in`) is already public government content —
  lowest-risk of all; the note to Sansad TV is courtesy/confirmation.

## After a grant
- Update `channels.json`: set `source` to the official value and `verified: true` for granted feeds.
- File the permission letter here and reference it in the Amazon Appstore submission.
