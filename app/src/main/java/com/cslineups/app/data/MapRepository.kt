package com.cslineups.app.data

import android.content.Context
import com.cslineups.app.model.CsMap
import com.cslineups.app.model.Difficulty
import com.cslineups.app.model.LineupCategory
import com.cslineups.app.model.LineupItem
import com.cslineups.app.model.UtilityType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

/**
 * 地图与道具的本地存储：全部放在 filesDir/maps.json 里。
 * 首次启动会写入一个示例地图（Mirage），用户删掉所有地图后不会再被强行恢复。
 */
class MapRepository(private val context: Context) {

    private val file = File(context.filesDir, "maps.json")
    private val mutex = Mutex()
    private val _maps = MutableStateFlow<List<CsMap>>(emptyList())

    val maps: StateFlow<List<CsMap>> = _maps.asStateFlow()

    suspend fun load(useChinese: Boolean) = mutex.withLock {
        val loaded = if (file.exists()) readMaps() else null
        val result = loaded ?: seedMaps(useChinese)
        if (loaded == null) writeMaps(result)
        _maps.value = result
    }

    suspend fun createMap(name: String): CsMap = mutate { current ->
        val map = CsMap(id = newId("map"), name = name.trim())
        current + map
    }.let { updated -> updated.last() }

    suspend fun renameMap(mapId: String, name: String) = mutate { current ->
        current.map { if (it.id == mapId) it.copy(name = name.trim()) else it }
    }

    suspend fun deleteMap(mapId: String) = mutate { current ->
        current.filterNot { it.id == mapId }
    }

    suspend fun addItem(mapId: String, name: String, targetArea: String): LineupItem {
        val item = LineupItem(
            id = newId("item"),
            name = name.trim(),
            targetArea = targetArea.trim(),
        )
        mutate { current ->
            current.map { map ->
                if (map.id == mapId) map.copy(items = map.items + item) else map
            }
        }
        return item
    }

    suspend fun updateItem(mapId: String, item: LineupItem) = mutate { current ->
        current.map { map ->
            if (map.id != mapId) map
            else map.copy(items = map.items.map { if (it.id == item.id) item else it })
        }
    }

    suspend fun deleteItem(mapId: String, itemId: String) = mutate { current ->
        current.map { map ->
            if (map.id != mapId) map
            else map.copy(items = map.items.filterNot { it.id == itemId })
        }
    }

    private suspend fun mutate(block: (List<CsMap>) -> List<CsMap>): List<CsMap> =
        mutex.withLock {
            val updated = block(_maps.value)
            writeMaps(updated)
            _maps.value = updated
            updated
        }

    /* ---------------- 读写 ---------------- */

    private suspend fun readMaps(): List<CsMap>? = withContext(Dispatchers.IO) {
        runCatching {
            val array = JSONArray(file.readText())
            (0 until array.length()).map { parseMap(array.getJSONObject(it)) }
        }.getOrNull()
    }

    private suspend fun writeMaps(maps: List<CsMap>) = withContext(Dispatchers.IO) {
        val array = JSONArray()
        maps.forEach { array.put(it.toJson()) }
        val temp = File(context.filesDir, "maps.json.tmp")
        temp.writeText(array.toString(2))
        if (file.exists()) file.delete()
        temp.renameTo(file)
    }

    private fun parseMap(source: JSONObject): CsMap {
        val items = source.optJSONArray("items") ?: JSONArray()
        return CsMap(
            id = source.getString("id"),
            name = source.optString("name"),
            isExample = source.optBoolean("isExample", false),
            items = (0 until items.length()).map { parseItem(items.getJSONObject(it)) },
        )
    }

    private fun parseItem(source: JSONObject) = LineupItem(
        id = source.getString("id"),
        name = source.optString("name"),
        type = UtilityType.fromName(source.optString("type")),
        side = source.optString("side").ifEmpty { "T" },
        category = LineupCategory.fromRaw(source.optString("category")),
        difficulty = Difficulty.fromName(source.optString("difficulty")),
        startArea = source.optString("startArea"),
        targetArea = source.optString("targetArea"),
        throwSteps = source.optString("throwSteps"),
        notes = source.optString("notes"),
        positionImage = source.optString("positionImage").ifEmpty { null },
        aimImage = source.optString("aimImage").ifEmpty { null },
        resultImage = source.optString("resultImage").ifEmpty { null },
    )

