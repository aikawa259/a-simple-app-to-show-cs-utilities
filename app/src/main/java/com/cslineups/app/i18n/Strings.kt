package com.cslineups.app.i18n

import com.cslineups.app.model.Lang

fun stringsFor(lang: Lang): Strings = if (lang == Lang.ZH_HANS) ZhStrings else EnStrings

/** 界面文案。用户自己填的内容（地图名、道具名等）不在这里。 */
interface Strings {
    val appName: String
    val settings: String
    val language: String
    val theme: String
    val themeSystem: String
    val themeLight: String
    val themeDark: String
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

    val utilityList: String
    val search: String
    val favorites: String
    val listSubtitle: String
    val searchSubtitle: String
    val favoritesSubtitle: String

    val maps: String
    val exampleMap: String
    val newMap: String
    val mapName: String
    val renameMap: String
    val deleteMap: String

    val addItem: String
    val itemName: String
    val targetPoint: String
    val createAndEdit: String
    val itemDetails: String
    val editItem: String

    val overview: String
    val name: String
    val type: String
    val side: String
    val category: String
    val difficulty: String
    val startArea: String
    val targetArea: String
    val throwSteps: String
    val notes: String

    val teachingImages: String
    val positionImage: String
    val aimImage: String
    val resultImage: String
    val chooseImage: String
    val removeImage: String
    val noImage: String

    val save: String
    val cancel: String
    val delete: String
    val rename: String
    val create: String
    val close: String

    val difficultyEasy: String
    val difficultyMedium: String
    val difficultyHard: String
    val smoke: String
    val flash: String
    val molotov: String
    val he: String
    val categoryASite: String
    val categoryBSite: String
    val categoryMid: String
    val categoryTSide: String
    val categoryCTSide: String

    val emptyItems: String
    val emptyItemsMessage: String
    val emptyMaps: String
    val emptyMapsMessage: String
    val emptyFavorites: String
    val emptyFavoritesMessage: String
    val emptySearch: String
    val emptySearchHint: String

    val searchPrompt: String
    val searchNoResults: String
    val addFavorite: String
    val removeFavorite: String

    val deleteConfirmTitle: String
    val deleteItemConfirm: String
    val deleteMapConfirm: String
    val autosaveHint: String
    val emptyValue: String

    fun itemCount(count: Int): String
}

object ZhStrings : Strings {
    override val appName = "CS Utility"
    override val settings = "设置"
    override val language = "语言"
    override val theme = "深色模式"
    override val themeSystem = "跟随系统"
    override val themeLight = "浅色"
    override val themeDark = "深色"
    override val followSystem = "跟随系统"
    override val simplifiedChinese = "简体中文"
    override val english = "English"
    override val about = "关于"
    override val version = "版本号"
    override val appIntro = "自己整理的道具本：为每张地图记录道具的站位、瞄点与落点，随时查阅。"
    override val unofficialNotice = "非官方声明"
    override val unofficialNoticeText = "本 App 为非官方作品，与 Valve 及相关商标持有者无任何关联。地图与道具名称仅为描述用途。"
    override val acknowledgements = "鸣谢"
    override val acknowledgementsText = "初版数据结构与界面风格参考开源项目 CSTacticsApp。"

    override val utilityList = "道具列表"
    override val search = "搜索"
    override val favorites = "收藏"
    override val listSubtitle = "查看这张地图上的全部道具"
    override val searchSubtitle = "按名称、位置或类型查找"
    override val favoritesSubtitle = "收藏起来的道具"

    override val maps = "全部地图"
    override val exampleMap = "示例"
    override val newMap = "新建地图"
    override val mapName = "地图名称"
    override val renameMap = "重命名"
    override val deleteMap = "删除地图"

    override val addItem = "添加道具"
    override val itemName = "道具名称"
    override val targetPoint = "投掷点"
    override val createAndEdit = "创建并编辑"
    override val itemDetails = "道具详情"
    override val editItem = "编辑"

    override val overview = "概览"
    override val name = "名称"
    override val type = "类型"
    override val side = "阵营"
    override val category = "分类"
    override val difficulty = "难度"
    override val startArea = "起始位置"
    override val targetArea = "目标位置"
    override val throwSteps = "投掷步骤"
    override val notes = "说明"

    override val teachingImages = "教学图片"
    override val positionImage = "站位图"
    override val aimImage = "瞄点图"
    override val resultImage = "效果图"
    override val chooseImage = "选择图片"
    override val removeImage = "移除图片"
    override val noImage = "未选择图片"

    override val save = "保存"
    override val cancel = "取消"
    override val delete = "删除"
    override val rename = "重命名"
    override val create = "创建"
    override val close = "关闭"

    override val difficultyEasy = "简单"
    override val difficultyMedium = "中等"
    override val difficultyHard = "困难"
    override val smoke = "烟"
    override val flash = "闪"
    override val molotov = "火"
    override val he = "雷"
    override val categoryASite = "A 包点"
    override val categoryBSite = "B 包点"
    override val categoryMid = "中路"
    override val categoryTSide = "T 方"
    override val categoryCTSide = "CT 方"

