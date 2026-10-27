# Path Reference (Path:Line)

[中文](README.md) | **English**

A small plugin for PhpStorm / JetBrains IDEs that turns the selected code into a
**project-relative path + line number** and puts it straight on the clipboard.

```
@app/Services/UserService.php:42
@app/Services/UserService.php:42-57
```

## Why not the built-in Copy Reference?

The built-in `Copy Reference` (`Ctrl+Alt+Shift+C` / `⌘⌥⇧C`) has two limitations:

- when the caret sits on a class or method name, it copies a PHP symbol reference
  (`App\Foo::bar`) instead of a path;
- it only takes the caret line — selecting several lines still yields just the
  start line.

This plugin is predictable: it **always** produces `path + line number`
(the separator is configurable, default `:`), and it supports selection ranges
and multiple carets.

## Features

- Selection → `prefix + relative/path:startLine-endLine`
- No selection → `prefix + relative/path:caretLine`
- Configurable prefix (default `@`; can be `#`, `>`, or empty)
- Configurable separator between the path and the line number (default `:`, also
  `#`, `|`, `@`, …; multiple characters and spaces are supported, e.g. ` #`
  gives `@src/Foo.php #42`, and the spaces are kept exactly as typed)
- Multiple carets / selections → one reference per line
- Selecting a file in the Project view → copies just the relative path
- The result is shown in the status bar
- Optional Markdown backticks and fenced code block
- Switchable path style: project root / content root / absolute path

## Usage

| Action | Shortcut |
|---|---|
| Copy path + line number | `Alt + C` |

You can also right-click in the editor → **Copy Path with Line Numbers**.

Example output (default prefix is `@`):

| Situation | Result |
|---|---|
| Caret on line 42 | `@src/Foo.php:42` |
| Lines 42–57 selected | `@src/Foo.php:42-57` |
| Part of line 42 selected | `@src/Foo.php:42` |
| Carets on lines 5 and 20 | `@src/Foo.php:5`<br>`@src/Foo.php:20` |
| Path/line separator changed to `#` | `@src/Foo.php#42`, `@src/Foo.php#42-57` |
| Separator changed to ` #` (space + hash) | `@src/Foo.php #42`, `@src/Foo.php #42-57` |

### Settings

`Settings | Tools | Path Reference` (`Settings | Tools | 路径引用` in a Chinese IDE)

You can also open it from the menu **Tools → Path Reference Settings…**
(Chinese: `路径引用设置…`).

- **Path style**: `Project root` (default) / `Content root` / `Absolute path`
- **Prefix**: text prepended to the result, default `@`; leave empty to add nothing
- **Path/line separator**: the text between the file path and the line number,
  default `:`; `#` gives `@src/Foo.php#42`, `|` gives `@src/Foo.php|42-57`.
  **Multiple characters and spaces** are supported: ` #` gives
  `@src/Foo.php #42`, ` <- ` gives `@src/Foo.php <- 42`; leading and trailing
  spaces are preserved verbatim (they are not trimmed)
- **Line range separator**: between the start and end line, default `-`
  (also `~`, `..`, …)
- **Wrap the reference in backticks (Markdown)**: outputs
  `` `@src/Foo.php:42-57` ``, handy for Markdown
- **Append the selected code block below the reference**:

  ````text
  `@src/Foo.php:42-57`
  ```php
  ...selected code...
  ```
  ````

#### Localization

The plugin UI (settings page, context menu, Tools menu, status bar message)
follows the IDE language:

| IDE language | Shown as |
|---|---|
| English (default) | `Path Reference` / `Copy Path with Line Numbers` |
| Chinese | `路径引用` / `复制路径和行号` |

This uses the standard IntelliJ `DynamicBundle` +
`messages/PathReferenceBundle.properties` (English) /
`PathReferenceBundle_zh_CN.properties` (Chinese). Switch the IDE language
(or install/remove the Chinese language pack) and restart the IDE to see it.

The plugin description in `Settings | Plugins` is bilingual, so it reads well
in both English and Chinese IDEs.

## Build

No Gradle: it compiles with the JBR and platform jars shipped with a local
PhpStorm, so there is no need to download the whole IntelliJ Platform SDK.

