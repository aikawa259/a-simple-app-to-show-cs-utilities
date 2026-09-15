# CS Utility（Android）

一个自己整理 CS 道具的安卓 App：**地图由你自己建，道具由你自己录**，每条道具可以记录站位图、瞄点图、效果图。

最初的版本是照着 iOS 项目 [`lec444c/My-First-IOS-App`](https://github.com/lec444c/My-First-IOS-App) 复刻的，
后来按需求改成了"用户自建地图 + 自建道具"的形态，并移除了原来的 2D 战术地图功能。

## 下载安装（安卓手机）

**免登录直链**（链接不随版本变化，可以直接发给别人）：

https://github.com/aikawa259/a-simple-app-to-show-cs-utilities/releases/latest/download/CSLineups-debug.apk

安装步骤：打开链接下载 → 浏览器提示时选择用系统安装器打开 → 允许"安装未知来源应用"
→ 出现"Play 保护机制"警告时点"更多信息" → "仍要安装"。要求 Android 8.0 及以上。

## 功能

**地图首页**

- 左上角 `+`：新建地图，输入地图名即可（可以重命名、删除）
- 标题（地图名 + 箭头）：点开全部地图列表，切换地图
- 三个入口：道具列表 / 搜索 / 收藏
- 右下角 `+`：添加道具，只需填「道具名」和「投掷点」，创建后直接进编辑页
- 首次启动自带一个示例地图 Mirage（6 条道具），可以删掉

**道具详情页**：概览、起始位置 / 目标位置、投掷步骤、三张教学图（点击全屏、可双指放大）、说明；
右上角星标收藏，铅笔进编辑页。

**道具编辑页**（改动自动保存，不用点保存）：
名称、类型、阵营、分类、难度、起始位置、目标位置、投掷步骤、说明、站位图 / 瞄点图 / 效果图。
图片从手机相册选，会复制进应用私有目录，不需要任何存储权限。

**其他**：中英文界面切换（设置页）、收藏与语言持久化。

## 技术决策

| 项目 | 选择 | 说明 |
|---|---|---|
| 平台 | Kotlin + Jetpack Compose | 单 Activity + Navigation Compose，页面切换用弹簧动画 |
| 数据 | 用户数据存 `filesDir/maps.json` | 用系统自带 `org.json` 读写，零额外依赖；图片存 `filesDir/images/` |
| 状态 | `MapRepository` + `StateFlow` | 增删改后立即落盘，界面随 flow 自动刷新 |
| 文案 | 自建 `Strings` 接口 + 中英文实现 | App 内切换语言，不依赖系统语言 |
| 动效 | 弹簧动画为主 | 页面从入口附近"旋转展开"（导航层缩放 + 页面自身旋转）、内容依次抬入、卡片按压缩放、胶囊选中回弹 |
| 图标 | 自适应图标 + 脚本生成 | 源图放 `docs/branding/`，`scripts/make-icons.ps1` 自动裁切、去白底并生成 5 个密度的前景图 |

## 目录结构

```
app/src/main/java/com/cslineups/app/
  model/          数据模型（地图 / 道具 / 枚举）
  data/           本地存储、图片导入、设置与收藏
  i18n/           中英文界面文案
  ui/theme/       配色
  ui/components/  卡片、徽章、弹窗、图片槽、动效工具
  ui/screens/     地图首页、道具列表、详情、编辑、搜索、收藏、设置、关于
app/src/test/     单元测试与界面渲染测试
scripts/          一键编译脚本、GitHub 推送脚本
docs/branding/    图标源图
```

## 测试

```
powershell -ExecutionPolicy Bypass -File scripts\build-apk.ps1
```

- `MapRepositoryTest`（7 项）：示例数据、新建地图、添加 / 编辑 / 删除道具、重命名、删空后不复活
- `ItemSearchTest`（5 项）：按名称 / 位置 / 类型 / 难度搜索，忽略大小写与空格
- `MapHomeScreenTest`（3 项）：首页三个入口、底部加号、无地图时的引导

共 15 项，全部通过。

> 未覆盖：弹窗的打开/关闭交互。Robolectric 环境下弹窗打开后 Compose 不会回到空闲状态，
> 测试会一直等到超时，因此这类交互留给真机验证（仓库层逻辑已被上面的测试覆盖）。

## 编译

工具链放在工程同级的 `tools` 目录（Android SDK、Gradle、依赖缓存），脚本会自动使用：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\build-apk.ps1
```

产物会复制到同级 `dist\CSLineups-debug.apk`。没有 `tools` 时会退回用仓库自带的 `gradlew`，
此时需要本机已有 Android SDK 并在 `local.properties` 写好 `sdk.dir`。

## 上传与发版

```powershell
powershell -ExecutionPolicy Bypass -File scripts\upload-to-github.ps1 -RepoUrl https://github.com/用户名/仓库名.git
```

发新版本时：重新编译后，在 GitHub 网页 Releases → Draft a new release，把 `dist\CSLineups-debug.apk`
拖进附件区，**附件名保持 `CSLineups-debug.apk`**，上面那条安装直链就永远指向最新版。

## 已知问题

- 图片目前只能从相册选，不支持拍照
- 道具列表按分类分组，顺序固定，暂不支持拖动排序
- 数据只存在本机，没有云同步 / 导入导出
- 当前是 debug 包，自己装没问题；上架需要正式签名

## 变更记录

**0.4.0**

- 补齐普通 PNG 图标（方形 + 圆形，五个分辨率）：部分第三方启动器不认自适应图标，
  会退化成系统默认图标，这样就有兜底了
- 首页（开屏第一个页面）不再做旋转与甩字动效，避免盖住系统启动动画
- 二级页面动效加强：页面旋转从 -5° 提到 -16° 并带过冲；
  标题加了"被甩飞后归位"的效果（横向冲进来 + 旋转，再弹回过冲落位）

**0.3.0**

- 应用名改为 **CS Utility**，图标换成 CS Unity 风格的新 logo（自适应图标，白底 + 黑色 logo）
- 页面切换重做：改为从入口卡片所在的偏上位置放大展开，并带一点旋转，时长放慢（约 300–400ms）；
  同时修掉了切换时"闪一下黑"的问题（浅色模式下窗口底色原来是深色）
- 首页三个入口依次抬入，入口图标按下时会转动一下

**0.2.0**

- 移除 2D 战术地图（含点位坐标、拖动校准、开发者模式），相关代码与测试一并删除
- 改为用户自建地图：新建 / 重命名 / 删除地图，点标题切换地图
- 道具改为一级结构：添加时只填名称与投掷点，其余细节在编辑页补全（全部可编辑，自动保存）
- 支持为每条道具选择三张教学图，全屏查看时可双指放大

**0.1.0**

- 首个可安装版本，效果对齐 iOS 项目
- 修复地图上点位不显示（坐标被换算两次，点位跑到可视区外约 30 万像素处）