    override val emptyItems = "还没有道具"
    override val emptyItemsMessage = "点右下角的加号，输入道具名和投掷点就能添加。"
    override val emptyMaps = "还没有地图"
    override val emptyMapsMessage = "点左上角的加号创建第一张地图。"
    override val emptyFavorites = "还没有收藏"
    override val emptyFavoritesMessage = "在道具详情页点右上角星标即可收藏。"
    override val emptySearch = "没有匹配结果"
    override val emptySearchHint = "输入道具名、位置或类型开始搜索。"

    override val searchPrompt = "搜索道具"
    override val searchNoResults = "没有匹配结果"
    override val addFavorite = "加入收藏"
    override val removeFavorite = "取消收藏"

    override val deleteConfirmTitle = "确认删除"
    override val deleteItemConfirm = "删除后无法恢复，确定删除这个道具吗？"
    override val deleteMapConfirm = "地图里的道具会一起删除，且无法恢复。确定删除吗？"
    override val autosaveHint = "改动会自动保存"
    override val emptyValue = "未填写"

    override fun itemCount(count: Int) = "$count 个道具"
}

object EnStrings : Strings {
    override val appName = "CS Utility"
    override val settings = "Settings"
    override val language = "Language"
    override val theme = "Dark mode"
    override val themeSystem = "System"
    override val themeLight = "Light"
    override val themeDark = "Dark"
    override val followSystem = "Follow system"
    override val simplifiedChinese = "简体中文"
    override val english = "English"
    override val about = "About"
    override val version = "Version"
    override val appIntro = "Your own lineup notebook: keep positions, aim points and results for every map you play."
    override val unofficialNotice = "Unofficial Notice"
    override val unofficialNoticeText = "This app is an unofficial project and is not affiliated with Valve or any trademark holder. Map and utility names are descriptive only."
    override val acknowledgements = "Acknowledgements"
    override val acknowledgementsText = "The first version's data structure and interface style were based on the open source project CSTacticsApp."

    override val utilityList = "Utility List"
    override val search = "Search"
    override val favorites = "Favorites"
    override val listSubtitle = "Every utility saved on this map"
    override val searchSubtitle = "Find by name, area or type"
    override val favoritesSubtitle = "Your saved utilities"

    override val maps = "All Maps"
    override val exampleMap = "Example"
    override val newMap = "New Map"
    override val mapName = "Map name"
    override val renameMap = "Rename"
    override val deleteMap = "Delete map"

    override val addItem = "Add Utility"
    override val itemName = "Utility name"
    override val targetPoint = "Landing spot"
    override val createAndEdit = "Create and edit"
    override val itemDetails = "Utility Details"
    override val editItem = "Edit"

    override val overview = "Overview"
    override val name = "Name"
    override val type = "Type"
    override val side = "Side"
    override val category = "Category"
    override val difficulty = "Difficulty"
    override val startArea = "Start area"
    override val targetArea = "Target area"
    override val throwSteps = "Throw steps"
    override val notes = "Notes"

    override val teachingImages = "Teaching Images"
    override val positionImage = "Position"
    override val aimImage = "Aim point"
    override val resultImage = "Result"
    override val chooseImage = "Choose image"
    override val removeImage = "Remove image"
    override val noImage = "No image yet"

    override val save = "Save"
    override val cancel = "Cancel"
    override val delete = "Delete"
    override val rename = "Rename"
    override val create = "Create"
    override val close = "Close"

    override val difficultyEasy = "Easy"
    override val difficultyMedium = "Medium"
    override val difficultyHard = "Hard"
    override val smoke = "Smoke"
    override val flash = "Flash"
    override val molotov = "Molotov"
    override val he = "HE"
    override val categoryASite = "A Site"
    override val categoryBSite = "B Site"
    override val categoryMid = "Mid"
    override val categoryTSide = "T Side"
    override val categoryCTSide = "CT Side"

    override val emptyItems = "No utilities yet"
    override val emptyItemsMessage = "Tap the plus button to add a name and its landing spot."
    override val emptyMaps = "No maps yet"
    override val emptyMapsMessage = "Tap the plus button in the top left to create your first map."
    override val emptyFavorites = "No favorites yet"
    override val emptyFavoritesMessage = "Tap the star on a utility page to save it here."
    override val emptySearch = "No results"
    override val emptySearchHint = "Type a name, area or utility type to search."

    override val searchPrompt = "Search utilities"
    override val searchNoResults = "No results"
    override val addFavorite = "Add to favorites"
    override val removeFavorite = "Remove from favorites"

    override val deleteConfirmTitle = "Delete?"
    override val deleteItemConfirm = "This cannot be undone. Delete this utility?"
    override val deleteMapConfirm = "The utilities on this map will be deleted too. This cannot be undone."
    override val autosaveHint = "Changes are saved automatically"
    override val emptyValue = "Not set"

    override fun itemCount(count: Int) = "$count utilities"
}
