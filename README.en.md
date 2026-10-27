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
PhpStorm.

```bash
./build.sh
```

Artifacts (`<version>` is read from `plugin.xml`):

```
build/path-reference-<version>.jar          # plugin jar
build/path-reference-plugin-<version>.zip   # installable zip
```

The build script also runs `ReferenceFormatterTest`, which covers formatting,
line ranges and selection boundaries. Minimum Java target is 17
(`since-build 241`, i.e. 2024.1+).

## Install

**Option 1: install from the Marketplace (recommended)**

`Settings | Plugins` → `Marketplace` → search for **Path Reference** → **Install**,
then restart the IDE.

**Option 2: install from the zip**

Download `path-reference-plugin-<version>.zip` from
[Releases](https://github.com/yzkos/path-reference-plugin/releases/latest), then
`Settings | Plugins` → ⚙ → **Install Plugin from Disk...** and pick the zip;
restart the IDE.

Uninstall: `Settings | Plugins` → **Path Reference (Path:Line)** → **Uninstall**.

## Links

| Item | Value |
|---|---|
| JetBrains Marketplace | <https://plugins.jetbrains.com/plugin/35487-path-reference-path-line-> |
| Source repository | <https://github.com/yzkos/path-reference-plugin> |
| Issue tracker | <https://github.com/yzkos/path-reference-plugin/issues> |
| License | Apache-2.0 |

The plugin is free, has no ads and collects no data.

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
build.sh                           # compile + test + package
LICENSE                            # Apache-2.0
```

## License

[Apache License 2.0](LICENSE).

## Troubleshooting

- **`Alt+C` does nothing**: open `Settings | Keymap` and search for
  `Copy Path with Line Numbers`; make sure the binding is not overridden by the
  system or another plugin (on Windows `Alt+C` may clash with menu mnemonics —
  pick another shortcut in the Keymap).

- **The path is not relative to the project root**: check that the path style in
  `Settings | Tools | Path Reference` is `Project root`, and that the project
  root is the directory containing `.idea`.
