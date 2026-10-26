#!/usr/bin/env bash
#
# Builds and installs the plugin into the current user's PhpStorm plugins dir.
# Usage: ./install.sh [plugins-dir]
#
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEST="${1:-$HOME/.local/share/JetBrains/PhpStorm2026.2}"

if [[ ! -d "$DEST" ]]; then
    echo "error: plugins directory not found: $DEST" >&2
    echo "       Pass the correct directory as the first argument." >&2
    exit 1
fi

"$ROOT/build.sh"

rm -rf "$DEST/path-reference"
cp -R "$ROOT/build/plugin/path-reference" "$DEST/"
echo "Installed to: $DEST/path-reference"
echo "Restart PhpStorm (or the remote-development backend) to load the plugin."
