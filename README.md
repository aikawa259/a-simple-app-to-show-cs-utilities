# CS Lineups（Android）

CS 战术道具（lineup）教学 App 的安卓实现，效果对齐 iOS 项目
[`lec444c/My-First-IOS-App`](https://github.com/lec444c/My-First-IOS-App)（CSTacticsApp / AimNade）。

## 目标效果

- 地图列表 → 地图首页（2D 战术地图 / 道具列表 / 搜索 / 收藏 四个入口）
- **2D 战术地图**：雷达图 + 按归一化坐标打点、点位聚合、1–4 倍缩放与平移、区域/道具类型筛选
- 道具列表（按区域分组）→ 点位详情 → 丢法详情（站位图 / 瞄点图 / 落点图，可翻页放大）
- 搜索（中英文与去空格匹配）、收藏（点位与丢法分别收藏）、设置（语言、开发者模式）、关于
- **开发者模式**：拖动点位校正坐标，实时显示坐标，一键复制坐标或整包 JSON
- 中英文双语，App 内切换（不跟随系统，与 iOS 版一致）

## 技术决策

| 项目 | 选择 | 原因 |
|---|---|---|
| 平台 | Kotlin + Jetpack Compose | 与 iOS 的 SwiftUI 结构一一对应，移植成本最低 |
| minSdk / targetSdk | 26 / 35 | 26 起可用自适应图标（不需要位图图标资源） |
| 内容数据 | 复用 iOS 版 `lineups_mirage.json` | 文件结构完全一致，直接放进 `assets/`，未改动一个字段 |
| 解析 | `org.json`（系统自带） | 零依赖，避免额外插件与版本约束 |
| 文本 | 自建 `Strings` 接口 + 中英文实现 | iOS 版是 App 内切换语言，用 `strings.xml` 需要重建 Context，很别扭 |
| 持久化 | DataStore（Preferences） | 对应 iOS 的 `UserDefaults` |

## 目录结构

```
app/src/main/
  assets/lineups_mirage.json          内容数据（与 iOS 版同构）
  res/drawable/mirage_map.jpg         雷达图
  java/com/cslineups/app/
    model/                            数据模型
    data/                             JSON 读取、设置与收藏持久化
    i18n/                             中英文文案
    ui/theme/                         配色
    ui/components/                    徽章、点位、功能卡
    ui/screens/                       各页面
    ui/AppNav.kt                      导航图
docs/map-math-probe/                  坐标换算与手势的验证页（见下）
```

## 地图坐标换算（已验证）

点位在数据里是 0–1 的归一化坐标，渲染时先按图片宽高比算出居中后的适配矩形，再映射坐标：

```
fit(图片宽高比, 容器宽高比)  →  居中矩形  →  点位 = 矩形 left/top + 矩形宽高 × 归一化坐标
缩放与平移作用于包含图片和点位的整层，因此点位随缩放一起放大（与 iOS 的 UIScrollView 行为一致）
```

这段逻辑已在 `docs/map-math-probe/index.html` 里用真实数据验证通过（浏览器打开即可，手机上也能开）。

## 待办与已知问题

- [ ] 首次编译：本机还没有 Android SDK 与 Gradle，需要先装工具链
- [ ] Gradle Wrapper 的 jar 需要在有工具链后生成（`gradle wrapper --gradle-version 8.9`）
- [ ] **内容坐标需要校正**：现有 3 组点位与雷达图自身的标注对不上（例如「VIP 烟」的点落在中路北侧而不是 VIP 附近，「警家烟」的点落在中远匪口而不是警家）。iOS 版内置开发者模式就是用来手工校准的，安卓版保留同一能力
- [ ] 教学图（站位/瞄点/落点）在数据里被引用，但项目中没有对应图片，两端都走上占位逻辑

