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

/** 外观模式：跟随系统 / 强制浅色 / 强制深色。 */
enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK;

    fun isDark(systemInDark: Boolean): Boolean = when (this) {
        SYSTEM -> systemInDark
        LIGHT -> false
        DARK -> true
    }
}

enum class UtilityType(val symbol: String) {
    SMOKE("S"),
    FLASH("F"),
    MOLOTOV("M"),
    HE("H");

    companion object {
        fun fromName(raw: String?): UtilityType =
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
        fun fromRaw(raw: String?): LineupCategory =
            entries.firstOrNull { it.raw.equals(raw, ignoreCase = true) } ?: MID
    }
}

enum class Difficulty(val raw: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard");

    companion object {
        fun fromName(raw: String?): Difficulty =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: MEDIUM
    }
}

/**
 * 一条道具。地图里直接就是道具列表，不再区分"点位 / 丢法"两层，
 * 这样每个道具的所有信息都在同一个编辑页里。
 */
data class LineupItem(
    val id: String,
    val name: String,
    val type: UtilityType = UtilityType.SMOKE,
    val side: String = "T",
    val category: LineupCategory = LineupCategory.MID,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val startArea: String = "",
    val targetArea: String = "",
    val throwSteps: String = "",
    val notes: String = "",
    val positionImage: String? = null,
    val aimImage: String? = null,
    val resultImage: String? = null,
)

data class CsMap(
    val id: String,
    val name: String,
    val items: List<LineupItem> = emptyList(),
    val isExample: Boolean = false,
)