```bash
./build.sh
```

If PhpStorm cannot be auto-detected:

```bash
PHPSTORM_HOME=/path/to/PhpStorm ./build.sh
```

Artifacts:

```
build/path-reference-1.4.2.jar                  # plugin jar
build/path-reference-plugin-1.4.2.zip           # installable zip
build/plugin/path-reference/lib/*.jar           # drop-in for the plugins directory
```

The build script also runs `ReferenceFormatterTest`, which covers formatting,
line ranges and selection boundaries. The version number lives only in
`plugin.xml`; both `build.sh` and `publish.sh` read it from there.
Minimum Java target is 17 (`since-build 241`, i.e. 2024.1+).

## Install

Three options, pick one.

**1. Copy the directory (most robust, recommended for Remote Development)**

Copy the whole `build/plugin/path-reference` directory into the IDE plugins
directory, then restart the IDE:

```bash
cp -R build/plugin/path-reference ~/.local/share/JetBrains/PhpStorm2026.2/
```

**2. Install Plugin from Disk with the jar**

`Settings | Plugins | ⚙ | Install Plugin from Disk...`, then pick:

```
build/path-reference-1.4.2.jar
```

A single jar is the least error-prone format (the plugin descriptor
`META-INF/plugin.xml` is inside the jar).

**3. Install Plugin from Disk with the zip**

Pick `build/path-reference-plugin-1.4.2.zip`; its structure is:

```
path-reference/lib/path-reference-1.4.2.jar
```

> Note: the zip must keep the outer `path-reference/` directory. If you flatten
> it to `lib/xxx.jar`, the IDE treats `lib` as the plugin root and reports
> "Cannot load plugin descriptor from file …".

> This environment is WSL + Remote Development: the plugin must be installed in
> the **backend (WSL side)** plugins directory, i.e. the path above; restart the
> IDE (or the backend process) on the client side afterwards.

Uninstall: delete `~/.local/share/JetBrains/PhpStorm2026.2/path-reference/`, or
use Uninstall in `Settings | Plugins`.

> The plugin ID is fixed as `com.github.yzkos.pathreference` (Marketplace uses
> the `<id>` from the uploaded `plugin.xml`; the web form does not change it).
> If you installed a very early build (ID `com.github.pathreference`), uninstall
> the old `Path Reference (Path:Line)` in `Settings | Plugins` first, otherwise
> both will appear. Settings are global keys and survive reinstallation.

## Publishing to JetBrains Marketplace

The plugin is free, licensed under **Apache-2.0**, and published on JetBrains
Marketplace:
<https://plugins.jetbrains.com/plugin/35487-path-reference-path-line->

