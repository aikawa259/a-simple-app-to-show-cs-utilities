package com.cslineups.app.i18n

import com.cslineups.app.model.Lang

fun stringsFor(lang: Lang): Strings = if (lang == Lang.ZH_HANS) ZhStrings else EnStrings

/** 界面文案。内容文案（道具名、说明等）来自数据文件的双语字段。 */
interface Strings {
    val appName: String
    val maps: String
    val settings: String
    val language: String
    val followSystem: String
    val simplifiedChinese: String
    val english: String
    val about: String
    val version: String
    val appIntro: String
    val unofficialNotice: String
    val unofficialNoticeText: String
    val acknowledgements: String
    val acknowledgementsText: String

    val tacticalMap: String
    val utilityList: String
    val favorites: String
    val featureMapSubtitle: String
    val featureListSubtitle: String
    val featureSearchSubtitle: String
    val featureFavoritesSubtitle: String

    val overview: String
    val name: String
    val type: String
    val side: String
    val category: String
    val difficulty: String
    val lineupVariants: String
    val position: String
    val startArea: String
    val targetArea: String
    val spawnRequirement: String
    val lineupSteps: String
    val notes: String
    val teachingImages: String
    val startPosition: String
    val aimPoint: String
    val resultImage: String
    val placeholder: String

    val addFavorite: String
    val removeFavorite: String
    val close: String
    val favoriteGroups: String
    val favoriteVariants: String
    val emptyFavorites: String
    val emptyFavoritesMessage: String
    val emptyUtilities: String
    val emptyUtilitiesMessage: String
    val groupSubtitle: String
    val variantSubtitle: String
    val difficultyEasy: String
    val difficultyMedium: String

    val smoke: String
    val flash: String
    val molotov: String
    val he: String
    val categoryASite: String
    val categoryBSite: String
    val categoryMid: String
    val categoryTSide: String
    val categoryCTSide: String

    val areaFilter: String
    val utilityTypeFilter: String
    val filterFeatured: String
    val filterAll: String
    val tapHint: String
    val developerHint: String
    val clusteredUtilities: String
    val targetPoint: String
    val startPoint: String
    val targetPoints: String
    val variantStartPoints: String
    val lineConnections: String
    val liveCoordinates: String
    val lastEdited: String
    val noEditedCoordinate: String
    val copyCoordinates: String
    val copyJson: String
    val coordinatesCopied: String
    val jsonCopied: String

    val search: String
    val searchPrompt: String
    val searchHint: String
    val searchNoResults: String
    val searchResultGroup: String
    val searchResultVariant: String

    val developerMode: String

    fun lineupCount(count: Int): String
    fun variantCount(count: Int): String
}

object ZhStrings : Strings {
    override val appName = "CS 道具"
    override val maps = "地图"
    override val settings = "设置"
    override val language = "语言"
    override val followSystem = "跟随系统"
    override val simplifiedChinese = "简体中文"
    override val english = "English"
    override val about = "关于"
    override val version = "版本号"
    override val appIntro = "CS 战术道具教学工具：按地图查看常用丢法的站位、瞄点与落点。"
    override val unofficialNotice = "非官方声明"
    override val unofficialNoticeText = "本 App 为非官方作品，与 Valve 及相关商标持有者无任何关联。游戏地图与道具名称为描述用途而引用。"
    override val acknowledgements = "鸣谢"
    override val acknowledgementsText = "数据结构与交互参考开源项目 CSTacticsApp。"

    override val tacticalMap = "2D 战术地图"
    override val utilityList = "道具列表"
    override val favorites = "收藏"
    override val featureMapSubtitle = "在地图上查看所有道具点位"
    override val featureListSubtitle = "按区域和类型浏览道具"
    override val featureSearchSubtitle = "按名称或位置搜索"
    override val featureFavoritesSubtitle = "收藏的点位与丢法"

    override val overview = "概览"
    override val name = "名称"
    override val type = "类型"
    override val side = "阵营"
    override val category = "分类"
    override val difficulty = "难度"
    override val lineupVariants = "丢法"
    override val position = "位置"
    override val startArea = "起始位置"
    override val targetArea = "目标位置"
    override val spawnRequirement = "适用出生点 / 身位"
    override val lineupSteps = "投掷步骤"
    override val notes = "说明"
    override val teachingImages = "教学图片"
    override val startPosition = "站位图"
    override val aimPoint = "瞄点图"
    override val resultImage = "落点效果"
    override val placeholder = "暂无图片"

    override val addFavorite = "加入收藏"
    override val removeFavorite = "取消收藏"
    override val close = "关闭"
    override val favoriteGroups = "收藏的点位"
    override val favoriteVariants = "收藏的丢法"
    override val emptyFavorites = "还没有收藏"
    override val emptyFavoritesMessage = "在点位或丢法页面点右上角星标即可收藏。"
    override val emptyUtilities = "暂无道具"
    override val emptyUtilitiesMessage = "这张地图还没有录入道具点位。"
    override val groupSubtitle = "点位"
    override val variantSubtitle = "丢法"
    override val difficultyEasy = "简单"
    override val difficultyMedium = "中等"

    override val smoke = "烟"
    override val flash = "闪"
    override val molotov = "火"
    override val he = "雷"
    override val categoryASite = "A 包点"
    override val categoryBSite = "B 包点"
    override val categoryMid = "中路"
    override val categoryTSide = "T 方"
    override val categoryCTSide = "CT 方"

