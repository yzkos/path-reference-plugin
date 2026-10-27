# Path Reference (Path:Line)

**中文** | [English](README.en.md)

一个给 PhpStorm / JetBrains IDE 用的小插件：把选中的代码变成
**项目根目录相对路径 + 行号**，直接进剪贴板。

```
@app/Services/UserService.php:42
@app/Services/UserService.php:42-57
```

## 为什么不用内置的 Copy Reference？

内置的 `Copy Reference`（`Ctrl+Alt+Shift+C` / `⌘⌥⇧C`）有两个限制：

- 光标停在类名、方法名上时，复制的是 PHP 符号引用（`App\Foo::bar`），不是路径；
- 它只取光标所在行，选中多行也只给起始行。

这个插件的行为是固定的：**永远是 `路径 + 行号`**（分隔符可自定义，默认 `:`），
并且支持选区范围、多光标。

## 功能

- 选中代码 → `开头符号 + 相对路径:起始行-结束行`
- 不选中 → `开头符号 + 相对路径:光标行`
- 开头符号可配置（默认 `@`，也可以改成 `#`、`>`，或留空）
- 路径与行号之间的分隔符可配置（默认 `:`，也可以是 `#`、`|`、`@`，
  支持多字符和空格，如 ` #` → `@src/Foo.php #42`，空格原样保留）
- 多光标 / 多选区 → 每行一个引用
- 在 Project 视图里选中文件 → 只复制相对路径
- 状态栏显示复制结果
- 可选的 Markdown 反引号、代码块
- 路径风格可切换：项目根目录 / content root / 绝对路径

## 使用

| 操作 | 快捷键 |
|---|---|
| 复制路径 + 行号 | `Alt + C` |

也可以右键编辑器 → **Copy Path with Line Numbers**。

输出示例（默认开头符号是 `@`）：

| 场景 | 结果 |
|---|---|
| 光标在第 42 行 | `@src/Foo.php:42` |
| 选中第 42–57 行 | `@src/Foo.php:42-57` |
| 选中第 42 行的一部分 | `@src/Foo.php:42` |
| 多光标分别在第 5、20 行 | `@src/Foo.php:5`<br>`@src/Foo.php:20` |
| 把「路径/行号」分隔符改成 `#` | `@src/Foo.php#42`、`@src/Foo.php#42-57` |
| 分隔符改成 ` #`（空格 + 井号） | `@src/Foo.php #42`、`@src/Foo.php #42-57` |

### 设置

`Settings | Tools | 路径引用`（英文 IDE 下显示为 `Settings | Tools | Path Reference`）

也可以从菜单 **Tools → 路径引用设置…**（英文：`Path Reference Settings…`）直接打开。

- **路径风格**：`项目根目录`（默认）/ `内容根目录` / `绝对路径`
- **开头符号**：复制结果开头添加的符号，默认 `@`；留空则不加
- **路径与行号分隔符**：文件路径和行号之间的分隔符，默认 `:`；改成 `#` 就得到
  `@src/Foo.php#42`，改成 `|` 就是 `@src/Foo.php|42-57`。
  支持**多字符和空格**：填入 ` #` 会得到 `@src/Foo.php #42`，
  填入 ` <- ` 会得到 `@src/Foo.php <- 42`；前后空格都会原样保留（不会被 trim）
- **行号范围分隔符**：起始行与结束行之间的分隔符，默认 `-`（也可以改成 `~`、`..` 等）
- **用反引号包裹引用（Markdown）**：输出 `` `@src/Foo.php:42-57` ``，方便贴 Markdown
- **在引用下方附加选中的代码块**：

  ````text
  `@src/Foo.php:42-57`
  ```php
  ...选中的代码...
  ```
  ````

#### 多语言

插件界面（设置页、右键菜单、Tools 菜单、状态栏提示）跟随 IDE 语言：

| IDE 语言 | 显示 |
|---|---|
| 英文（默认） | `Path Reference` / `Copy Path with Line Numbers` |
| 中文 | `路径引用` / `复制路径和行号` |

