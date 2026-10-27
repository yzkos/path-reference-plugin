# 发布到 JetBrains Marketplace

本文件只记录**维护者**的上架/发版流程，普通用户不需要看。
插件本体和用法见 [README.md](README.md)。

插件地址：<https://plugins.jetbrains.com/plugin/35487-path-reference-path-line->

| 项 | 值 |
|---|---|
| Plugin XML ID | `com.github.yzkos.pathreference` |
| Vendor | `yzkos`（https://github.com/yzkos/path-reference-plugin） |
| 源码仓库 | https://github.com/yzkos/path-reference-plugin |
| Issue Tracker | https://github.com/yzkos/path-reference-plugin/issues |
| License | Apache-2.0 |

插件是免费的，采用 **Apache-2.0** 许可。

## Marketplace 表单填写建议

**Add new plugin** 页面上要填的字段（其余按默认）：

| 字段 | 填什么 |
|---|---|
| Plugin name | `Path Reference (Path:Line)` |
| Plugin ID | 由上传包里的 `<id>` 决定：`com.github.yzkos.pathreference`（不是手填的） |
| Vendor | `yzkos` |
| License | `Apache-2.0` |
| Category | `Editor`（或 `Code tools`） |
| Tags | `php`, `path`, `line number`, `copy`, `reference`, `markdown`, `remote development` |
| Source code URL | `https://github.com/yzkos/path-reference-plugin` |
| Issue tracker URL | `https://github.com/yzkos/path-reference-plugin/issues` |
| Pricing | `Free` |
| 收集用户数据 / 广告 | 都不勾（本插件不联网、不采集、无广告） |
| EULA / Privacy Policy | 留空 |
| 支持的 IDE | 默认按 `plugin.xml` 计算（`since-build 241`，无 `until-build`） |

插件名、描述、vendor、change-notes、图标都从 `plugin.xml` 和
`META-INF/pluginIcon.svg` 读取，上传后核对即可。

## 一次性准备（只能由插件作者完成）

1. 用 JetBrains Account 登录 <https://plugins.jetbrains.com/author/me>，
   同意 Marketplace Developer Agreement。
2. 点 **Add new plugin**，上传 `build/path-reference-plugin-<version>.zip`（`<version>` 取自 `plugin.xml`），
   填写插件页信息（描述会自动读取 `plugin.xml`，已是中英双语）。
3. 等待自动化检查通过（会跑 Plugin Verifier，本插件为 `Compatible`），
   通过后插件才会公开。
4. 在 <https://plugins.jetbrains.com/author/me/tokens> 生成 **permanent token**
   用于上传后续版本：

   ```bash
   echo 'perm:xxxxxxxx' > ~/.jetbrains-marketplace-token
   chmod 600 ~/.jetbrains-marketplace-token
   ```

> 第一次发布必须走网页上传（Marketplace 的规定），之后才能用 API/脚本。

## 发布新版本

```bash
# 1. 改 src/main/resources/META-INF/plugin.xml 里的 <version> 和 <change-notes>
# 2. 构建 + 上传（默认 Stable 通道）
./publish.sh

# 只构建、不上传
./publish.sh --build-only

# 发到 beta 通道
./publish.sh --channel beta
```

脚本做的事：`./build.sh` → `POST https://plugins.jetbrains.com/api/updates/upload`
（`xmlId=com.github.yzkos.pathreference`，token 从环境变量
`JETBRAINS_MARKETPLACE_TOKEN` 或 `~/.jetbrains-marketplace-token` 读取）。

Marketplace **不接受相同版本号重复上传**，每次发版记得先改 `<version>`。

## 关于签名

插件目前未签名，安装时 IDE 可能显示一次「未签名」提示。要消除它需要用
[Marketplace ZIP Signer](https://github.com/JetBrains/marketplace-zip-signer)
自签证书后再上传（`openssl` 生成 key + `chain.crt`，然后签名 zip），属于可选步骤。
