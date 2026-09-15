package com.cslineups.app.data

import android.content.Context
import com.cslineups.app.model.CsMap
import com.cslineups.app.model.LineupCategory
import com.cslineups.app.model.LineupGroup
import com.cslineups.app.model.LineupVariant
import com.cslineups.app.model.LocalizedText
import com.cslineups.app.model.UtilityType
import org.json.JSONObject

/**
 * 内容数据与 iOS 版共用同一份 JSON（app/src/main/assets/lineups_mirage.json），
 * 字段结构未做任何改动。
 */
object LineupRepository {

    fun loadMirage(context: Context): CsMap {
        val raw = context.assets.open(ASSET_NAME).bufferedReader().use { it.readText() }
        return parseMap(JSONObject(raw))
    }

    private const val ASSET_NAME = "lineups_mirage.json"

    private fun parseMap(source: JSONObject): CsMap {
        val groups = source.getJSONArray("lineupGroups")
        return CsMap(
            id = source.getString("id"),
            name = parseLocalized(source.getJSONObject("name")),
            imageName = source.getString("imageName"),
            lineupGroups = (0 until groups.length()).map { index ->
                parseGroup(groups.getJSONObject(index))
            },
        )
    }

    private fun parseGroup(source: JSONObject): LineupGroup {
        val variants = source.getJSONArray("variants")
        return LineupGroup(
            id = source.getString("id"),
            mapId = source.getString("mapId"),
            targetName = parseLocalized(source.getJSONObject("targetName")),
            type = UtilityType.fromRaw(source.getString("type")),
            side = source.getString("side"),
            category = LineupCategory.fromRaw(source.getString("category")),
            targetMapX = source.getDouble("targetMapX"),
            targetMapY = source.getDouble("targetMapY"),
            isFeatured = source.optBoolean("isFeatured", false),
            variants = (0 until variants.length()).map { index ->
                parseVariant(variants.getJSONObject(index))
            },
        )
    }

    private fun parseVariant(source: JSONObject) = LineupVariant(
        id = source.getString("id"),
        name = parseLocalized(source.getJSONObject("name")),
        spawnRequirement = parseLocalized(source.getJSONObject("spawnRequirement")),
        startArea = parseLocalized(source.getJSONObject("startArea")),
        targetArea = parseLocalized(source.getJSONObject("targetArea")),
        throwMethod = parseLocalized(source.getJSONObject("throwMethod")),
        description = parseLocalized(source.getJSONObject("description")),
        difficulty = source.getString("difficulty"),
        startMapX = source.getDouble("startMapX"),
        startMapY = source.getDouble("startMapY"),
        targetMapX = source.getDouble("targetMapX"),
        targetMapY = source.getDouble("targetMapY"),
        positionImageName = source.getString("positionImageName"),
        aimImageName = source.getString("aimImageName"),
        resultImageName = source.getString("resultImageName"),
    )

    private fun parseLocalized(source: JSONObject) = LocalizedText(
        en = source.getString("en"),
        zhHans = source.getString("zhHans"),
    )
}

