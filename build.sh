#!/usr/bin/env bash
#
# Builds the plugin without Gradle, using the JBR/javac that ships with a local
# PhpStorm installation. Set PHPSTORM_HOME if auto-detection fails.
#
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD="$ROOT/build"
VERSION="$(sed -n 's:.*<version>\(.*\)</version>.*:\1:p' \
    "$ROOT/src/main/resources/META-INF/plugin.xml" | head -1)"
if [[ -z "$VERSION" ]]; then
    echo "error: could not read <version> from plugin.xml" >&2
    exit 1
fi
JAR_NAME="path-reference-$VERSION.jar"

detect_ide() {
    if [[ -n "${PHPSTORM_HOME:-}" && -x "${PHPSTORM_HOME}/jbr/bin/javac" ]]; then
        printf '%s\n' "$PHPSTORM_HOME"
        return 0
    fi
    local dir
    for dir in "$HOME"/.cache/JetBrains/RemoteDev/dist/*PhpStorm-* \
               "$HOME"/.local/share/JetBrains/Toolbox/apps/PhpStorm/* \
               /opt/phpstorm /opt/PhpStorm-*; do
        if [[ -x "$dir/jbr/bin/javac" ]]; then
            printf '%s\n' "$dir"
            return 0
        fi
    done
    return 1
}

IDE="$(detect_ide || true)"
if [[ -z "$IDE" ]]; then
    echo "error: PhpStorm installation not found." >&2
    echo "       Set PHPSTORM_HOME to the PhpStorm directory (the one containing jbr/ and lib/)." >&2
    exit 1
fi

JAVAC="$IDE/jbr/bin/javac"
JAVA="$IDE/jbr/bin/java"
if [[ ! -x "$JAVAC" ]]; then
    echo "error: $JAVAC not found" >&2
    exit 1
fi

echo "Using PhpStorm: $IDE"
rm -rf "$BUILD"
mkdir -p "$BUILD/classes" "$BUILD/plugin/path-reference/lib"

echo "Compiling..."
find "$ROOT/src/main/java" -name '*.java' > "$BUILD/sources.txt"
"$JAVAC" --release 17 -encoding UTF-8 -Xlint:-options \
    -cp "$IDE/lib/*" -d "$BUILD/classes" @"$BUILD/sources.txt"

cp -R "$ROOT/src/main/resources/." "$BUILD/classes/"

echo "Packaging..."
python3 - "$BUILD/classes" "$BUILD/$JAR_NAME" <<'PY'
import os
import sys
import zipfile

source, target = sys.argv[1], sys.argv[2]
with zipfile.ZipFile(target, "w", zipfile.ZIP_DEFLATED) as archive:
    for base, _dirs, files in os.walk(source):
        for name in sorted(files):
            full = os.path.join(base, name)
            arcname = os.path.relpath(full, source).replace(os.sep, "/")
            archive.write(full, arcname)
PY

cp "$BUILD/$JAR_NAME" "$BUILD/plugin/path-reference/lib/"

python3 - "$BUILD/plugin" "$BUILD/path-reference-plugin-$VERSION.zip" <<'PY'
import os
import sys
import zipfile

source, target = sys.argv[1], sys.argv[2]
with zipfile.ZipFile(target, "w", zipfile.ZIP_DEFLATED) as archive:
    for base, _dirs, files in os.walk(source):
        for name in sorted(files):
            full = os.path.join(base, name)
            arcname = os.path.relpath(full, source).replace(os.sep, "/")
            archive.write(full, arcname)
PY

if [[ -d "$ROOT/src/test/java" ]]; then
    echo "Running tests..."
    mkdir -p "$BUILD/test-classes"
    find "$ROOT/src/test/java" -name '*.java' > "$BUILD/test-sources.txt"
    "$JAVAC" --release 17 -encoding UTF-8 -Xlint:-options \
        -cp "$BUILD/classes:$IDE/lib/*" -d "$BUILD/test-classes" @"$BUILD/test-sources.txt"
    "$JAVA" -cp "$BUILD/classes:$BUILD/test-classes" com.github.pathreference.ReferenceFormatterTest
fi

echo
echo "Built:"
echo "  jar: $BUILD/$JAR_NAME"
echo "  zip: $BUILD/path-reference-plugin-$VERSION.zip"
echo "  dir: $BUILD/plugin/path-reference"
echo
echo "Install by copying the 'path-reference' directory to:"
echo "  ${IDEA_PLUGINS_DIR:-$HOME/.local/share/JetBrains/PhpStorm2026.2}/"
echo "then restart the IDE."