| Item | Value |
|---|---|
| Plugin XML ID | `com.github.yzkos.pathreference` |
| Vendor | `yzkos` (https://github.com/yzkos/path-reference-plugin) |
| Source repository | https://github.com/yzkos/path-reference-plugin |
| Issue tracker | https://github.com/yzkos/path-reference-plugin/issues |
| License | Apache-2.0 |

### Suggested Marketplace form values

Fields on the **Add new plugin** page (leave the rest at their defaults):

| Field | Value |
|---|---|
| Plugin name | `Path Reference (Path:Line)` |
| Plugin ID | Determined by the `<id>` in the uploaded package: `com.github.yzkos.pathreference` (not typed by hand) |
| Vendor | `yzkos` |
| License | `Apache-2.0` |
| Category | `Editor` (or `Code tools`) |
| Tags | `php`, `path`, `line number`, `copy`, `reference`, `markdown`, `remote development` |
| Source code URL | `https://github.com/yzkos/path-reference-plugin` |
| Issue tracker URL | `https://github.com/yzkos/path-reference-plugin/issues` |
| Pricing | `Free` |
| Data collection / ads | neither (the plugin has no network access, collects nothing, has no ads) |
| EULA / Privacy Policy | leave empty |
| Supported IDEs | computed from `plugin.xml` (`since-build 241`, no `until-build`) |

The plugin name, description, vendor, change notes and icon all come from
`plugin.xml` and `META-INF/pluginIcon.svg`; just verify them after uploading.

### One-time preparation (plugin author only)

1. Sign in at <https://plugins.jetbrains.com/author/me> with your JetBrains
   Account and accept the Marketplace Developer Agreement.
2. Click **Add new plugin** and upload
   `build/path-reference-plugin-1.4.2.zip`, then fill in the plugin page
   (the description is read from `plugin.xml` and is already bilingual).
3. Wait for the automated checks (Plugin Verifier; this plugin is
   `Compatible`). The plugin becomes public once they pass.
4. Generate a **permanent token** at
   <https://plugins.jetbrains.com/author/me/tokens> for uploading later
   versions:

   ```bash
   echo 'perm:xxxxxxxx' > ~/.jetbrains-marketplace-token
   chmod 600 ~/.jetbrains-marketplace-token
   ```

> The very first release must be uploaded through the web UI (Marketplace
> rule); the API/script works for all later versions.

### Publishing a new version

```bash
# 1. bump <version> and <change-notes> in src/main/resources/META-INF/plugin.xml
# 2. build + upload (Stable channel by default)
./publish.sh

# build only, no upload
./publish.sh --build-only

# publish to the beta channel
./publish.sh --channel beta
```

What the script does: `./build.sh` → `POST https://plugins.jetbrains.com/api/updates/upload`
(`xmlId=com.github.yzkos.pathreference`, token from `JETBRAINS_MARKETPLACE_TOKEN`
or `~/.jetbrains-marketplace-token`).

Marketplace **rejects uploading the same version twice**, so bump `<version>`
before each release.

### Signing

The plugin is currently unsigned, so the IDE may show a one-time "unsigned"
warning. To remove it, sign the zip with a self-signed certificate using
[Marketplace ZIP Signer](https://github.com/JetBrains/marketplace-zip-signer)
(generate a key plus `chain.crt` with `openssl`, then sign); this is optional.

## Compatibility

- Depends only on `com.intellij.modules.platform`, not on the PHP plugin, so it
  works in PhpStorm, IDEA, WebStorm, etc. (developed and tested on PhpStorm).
- Compiled for Java 17; `since-build="241"` (2024.1+), no `until-build`.

## Project layout

```
src/main/java/com/github/pathreference/
├── CopyPathReferenceAction.java   # action: file, selection, clipboard
├── OpenSettingsAction.java        # settings entry in the Tools menu
├── LineRangeCalculator.java       # pure function: offset -> line range (unit tested)
├── ReferenceFormatter.java        # pure function: builds `path:start-end`
├── PathReferenceSettings.java     # PropertiesComponent read/write
├── PathReferenceBundle.java       # localized strings (DynamicBundle)
└── PathReferenceConfigurable.java # settings UI
src/main/resources/META-INF/plugin.xml
src/main/resources/META-INF/pluginIcon.svg  # icon (40x40, no <text>, the platform SVG renderer does not support text)
src/main/resources/messages/
├── PathReferenceBundle.properties         # English (default)
└── PathReferenceBundle_zh_CN.properties   # Chinese
src/test/java/...                  # IDE-independent unit tests
build.sh                           # compile + test + package (no install)
install.sh                         # build, then install into the local plugins dir
publish.sh                         # build + upload to JetBrains Marketplace
LICENSE                            # Apache-2.0
```

## License

[Apache License 2.0](LICENSE).

## Troubleshooting

- **`Alt+C` does nothing**: open `Settings | Keymap` and search for
  `Copy Path with Line Numbers`; make sure the binding is not overridden by the
  system or another plugin (on Windows `Alt+C` may clash with menu mnemonics —
  pick another shortcut in the Keymap).
- **`Shift+Alt+C` opens a chooser**: the previously installed
  `Copy Code Reference with Line Numbers 1.1.0`
  (`com.ccsoftware.copycodereference`) is still there. Its jar contains only
  `plugin.xml` and no class files, so it is broken; uninstall it in
  `Settings | Plugins`.
- **The path is not relative to the project root**: check that the path style in
  `Settings | Tools | Path Reference` is `Project root`, and that the project
  root is the directory containing `.idea`.