    override val areaFilter = "区域"
    override val utilityTypeFilter = "道具类型"
    override val filterFeatured = "推荐"
    override val filterAll = "全部"
    override val tapHint = "点击地图上的圆点查看丢法"
    override val developerHint = "开发者模式：拖动点位校正坐标，单指拖点、双指平移"
    override val clusteredUtilities = "道具"
    override val targetPoint = "目标"
    override val startPoint = "站位"
    override val targetPoints = "目标点"
    override val variantStartPoints = "站位点"
    override val lineConnections = "站位到目标连线"
    override val liveCoordinates = "实时坐标"
    override val lastEdited = "最近调整"
    override val noEditedCoordinate = "还没有调整点位。"
    override val copyCoordinates = "复制坐标"
    override val copyJson = "复制 JSON"
    override val coordinatesCopied = "坐标已复制。"
    override val jsonCopied = "JSON 已复制。"

    override val search = "搜索"
    override val searchPrompt = "搜索点位或丢法"
    override val searchHint = "输入地图、区域或道具名称开始搜索。"
    override val searchNoResults = "没有匹配结果"
    override val searchResultGroup = "点位"
    override val searchResultVariant = "丢法"

    override val developerMode = "开发者模式"

    override fun lineupCount(count: Int) = "$count 个点位"
    override fun variantCount(count: Int) = "$count 种丢法"
}

object EnStrings : Strings {
    override val appName = "CS Lineups"
    override val maps = "Maps"
    override val settings = "Settings"
    override val language = "Language"
    override val followSystem = "Follow system"
    override val simplifiedChinese = "简体中文"
    override val english = "English"
    override val about = "About"
    override val version = "Version"
    override val appIntro = "Counter-Strike utility lineups: browse positions, aim points and results per map."
    override val unofficialNotice = "Unofficial Notice"
    override val unofficialNoticeText = "This app is an unofficial project and is not affiliated with Valve or any trademark holder. Map and utility names are referenced for descriptive purposes."
    override val acknowledgements = "Acknowledgements"
    override val acknowledgementsText = "Data structure and interaction are based on the open source project CSTacticsApp."

    override val tacticalMap = "2D Tactical Map"
    override val utilityList = "Utility List"
    override val favorites = "Favorites"
    override val featureMapSubtitle = "See every utility point on the map"
    override val featureListSubtitle = "Browse utility by area and type"
    override val featureSearchSubtitle = "Search by name or area"
    override val featureFavoritesSubtitle = "Saved points and lineups"

    override val overview = "Overview"
    override val name = "Name"
    override val type = "Type"
    override val side = "Side"
    override val category = "Category"
    override val difficulty = "Difficulty"
    override val lineupVariants = "Lineup Variants"
    override val position = "Position"
    override val startArea = "Start Area"
    override val targetArea = "Target Area"
    override val spawnRequirement = "Spawn / Body Position"
    override val lineupSteps = "Lineup Steps"
    override val notes = "Notes"
    override val teachingImages = "Teaching Images"
    override val startPosition = "Start Position"
    override val aimPoint = "Aim Point"
    override val resultImage = "Result"
    override val placeholder = "No image yet"

    override val addFavorite = "Add to favorites"
    override val removeFavorite = "Remove from favorites"
    override val close = "Close"
    override val favoriteGroups = "Favorite Points"
    override val favoriteVariants = "Favorite Lineups"
    override val emptyFavorites = "No favorites yet"
    override val emptyFavoritesMessage = "Tap the star on a point or lineup page to save it here."
    override val emptyUtilities = "No utility yet"
    override val emptyUtilitiesMessage = "No utility points have been added for this map."
    override val groupSubtitle = "Point"
    override val variantSubtitle = "Lineup"
    override val difficultyEasy = "Easy"
    override val difficultyMedium = "Medium"

    override val smoke = "Smoke"
    override val flash = "Flash"
    override val molotov = "Molotov"
    override val he = "HE"
    override val categoryASite = "A Site"
    override val categoryBSite = "B Site"
    override val categoryMid = "Mid"
    override val categoryTSide = "T Side"
    override val categoryCTSide = "CT Side"

    override val areaFilter = "Area"
    override val utilityTypeFilter = "Utility Type"
    override val filterFeatured = "Featured"
    override val filterAll = "All"
    override val tapHint = "Tap a dot on the map to see the lineups"
    override val developerHint = "Developer mode: drag points to fix coordinates (one finger drags a point, two fingers pan)"
    override val clusteredUtilities = "Utilities"
    override val targetPoint = "Target"
    override val startPoint = "Start"
    override val targetPoints = "Target Points"
    override val variantStartPoints = "Variant Start Points"
    override val lineConnections = "Line Connections"
    override val liveCoordinates = "Live Coordinates"
    override val lastEdited = "Last Edited"
    override val noEditedCoordinate = "No edited coordinate yet."
    override val copyCoordinates = "Copy Coordinates"
    override val copyJson = "Copy JSON"
    override val coordinatesCopied = "Coordinates copied."
    override val jsonCopied = "JSON copied."

    override val search = "Search"
    override val searchPrompt = "Search points or lineups"
    override val searchHint = "Type a map, area or utility name to search."
    override val searchNoResults = "No results"
    override val searchResultGroup = "Point"
    override val searchResultVariant = "Lineup"

    override val developerMode = "Developer Mode"

    override fun lineupCount(count: Int) = "$count points"
    override fun variantCount(count: Int) = "$count lineups"
}
