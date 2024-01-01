package net.pooleaf.gamecore.map

import com.grinderwolf.swm.api.SlimePlugin
import com.grinderwolf.swm.api.world.properties.SlimeProperties
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap
import kotlinx.coroutines.async
import net.pooleaf.core.modules.annoconfig.AnnoConfigModule
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.game.GameWorldBorderChangeEvent
import org.bukkit.Bukkit
import java.io.File
import kotlin.math.abs

class GameMapService {

    // 맵 설정 폴더
    private val mapConfigFolder: File by lazy {
        File(GameCore.gamePlugin.dataFolder, "map")
    }


    /**
     * 맵 설정을 저장합니다.
     */
    fun saveMapConfig(map: GameMap) {
        mapConfigFolder.mkdirs()

        val mapFile = File(mapConfigFolder, "${map.name}.yml")
        AnnoConfigModule.save(mapFile, map)
    }

    /**
     * 모든 맵 설정을 저장합니다.
     */
    fun saveMapConfigs() {
        GameCore.unsafe.mapManager.values().forEach { saveMapConfig(it) }
    }

    /**
     * 맵 설정을 삭제합니다.
     */
    fun deleteMapConfig(mapName: String) {
        val mapFile = File(mapConfigFolder, "${mapName}.yml")

        if (!mapFile.exists()) {
            error("map file ${mapFile.name} not exists")
        }

        mapFile.delete()
    }

    /**
     * 모든 맵 설정을 삭제합니다.
     */
    fun deleteMapConfigs() {
        GameCore.unsafe.mapManager.values().forEach { deleteMapConfig(it.name) }
    }

    /**
     * 맵 설정을 불러옵니다.
     */
    fun loadMapConfig(mapName: String) {
        val map = GameCore.unsafe.mapManager.get(mapName) ?: GameCore.unsafe.mapManager.gameMapFactory.createGameMap()

        val mapFile = File(mapConfigFolder, "${mapName}.yml")
        try {
            AnnoConfigModule.load(mapFile, map)
        } catch (exception: Exception) {
            Logger.warning("${mapFile.name} 맵을 불러올 수 없습니다.")
            exception.printStackTrace()
        }

        GameCore.unsafe.mapManager.set(mapName, map)

        GameCore.unsafe.mapVoteManager.mapVoteGui.updateAsynchronously()
    }

    /**
     * 모든 맵 설정을 불러옵니다.
     */
    fun loadMapConfigs() {
        mapConfigFolder.listFiles().forEach { file ->
            if (!file.name.endsWith(".yml")) {
                return@forEach
            }

            val mapName = file.name.substring(0, file.name.indexOf(".yml"))
            loadMapConfig(mapName)
        }
    }

    /**
     * 모든 맵 설정을 다시 불러옵니다.
     * 기존 객체에 그대로 불러오며, 파일이 존재하지 않을 경우 객체를 삭제합니다.
     */
    fun reloadMapConfigs() {
        loadMapConfigs()

        val fileList = mapConfigFolder.listFiles()

        GameCore.unsafe.mapManager.values().iterator().forEach { map ->
            val mapFile = File(mapConfigFolder, "${map.name}.yml")
            if (fileList.contains(mapFile)) {
                return@forEach
            }

            GameCore.unsafe.mapManager.remove(map.name)
        }
    }

    /**
     * 경계선을 초기화합니다.
     */
    fun initWorldBorder(map: GameMap) {
        map.currentWorldBorderSize = map.worldBorderSize
        updateWorldBorder(map, map.currentWorldBorderSize)
    }

    /**
     * 경계선을 [newSize]로 즉시 업데이트합니다.
     */
    fun updateWorldBorder(map: GameMap, newSize: Int) {
        map.centerLocation?.let { centerLocation ->
            val beforeSize = map.currentWorldBorderSize

            val worldBorder = centerLocation.world.worldBorder
            worldBorder.center = centerLocation
            worldBorder.size = newSize.toDouble()
            worldBorder.damageBuffer = 0.0

            map.currentWorldBorderSize = newSize

            // 이벤트
            Bukkit.getPluginManager().callEvent(GameWorldBorderChangeEvent(centerLocation, beforeSize, newSize, 0))
        } ?: error("centerLocation cannot be null")
    }

