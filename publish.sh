#!/usr/bin/env bash
#
# Uploads the current build to JetBrains Marketplace.
#
# The FIRST release of a plugin must be uploaded in the web UI
# (https://plugins.jetbrains.com/author/me -> "Add new plugin"). This script is
# for all following versions.
#
# Token: https://plugins.jetbrains.com/author/me/tokens ("Generate Token"),
# then either export JETBRAINS_MARKETPLACE_TOKEN=perm:... or put it in
# ~/.jetbrains-marketplace-token.
#
# Usage:
#   ./publish.sh                 # build, then upload to the Stable channel
#   ./publish.sh --build-only    # build the zip only
#   ./publish.sh --channel beta  # upload to the "beta" channel
#   ./publish.sh --token perm:…  # pass the token explicitly
#
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
XML_ID="com.github.pathreference"
CHANNEL=""
BUILD_ONLY=0
TOKEN="${JETBRAINS_MARKETPLACE_TOKEN:-}"

while [[ $# -gt 0 ]]; do
    case "$1" in
        --build-only) BUILD_ONLY=1; shift ;;
        --channel) CHANNEL="${2:?missing value for --channel}"; shift 2 ;;
        --token) TOKEN="${2:?missing value for --token}"; shift 2 ;;
        -h|--help) sed -n '2,20p' "${BASH_SOURCE[0]}" | sed 's/^# \{0,1\}//'; exit 0 ;;
        *) echo "error: unknown argument: $1" >&2; exit 2 ;;
    esac
done

VERSION="$(sed -n 's:.*<version>\(.*\)</version>.*:\1:p' \
    "$ROOT/src/main/resources/META-INF/plugin.xml" | head -1)"
ZIP="$ROOT/build/path-reference-plugin-$VERSION.zip"

"$ROOT/build.sh"

if [[ $BUILD_ONLY -eq 1 ]]; then
    echo
    echo "Built $ZIP"
    echo "Upload it at https://plugins.jetbrains.com/author/me"
    exit 0
fi

if [[ -z "$TOKEN" && -f "$HOME/.jetbrains-marketplace-token" ]]; then
    TOKEN="$(tr -d '[:space:]' < "$HOME/.jetbrains-marketplace-token")"
fi

if [[ -z "$TOKEN" ]]; then
    cat >&2 <<'EOF'
error: no Marketplace token.

1. Create / log in to your JetBrains account.
2. Open https://plugins.jetbrains.com/author/me/tokens and click "Generate Token".
3. Save it (it is shown only once):

     echo 'perm:xxxxxxxxxxxxxxxx' > ~/.jetbrains-marketplace-token
     chmod 600 ~/.jetbrains-marketplace-token

   or export JETBRAINS_MARKETPLACE_TOKEN=perm:xxxxxxxxxxxxxxxx

Note: the very first upload of a plugin cannot be done through this API — use
"Add new plugin" on https://plugins.jetbrains.com/author/me once, then this
script handles all updates.
EOF
    exit 1
fi

echo
echo "Uploading $VERSION to JetBrains Marketplace (channel: ${CHANNEL:-stable})..."

args=(-sS -X POST
      -H "Authorization: Bearer $TOKEN"
      -H "Accept: application/json"
      -F "xmlId=$XML_ID"
      -F "file=@$ZIP")
if [[ -n "$CHANNEL" ]]; then
    args+=(-F "channel=$CHANNEL")
fi

response="$(curl "${args[@]}" -w '\nHTTP %{http_code}\n' https://plugins.jetbrains.com/api/updates/upload)"
echo "$response"

if grep -q 'HTTP 2' <<< "$response"; then
    echo
    echo "Uploaded. The new version appears on https://plugins.jetbrains.com/plugin/$XML_ID"
    echo "after JetBrains' automated checks finish (usually a few minutes)."
else
    echo
    echo "Upload failed. Common causes: invalid/expired token, version $VERSION" >&2
    echo "already published (bump <version> in plugin.xml), or plugin not created yet." >&2
    exit 1
fi
