package com.cslineups.app.model

/** App 内可选语言。SYSTEM 表示跟随系统。 */
enum class Lang {
    SYSTEM,
    ZH_HANS,
    EN;

    fun resolved(systemIsChinese: Boolean): Lang = when (this) {
        SYSTEM -> if (systemIsChinese) ZH_HANS else EN
        else -> this
    }
}

/** 内容数据里的双语文本。 */
data class LocalizedText(val en: String, val zhHans: String) {
    fun value(lang: Lang): String = if (lang == Lang.ZH_HANS) zhHans else en
}

enum class UtilityType(val symbol: String) {
    SMOKE("S"),
    FLASH("F"),
    MOLOTOV("M"),
    HE("H");

    companion object {
        fun fromRaw(raw: String): UtilityType =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: SMOKE
    }
}

enum class LineupCategory(val raw: String) {
    A_SITE("aSite"),
    B_SITE("bSite"),
    MID("mid"),
    T_SIDE("tSide"),
    CT_SIDE("ctSide");

    companion object {
        fun fromRaw(raw: String): LineupCategory =
            entries.firstOrNull { it.raw.equals(raw, ignoreCase = true) } ?: MID
    }
}

data class LineupVariant(
    val id: String,
    val name: LocalizedText,
    val spawnRequirement: LocalizedText,
    val startArea: LocalizedText,
    val targetArea: LocalizedText,
    val throwMethod: LocalizedText,
    val description: LocalizedText,
    val difficulty: String,
    val startMapX: Double,
    val startMapY: Double,
    val targetMapX: Double,
    val targetMapY: Double,
    val positionImageName: String,
    val aimImageName: String,
    val resultImageName: String,
)

data class LineupGroup(
    val id: String,
    val mapId: String,
    val targetName: LocalizedText,
    val type: UtilityType,
    val side: String,
    val category: LineupCategory,
    val targetMapX: Double,
    val targetMapY: Double,
    val isFeatured: Boolean,
    val variants: List<LineupVariant>,
)

data class CsMap(
    val id: String,
    val name: LocalizedText,
    val imageName: String,
    val lineupGroups: List<LineupGroup>,
)