实现方式是 IntelliJ 标准的 `DynamicBundle` +
`messages/PathReferenceBundle.properties`（英文）/ `PathReferenceBundle_zh_CN.properties`（中文）。
切换 IDE 语言（或安装/卸载中文语言包）后重启 IDE 即可看到对应语言。

插件描述（`Settings | Plugins` 里的详情页）是中英双语的，中英文 IDE 都能直接看懂。

## 构建

不依赖 Gradle，使用本机 PhpStorm 自带的 JBR 和平台 jar 编译。

```bash
./build.sh
```

产物（`<version>` 取自 `plugin.xml`）：

```
build/path-reference-<version>.jar          # 插件 jar
build/path-reference-plugin-<version>.zip   # 可安装的 zip
```

构建脚本会同时运行 `ReferenceFormatterTest`，覆盖格式化、行号范围和选区边界。
最低 Java 目标版本为 17（`since-build 241`，即 2024.1+）。

## 安装

**方式 1：从插件中心安装（推荐）**

`Settings | Plugins` → `Marketplace` → 搜索 **Path Reference** → **Install**，
重启 IDE 即可。

**方式 2：下载 zip 安装**

从 [Releases](https://github.com/yzkos/path-reference-plugin/releases/latest) 下载
`path-reference-plugin-<version>.zip`，然后在
`Settings | Plugins` → ⚙ → **Install Plugin from Disk...** 里选择该 zip，重启 IDE。

卸载：`Settings | Plugins` 里找到 **Path Reference (Path:Line)** → **Uninstall**。

## 相关链接

| 项 | 值 |
|---|---|
| JetBrains Marketplace | <https://plugins.jetbrains.com/plugin/35487-path-reference-path-line-> |
| 源码仓库 | <https://github.com/yzkos/path-reference-plugin> |
| Issue Tracker | <https://github.com/yzkos/path-reference-plugin/issues> |
| License | Apache-2.0 |

插件免费、无广告、不采集任何数据。

## 兼容性

- 只依赖 `com.intellij.modules.platform`，不依赖 PHP 插件，所以 PhpStorm、IDEA、
  WebStorm 等都能用（在 PhpStorm 上开发和测试）。
- 编译目标 Java 17；`since-build="241"`（2024.1+），没有设置 `until-build`。

## 目录结构

```
src/main/java/com/github/pathreference/
├── CopyPathReferenceAction.java   # 动作：取文件、选区、写剪贴板
├── OpenSettingsAction.java        # Tools 菜单里的设置入口
├── LineRangeCalculator.java       # 纯函数：offset -> 行号范围（可单测）
├── ReferenceFormatter.java        # 纯函数：拼 `path:start-end`
├── PathReferenceSettings.java     # PropertiesComponent 读写
├── PathReferenceBundle.java       # 多语言字符串入口 (DynamicBundle)
└── PathReferenceConfigurable.java # Settings UI
src/main/resources/META-INF/plugin.xml
src/main/resources/META-INF/pluginIcon.svg  # 插件图标（40×40，无 <text>，平台 SVG 渲染器不支持文字）
src/main/resources/messages/
├── PathReferenceBundle.properties         # 英文（默认）
└── PathReferenceBundle_zh_CN.properties   # 中文
src/test/java/...                  # 不依赖 IDE 的单元测试
build.sh                           # 编译 + 测试 + 打包
LICENSE                            # Apache-2.0
```

## 许可证

[Apache License 2.0](LICENSE)。

## 排障

- **按 `Alt+C` 没反应**：`Settings | Keymap` 搜 `Copy Path with Line Numbers`，
  确认绑定没有被系统或其它插件覆盖（Windows 下 `Alt+C` 可能和菜单助记键冲突，
  可在 Keymap 里换一个组合键）。

- **路径不是相对项目根目录**：确认 `Settings | Tools | 路径引用` 里的
  路径风格是 `项目根目录`，并且项目根目录就是包含 `.idea` 的目录。
