#!/usr/bin/env bash
#
# Submit a signed APK to the Amazon Appstore via the App Submission API.
# Requires env: AMAZON_CLIENT_ID, AMAZON_CLIENT_SECRET, AMAZON_APP_ID
# Usage: scripts/amazon_submit.sh <path-to-apk>
#
# Flow: get LWA token -> open (or create) an edit -> replace/upload the APK ->
#       validate -> commit (submits for Amazon review).
#
# Note: this drives the documented Submission API. The first live submission of
# the app must be done once in the console (content rating + availability). After
# that, this automates every version update.
set -euo pipefail

APK="${1:?usage: amazon_submit.sh <apk>}"
: "${AMAZON_CLIENT_ID:?}"; : "${AMAZON_CLIENT_SECRET:?}"; : "${AMAZON_APP_ID:?}"

API="https://developer.amazon.com/api/appstore/v1/applications/${AMAZON_APP_ID}"

echo "==> Requesting access token"
TOKEN=$(curl -sf -X POST https://api.amazon.com/auth/o2/token \
  -d grant_type=client_credentials \
  -d client_id="$AMAZON_CLIENT_ID" \
  -d client_secret="$AMAZON_CLIENT_SECRET" \
  -d scope=appstore::apps:readwrite | jq -r .access_token)
[ -n "$TOKEN" ] && [ "$TOKEN" != "null" ] || { echo "No token"; exit 1; }
AUTH=(-H "Authorization: Bearer $TOKEN")

# ETag of a resource (from response headers)
etag_of() { curl -s -D - -o /dev/null "${AUTH[@]}" "$1" | tr -d '\r' | awk -F': ' 'tolower($1)=="etag"{print $2}'; }

echo "==> Getting current edit (create one if none is open)"
EDIT=$(curl -s "${AUTH[@]}" "$API/edits" || true)
EDIT_ID=$(echo "$EDIT" | jq -r '.id // empty')
if [ -z "$EDIT_ID" ]; then
  EDIT=$(curl -sf "${AUTH[@]}" -X POST "$API/edits")
  EDIT_ID=$(echo "$EDIT" | jq -r '.id')
fi
echo "    editId=$EDIT_ID"

echo "==> Replacing / uploading the APK"
APKS=$(curl -s "${AUTH[@]}" "$API/edits/$EDIT_ID/apks")
APK_ID=$(echo "$APKS" | jq -r '.[0].id // empty')
if [ -n "$APK_ID" ]; then
  APK_ETAG=$(etag_of "$API/edits/$EDIT_ID/apks/$APK_ID")
  curl -sf "${AUTH[@]}" -H "If-Match: $APK_ETAG" \
    -H "Content-Type: application/vnd.android.package-archive" \
    -X PUT --data-binary @"$APK" \
    "$API/edits/$EDIT_ID/apks/$APK_ID/replace" >/dev/null
  echo "    replaced apk $APK_ID"
else
  curl -sf "${AUTH[@]}" -H "fileName: app-release.apk" \
    -H "Content-Type: application/vnd.android.package-archive" \
    -X POST --data-binary @"$APK" \
    "$API/edits/$EDIT_ID/apks/large/upload" >/dev/null
  echo "    uploaded new apk"
fi

# Amazon requires non-empty "recent changes" on every listing before an edit can
# be submitted. RELEASE_NOTES is set by the deploy workflow (the commit subject).
echo "==> Setting release notes on each listing"
NOTES="${RELEASE_NOTES:-Bug fixes and improvements.}"
for LANG in $(curl -sf "${AUTH[@]}" "$API/edits/$EDIT_ID/listings" | jq -r '.listings | keys[]'); do
  L_ETAG=$(etag_of "$API/edits/$EDIT_ID/listings/$LANG")
  curl -sf "${AUTH[@]}" "$API/edits/$EDIT_ID/listings/$LANG" \
    | jq --arg rc "$NOTES" '.recentChanges = $rc' \
    | curl -sf "${AUTH[@]}" -H "If-Match: $L_ETAG" -H "Content-Type: application/json" \
        -X PUT --data @- "$API/edits/$EDIT_ID/listings/$LANG" >/dev/null
  echo "    release notes set for $LANG"
done

echo "==> Validating edit"
curl -sf "${AUTH[@]}" -X POST "$API/edits/$EDIT_ID/validate" >/dev/null

echo "==> Committing edit (submitting for review)"
EDIT_ETAG=$(etag_of "$API/edits/$EDIT_ID")
curl -sf "${AUTH[@]}" -H "If-Match: $EDIT_ETAG" -X POST "$API/edits/$EDIT_ID/commit" >/dev/null

echo "==> Submitted to Amazon Appstore for review."
