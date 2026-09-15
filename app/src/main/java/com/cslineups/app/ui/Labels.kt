package com.cslineups.app.ui

import com.cslineups.app.i18n.Strings
import com.cslineups.app.model.Difficulty
import com.cslineups.app.model.LineupCategory
import com.cslineups.app.model.UtilityType

fun LineupCategory.label(strings: Strings): String = when (this) {
    LineupCategory.A_SITE -> strings.categoryASite
    LineupCategory.B_SITE -> strings.categoryBSite
    LineupCategory.MID -> strings.categoryMid
    LineupCategory.T_SIDE -> strings.categoryTSide
    LineupCategory.CT_SIDE -> strings.categoryCTSide
}

fun UtilityType.label(strings: Strings): String = when (this) {
    UtilityType.SMOKE -> strings.smoke
    UtilityType.FLASH -> strings.flash
    UtilityType.MOLOTOV -> strings.molotov
    UtilityType.HE -> strings.he
}

fun Difficulty.label(strings: Strings): String = when (this) {
    Difficulty.EASY -> strings.difficultyEasy
    Difficulty.MEDIUM -> strings.difficultyMedium
    Difficulty.HARD -> strings.difficultyHard
}
