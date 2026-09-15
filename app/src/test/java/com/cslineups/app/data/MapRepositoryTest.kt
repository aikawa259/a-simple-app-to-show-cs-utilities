package com.cslineups.app.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.File

/**
 * 地图与道具的本地存储：建、改、删、以及"关掉再打开还在不在"。
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class MapRepositoryTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val dataFile = File(context.filesDir, "maps.json")

    @Before
    fun cleanStorage() {
        dataFile.delete()
    }

    @Test
    fun `首次启动会写入示例地图`() = runBlocking {
        val repository = MapRepository(context)
        repository.load(useChinese = true)

        val maps = repository.maps.value
        assertEquals(1, maps.size)
        assertEquals("Mirage", maps.first().name)
        assertTrue(maps.first().isExample)
        assertEquals(6, maps.first().items.size)
        assertTrue("示例数据要落盘", dataFile.exists())
    }

    @Test
    fun `新建地图会带上用户填的名字并且能重新读回`() = runBlocking {
        val repository = MapRepository(context)
        repository.load(useChinese = true)
        val created = repository.createMap("  我的地图  ")

        val reopened = MapRepository(context)
        reopened.load(useChinese = true)

        val stored = reopened.maps.value.first { it.id == created.id }
        assertEquals("我的地图", stored.name)
        assertEquals(0, stored.items.size)
    }

    @Test
    fun `添加道具后能读回名称与投掷点`() = runBlocking {
        val repository = MapRepository(context)
        repository.load(useChinese = true)
        val map = repository.maps.value.first()

        val item = repository.addItem(map.id, "B 点烟", "B 包点")

        val reopened = MapRepository(context)
        reopened.load(useChinese = true)

        val stored = reopened.maps.value.first { it.id == map.id }.items.first { it.id == item.id }
        assertEquals("B 点烟", stored.name)
        assertEquals("B 包点", stored.targetArea)
        assertEquals("默认类型应为烟", com.cslineups.app.model.UtilityType.SMOKE, stored.type)
    }

    @Test
    fun `编辑道具会保存所有细节`() = runBlocking {
        val repository = MapRepository(context)
        repository.load(useChinese = true)
        val map = repository.maps.value.first()
        val item = repository.addItem(map.id, "A 点闪", "A 大坑")

        repository.updateItem(
            map.id,
            item.copy(
                name = "A 点闪（改）",
                side = "CT",
                category = com.cslineups.app.model.LineupCategory.A_SITE,
                difficulty = com.cslineups.app.model.Difficulty.HARD,
                type = com.cslineups.app.model.UtilityType.FLASH,
                startArea = "警家",
                throwSteps = "跳投",
                notes = "备注",
                positionImage = "img-a.jpg",
            ),
        )

        val reopened = MapRepository(context)
        reopened.load(useChinese = true)
        val stored = reopened.maps.value.first { it.id == map.id }.items.first { it.id == item.id }

        assertEquals("A 点闪（改）", stored.name)
        assertEquals("CT", stored.side)
        assertEquals(com.cslineups.app.model.LineupCategory.A_SITE, stored.category)
        assertEquals(com.cslineups.app.model.Difficulty.HARD, stored.difficulty)
        assertEquals(com.cslineups.app.model.UtilityType.FLASH, stored.type)
        assertEquals("警家", stored.startArea)
        assertEquals("跳投", stored.throwSteps)
        assertEquals("备注", stored.notes)
        assertEquals("img-a.jpg", stored.positionImage)
    }

    @Test
    fun `删除地图后不会在下次启动被恢复`() = runBlocking {
        val repository = MapRepository(context)
        repository.load(useChinese = true)
        repository.deleteMap(repository.maps.value.first().id)

        val reopened = MapRepository(context)
        reopened.load(useChinese = true)

        assertTrue("删空之后不应该再塞回示例地图", reopened.maps.value.isEmpty())
    }

    @Test
    fun `删除道具后列表里就没有它了`() = runBlocking {
        val repository = MapRepository(context)
        repository.load(useChinese = true)
        val map = repository.maps.value.first()
        val item = repository.addItem(map.id, "临时", "某处")

        repository.deleteItem(map.id, item.id)

        val reopened = MapRepository(context)
        reopened.load(useChinese = true)
        val stored = reopened.maps.value.first { it.id == map.id }
        assertNull(stored.items.firstOrNull { it.id == item.id })
    }

    @Test
    fun `重命名地图`() = runBlocking {
        val repository = MapRepository(context)
        repository.load(useChinese = true)
        val map = repository.maps.value.first()

        repository.renameMap(map.id, "Dust2")

        val reopened = MapRepository(context)
        reopened.load(useChinese = true)
        assertEquals("Dust2", reopened.maps.value.first { it.id == map.id }.name)
    }
}
