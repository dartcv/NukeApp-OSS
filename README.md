# NukeApp-OSS

NukeApp-OSS 是给**抖音**（`com.ss.android.ugc.aweme`）用的 Android Hook 模块。装好以后，抖音的设置页里会多出一行「NukeApp-OSS」，点进去是一百多项功能开关，改动在抖音重启后生效。

> **状态：早期、实验性。** 目前没有发布页。每次推送到 `dev` 的构建都会作为 [GitHub Actions](https://github.com/dartcv/NukeApp-OSS/actions/workflows/ci.yml) 的 `release-apks` 产物上传（已签名的模块和 NukePatcher APK，下载需要登录 GitHub）。大多数功能还没有在真机的抖音各版本上逐项验证过，详见[已知限制](#已知限制)。请只在自己的设备上、自担风险使用。

## 能做什么

设置页按分类列出功能，只有「禁用双击点赞」默认开启，其余都默认关闭，用哪个开哪个。

| 分区 | 分类 |
| --- | --- |
| 视频 | 推荐流、播放控制、画质与缓存、视频页面、手势与操作、弹幕、广告 |
| 社交 | 评论、消息、聊天玩法、分享与转发、个人主页、隐私 |
| 应用 | 布局与显示、使用时长、应用与系统、其他 |

举几个例子：不上报已读状态（可只对黑名单或白名单里的会话生效）、自定义火花天数（只有你自己看得到）、自动续火、自动抢红包、聊天消息复读（气泡旁边的「+1」）、去除转发数量限制。每一项在设置页里都有一句说明。

除了内置功能，还能在设置页的「脚本」页导入 `.nks` 脚本包，用 JavaScript 写自己的功能。脚本怎么写、怎么打包见[脚本 SDK](sdk/script-sdk/README.md)。

## 需要什么

- Android 8.1（API 27）及以上。
- 抖音（不含国际版 TikTok）。
- 下面三种方式里选一种让模块进入抖音。三种方式的功能一样，区别只是怎么把模块放进去：

| 方式 | 适合谁 | 要求 |
| --- | --- | --- |
| Xposed | 已经在用 LSPosed 之类的框架 | 支持经典 Xposed（API 82）或现代 libxposed（API 102）的框架 |
| Zygisk | 有 root，用 Magisk、KernelSU 等 | 开启 Zygisk，设备是 `arm64-v8a` |
| 修补 | 没有 root | 用 NukePatcher 给抖音打补丁，Android 9 及以上，4 KiB 页大小 |

同时启用了多种方式也没关系：同一个进程里只有最先到达的那个入口生效，不会重复 Hook。

## 安装

### 第一步：拿到模块 APK

用 Xposed 或 Zygisk 方式时，要把 NukeApp-OSS 的 APK 装到手机上，设置页本身就在这个 APK 里。用修补方式时不用安装它，只要把这个 APK 作为文件选进 NukePatcher。

### 方式一：Xposed

1. 在框架里启用 NukeApp-OSS，作用域选抖音。
2. 彻底重启抖音（强行停止后再打开）。

### 方式二：Zygisk

1. 把模块 APK 改名为 `.zip`（也可以在模块首页「导出模块 APK」，再改名）。**这个 zip 必须和已安装的 APK 是同一个构建。**
2. 在 root 管理器里「从本地安装」这个 zip。
3. 确认 Zygisk 已开启，并且抖音**不在**排除列表（DenyList）里，否则模块可能不会被加载进抖音。
4. 重启手机。

### 方式三：修补（没有 root）

1. 安装 NukePatcher，打开「修补」页。
2. 分别选一个 NukeApp-OSS 模块 APK 和一个完整的单 APK 抖音安装包。
3. 校验通过后开始修补，签名并核对嵌入内容后导出。
4. 自己安装导出的 APK。

需要知道：

- 产物用修补器自己的密钥签名，所以**不能覆盖更新原来签名的应用**，要先卸载原版再装。修补器不会替你安装、卸载或清除数据。
- 修补器的密钥只保存在 NukePatcher 自己的数据里，不会迁移。换了密钥之后，用旧密钥修补的应用不能被新的覆盖更新。
- 不支持拆分 APK、APKS、XAPK，也不支持已经被修补过的应用；16 KiB 页大小的设备会被拒绝。
- 修补器界面目前只有中文，可以离线使用。

## 使用

1. 打开抖音，进入设置，找到「NukeApp-OSS」。
2. 按分类找到想要的功能，打开开关。有额外选项的功能点进去设置。
3. **强行停止抖音再重新打开**，改动才会生效。

模块应用首页的运行状态目前还接不上宿主，会一直显示「未连接」，这不代表没有生效，以抖音设置页里能看到入口为准。

点设置页顶部的头像，会弹出「设备可信」对话框：只读地检查这台手机和本应用自己的进程，共 17 项，涵盖调试器、库注入、Frida、Hook 与 root 工具、内存映射、root、bootloader 与验证启动、SELinux、内核暴露面、挂载、系统构建、模拟器和 adbd 等。先给总判断，再按严重程度列出每一项。检查在本机完成，结果不会发送到任何地方。

## 出问题时

**抖音里没有「NukeApp-OSS」这一行**

- Xposed：检查作用域里勾了抖音，并且彻底重启过抖音。
- Zygisk：依次检查 Zygisk 是否开启、模块是否启用、抖音是不是在 DenyList 里、有没有重启手机、设备是不是 `arm64-v8a`。然后在电脑上看日志（需要 adb）：

  ```bash
  adb logcat -c
  adb shell am force-stop com.ss.android.ugc.aweme
  adb shell monkey -p com.ss.android.ugc.aweme -c android.intent.category.LAUNCHER 1
  adb logcat -d -s 'NukeApp-OSS/Zygisk'
  ```

  看到 `zygisk_started` 说明运行时起来了。抖音进程里有时看不到这条日志，这时可以读抖音数据目录里的 `cache/nukeapp-zygisk.txt`（需要 root）：里面记着这次启动有没有走到模块、`bootstrap` 是 `returned` 还是 `failed`，失败时还写着停在哪一步。
- 修补：确认装的是修补后的那个包，而不是原版。

**某个功能没效果**：功能开关改完要重启抖音。抖音更新后，部分功能依赖的内部代码可能变了，这样的功能会失效，需要等模块跟进。

**想确认模块在工作**：安装 Hook Fixture（测试应用），它的说明文字会显示当前入口 `backend=xposed82`、`libxposed102` 或 `lsplant`。输入 `Alice`：开启改写时结果是 `hooked:original:NukeApp`，关闭时是 `original:Alice`。这个开关只影响当前进程，重启后又会打开。

## 已知限制

- **验证有限。** Zygisk 方式在一台 Android 16 设备（ReSukiSU + ReZygisk）上跑通过运行时启动；Magisk 自带的 Zygisk、其他设备和系统版本没有验证。
- **修补方式还没在真机上复测。** 修补、签名、导出和安装能走通，但修补后的应用里模块曾因签名检查误判宿主而没有生效，已经修复，尚未在设备上确认 Hook 实际装上。
- **功能效果没有逐项核对。** 设置入口和每个功能的实际效果都没有对照真实的抖音各版本验证过。
- **Zygisk 的 native 库随 zip 提供**，不再要求同时装着同一个构建的 APK 才能加载它们；但这一点还没有在设备上单独验证过，也没有做过「不装 APK」的运行。设置页本身、宿主里显示的模块字符串和图标，以及模块签名检查仍然依赖已安装的 APK（没有 APK 时签名检查记为「未检查」并继续运行）。
- **设备可信检测只是参考。** 它读取本机状态，能 Hook 系统的工具可以让结果失真；root 一项只看常见路径下有没有 `su`，KernelSU 这类管理器会对普通应用隐藏 `su`，所以结果干净不等于设备没有 root。root、bootloader 解锁、adbd 在运行这类常见状态只作提示，不影响总体结论。「SELinux 策略」和模拟器运行环境的判断是经验值，别的厂商的手机启动方式不同时可能出现提示，不是定论。
- **还没做：发送详细日志。** 现在日志只能导出成文件。
- **脚本**：脚本页、导入、对话框和各个脚本模块没有在设备上的抖音里跑过。
- **模块签名**：模块会检查自己的 APK 是否用发布证书签名；没有发布密钥的构建不做任何强制。签名不符会让模块在抖音里不启动。

## 组成

三个应用互相独立：

| 名称 | 应用 ID | 作用 |
| --- | --- | --- |
| NukeApp-OSS（模块） | `io.github.nuke.oss` | 三种入口都带上的模块，加设置管理界面（中英文）。可以把模块 APK 按原字节导出。 |
| NukePatcher | `io.github.nuke.oss.patcher` | 把模块嵌进抖音的修补器，用固定版本的 [LSPatch](https://github.com/JingMatrix/LSPatch)。不依赖模块，也不要求设备上装了模块。 |
| Hook Fixture | `io.github.nuke.oss.fixture` | 测试应用，用来确认一个 Hook 是否生效。 |

Hook 只会装在两处：Hook Fixture，以及抖音的主进程和少数功能声明过的非隔离子进程（小程序、代码热更新）。

## 许可证

仓库按目录分两种许可证：

| 目录 | 许可证 | 原因 |
| --- | --- | --- |
| `apps/patcher`（NukePatcher） | [GPL-3.0](apps/patcher/LICENSE) | 它内置 LSPatch（GPL-3.0） |
| 其余：模块、`core`、`sdk`、`fixtures`、`tools` | [Apache-2.0](LICENSE) | |

版权归 dartcv，见 [NOTICE](NOTICE)。模块和修补器是各自独立的 APK，修补器只是把模块当文件嵌进目标应用，这属于并列分发，不改变各自的许可证。

模块的原生库静态链接了 LSPlant 和 DexBuilder（LGPL-3.0）。按 LGPL 的要求，你可以替换这两个库并重新链接：`python3 tools/build_native.py --export-relink <目录>` 会把重新链接所需的目标文件和脚本导出到那个目录，细节见 [native 说明](apps/module/src/main/cpp/README.md)。其余第三方组件的许可证文本在各应用的 `src/main/resources/third-party` 目录。

## 免责声明

本项目与抖音、字节跳动没有任何关联，也未获其认可。修改应用可能违反其服务条款，请只在自己的设备上、自担风险使用。

## 致谢

NukeApp-OSS 站在这些开源项目之上，感谢它们的作者：

- 界面与设计：[QAuxiliary](https://github.com/cinit/QAuxiliary)（cinit）。模块主界面的设计参考了它。
- 注入与 Hook：[LSPlant](https://github.com/LSPosed/LSPlant) 和 [DexBuilder](https://github.com/LSPosed/DexBuilder)（LSPosed）、[Dobby](https://github.com/jmpews/Dobby)（jmpews）、[xDL](https://github.com/hexhacking/xDL)（hexhacking）、[Zygisk 示例模块](https://github.com/topjohnwu/zygisk-module-sample)的 API 头文件（topjohnwu）、Xposed API 与 libxposed。
- 修补：[LSPatch](https://github.com/JingMatrix/LSPatch)（JingMatrix 的分支）。
- 查找与反射：[DexKit](https://github.com/LuckyPray/DexKit)（LuckyPray）、[KavaRef](https://github.com/HighCapable/KavaRef)（HighCapable）。
- 脚本：[QuickJS](https://bellard.org/quickjs/) 和 [quickjs-kt](https://github.com/dokar3/quickjs-kt)（dokar3）。
- 其他：[parallel-hashmap](https://github.com/greg7mdp/parallel-hashmap)（greg7mdp）、Silk-Codec-Android、AndroidHiddenApiBypass（LSPosed）、Jetpack Compose。

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=dartcv/NukeApp-OSS&type=Date)](https://star-history.com/#dartcv/NukeApp-OSS&Date)