    /**
     * 경계선을 [newSize] 크기로 초당 [updateSizePerSeconds] 칸만큼 변화시킵니다.
     * 줄어드는 데 걸리는 시간을 반환합니다.
     */
    fun updateWorldBorder(map: GameMap, newSize: Int, updateSizePerSeconds: Int): Int {
        if (newSize < 0) error("newSize cannot be less than 0 (value: ${newSize})")
        if (updateSizePerSeconds < 1) error("updateSizePerSeconds cannot be less than 1 (value: ${updateSizePerSeconds}")

        map.centerLocation?.let { centerLocation ->
            val beforeSize = map.currentWorldBorderSize

            // 줄어드는 데 걸리는 시간
            val updateDurationSeconds = (abs(map.currentWorldBorderSize - newSize) / updateSizePerSeconds).toInt()

            // 경계선 설정
            val worldBorder = centerLocation.world.worldBorder
            worldBorder.center = centerLocation
            worldBorder.setSize(newSize.toDouble(), updateDurationSeconds.toLong())
            worldBorder.damageBuffer = 0.0

            map.currentWorldBorderSize = newSize

            // 이벤트
            Bukkit.getPluginManager().callEvent(GameWorldBorderChangeEvent(centerLocation, beforeSize, newSize, updateDurationSeconds))

            return updateDurationSeconds
        } ?: error("centerLocation cannot be null")
    }

    private fun checkSwmEnabled() {
        if (Bukkit.getPluginManager().getPlugin("SlimeWorldManager")?.let { it.isEnabled } == false) {
            error("SlimeWorldManager not found")
        }
    }

    /**
     * 월드 로드 여부를 반환합니다.
     */
    fun isWorldLoaded(map: GameMap): Boolean {
        return Bukkit.getWorld(map.centerWorldName) != null
    }

    /**
     * 월드를 불러옵니다.
     */
    suspend fun loadWorld(map: GameMap) {
        checkSwmEnabled()

        // 기본 월드 제외
        if (map.centerWorldName == "world") return

        // 로딩 체크
        if (isWorldLoaded(map)) return

        // 월드 불러오기
        val swmPlugin = Bukkit.getPluginManager().getPlugin("SlimeWorldManager") as SlimePlugin

        val sqlLoader = swmPlugin.getLoader("mysql")

        val properties = SlimePropertyMap()
        properties.setInt(SlimeProperties.SPAWN_X, map.centerX.toInt())
        properties.setInt(SlimeProperties.SPAWN_Y, map.centerY.toInt())
        properties.setInt(SlimeProperties.SPAWN_Z, map.centerZ.toInt())
        properties.setString(SlimeProperties.DIFFICULTY, "easy")
        properties.setBoolean(SlimeProperties.ALLOW_MONSTERS, false)
        properties.setBoolean(SlimeProperties.ALLOW_ANIMALS, false)
        properties.setBoolean(SlimeProperties.PVP, true)
        properties.setString(SlimeProperties.ENVIRONMENT, "NORMAL")
        properties.setString(SlimeProperties.WORLD_TYPE, "default")

        val slimeWorld = swmPlugin.loadWorld(sqlLoader, map.centerWorldName, true, properties)

        BukkitSyncScope.async {
            swmPlugin.generateWorld(slimeWorld)
            initWorldBorder(map)
        }.await()
    }

    /**
     * 월드를 언로드합니다.
     */
    suspend fun unloadWorld(map: GameMap): Boolean {
        checkSwmEnabled()

        // 기본 월드 제외
        if (map.centerWorldName == "world") return true

        // 로딩 체크
        if (!isWorldLoaded(map)) return true

        return BukkitSyncScope.async {
            if (Bukkit.unloadWorld(map.centerWorldName, false)) {
                Logger.log("${map.centerWorldName} 월드가 언로드 되었습니다.")
                return@async true
            } else {
                Logger.warning("${map.centerWorldName} 월드 언로드에 실패했습니다.")
                return@async false
            }
        }.await()
    }

}