    private fun CsMap.toJson() = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("isExample", isExample)
        put("items", JSONArray().apply { items.forEach { put(it.toJson()) } })
    }

    private fun LineupItem.toJson() = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("type", type.name)
        put("side", side)
        put("category", category.raw)
        put("difficulty", difficulty.name)
        put("startArea", startArea)
        put("targetArea", targetArea)
        put("throwSteps", throwSteps)
        put("notes", notes)
        positionImage?.let { put("positionImage", it) }
        aimImage?.let { put("aimImage", it) }
        resultImage?.let { put("resultImage", it) }
    }

    private fun newId(prefix: String) = "$prefix-${UUID.randomUUID()}"

    /* ---------------- 首次启动的示例数据 ---------------- */

    private fun seedMaps(useChinese: Boolean): List<CsMap> {
        fun t(zh: String, en: String) = if (useChinese) zh else en

        val items = listOf(
            LineupItem(
                id = "example-window-standard",
                name = t("VIP 烟 · 匪家稳定丢法", "Window Smoke · Standard T Spawn"),
                startArea = t("匪家", "T Spawn"),
                targetArea = t("VIP", "Window"),
                throwSteps = t(
                    "站位：匪家。瞄点：屋顶参考点。投掷：跳投。",
                    "Position: T Spawn. Aim: roof reference. Throw: jump throw.",
                ),
                notes = t("封中路窗口，方便拿中路控制。", "Blocks Mid Window for mid control."),
            ),
            LineupItem(
                id = "example-window-fast",
                name = t("VIP 烟 · 靠左出生快丢", "Window Smoke · Left Spawn Fast Throw"),
                startArea = t("匪家左侧", "Left T Spawn"),
                targetArea = t("VIP", "Window"),
                throwSteps = t(
                    "站位：匪家左侧。瞄点：屋檐边。投掷：跳投。",
                    "Position: left T spawn. Aim: roof edge. Throw: jump throw.",
                ),
                notes = t("更快封 VIP，方便中路前压。", "A faster Window Smoke option for mid pressure."),
            ),
            LineupItem(
                id = "example-ct-tspawn",
                name = t("警家烟 · 匪家丢法", "CT Smoke · T Spawn Lineup"),
                category = LineupCategory.A_SITE,
                difficulty = Difficulty.EASY,
                startArea = t("匪家", "T Spawn"),
                targetArea = t("警家", "CT"),
                throwSteps = t(
                    "站位：匪家。瞄点：墙体参考点。投掷：跳投。",
                    "Position: T Spawn. Aim: wall reference. Throw: jump throw.",
                ),
                notes = t("封警家，帮助进攻 A 包点。", "Blocks CT for an A site execute."),
            ),
            LineupItem(
                id = "example-ct-ramp",
                name = t("警家烟 · A Ramp 丢法", "CT Smoke · A Ramp Lineup"),
                category = LineupCategory.A_SITE,
                startArea = "A Ramp",
                targetArea = t("警家", "CT"),
                throwSteps = t(
                    "站位：A Ramp。瞄点：墙体参考点。投掷：跳投。",
                    "Position: A Ramp. Aim: wall reference. Throw: jump throw.",
                ),
                notes = t("A Ramp 进攻时用于封警家。", "A Ramp option for blocking CT during an A execute."),
            ),
            LineupItem(
                id = "example-jungle-ramp",
                name = t("Jungle 烟 · A Ramp 丢法", "Jungle Smoke · A Ramp Lineup"),
                category = LineupCategory.A_SITE,
                startArea = "A Ramp",
                targetArea = "Jungle",
                throwSteps = t(
                    "站位：A Ramp。瞄点：上方墙体参考。投掷：跳投。",
                    "Position: A Ramp. Aim: top wall reference. Throw: jump throw.",
                ),
                notes = t("封 Jungle，减少 A 包点交叉火力。", "Blocks Jungle to reduce crossfire on A site."),
            ),
            LineupItem(
                id = "example-jungle-palace",
                name = t("Jungle 烟 · 二楼丢法", "Jungle Smoke · Palace Lineup"),
                category = LineupCategory.A_SITE,
                startArea = t("A 二楼", "Palace"),
                targetArea = "Jungle",
                throwSteps = t(
                    "站位：A 二楼。瞄点：上方墙体参考。投掷：跳投。",
                    "Position: Palace. Aim: top wall reference. Throw: jump throw.",
                ),
                notes = t("封 Jungle，配合 A 二楼或夹 A 进攻。", "Blocks Jungle for a Palace or A split execute."),
            ),
        )

        return listOf(
            CsMap(
                id = "example-mirage",
                name = "Mirage",
                items = items,
                isExample = true,
            ),
        )
    }
}
