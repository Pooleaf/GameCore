package net.pooleaf.gamecore.map

import net.pooleaf.core.modules.annoconfig.common.anno.ConfigExclude
import net.pooleaf.core.modules.annoconfig.common.anno.ConfigName
import net.pooleaf.gamecore.GameCore
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import kotlin.random.Random

open class GameMap {

    @ConfigName("이름")
    lateinit var name: String

    @ConfigName("표기 이름")
    var displayName: String? = null
        get() = field ?: name
        set(value) {
            if (value == name) {
                field == null
            } else {
                field = value
            }
        }

    @ConfigName("중앙 위치.world")
    var centerWorldName: String? = null
        internal set

    @ConfigName("중앙 위치.x")
    var centerX: Double = 0.0
        internal set

    @ConfigName("중앙 위치.y")
    var centerY: Double = 0.0
        internal set

    @ConfigName("중앙 위치.z")
    var centerZ: Double = 0.0
        internal set

    @ConfigName("중앙 위치.yaw")
    var centerYaw: Float = 0f
        internal set

    @ConfigName("중앙 위치.pitch")
    var centerPitch: Float = 0f
        internal set

    // 경계선 범위 (지름)
    @ConfigName("경계선 범위")
    var worldBorderSize: Int = 0
        internal set

    // true일 경우 월드보더 사용, false일 경우 이벤트에서 이동만 차단
/*    @ConfigName("경계선.보이기")
    var showWorldBorder: Boolean = true
        internal set*/

    // 현재 맵 범위 (지름) (경계선 줄일 때 사용)
    @ConfigExclude
    var currentWorldBorderSize: Int = 0
        internal set

    /**
     * 맵 중앙 위치를 반환합니다.
     * 맵 중앙 위치가 설정되지 않았을 경우 null을 반환합니다.
     */
    var centerLocation: Location?
        get() {
            return centerWorldName?.let {
                val world = Bukkit.getWorld(it)
                world?.let { Location(world, centerX, centerY, centerZ, centerYaw, centerPitch) }
            }
        }
        set(value) {
            value?.let {
                centerWorldName = it.world.name
                centerX = it.x
                centerY = it.y
                centerZ = it.z
                centerYaw = it.yaw
                centerPitch = it.pitch
            }
        }

    /**
     * 사용 가능 맵 여부를 반환합니다.
     */
    val canUse
        get() = centerWorldName != null && worldBorderSize != null


    fun getCenterLocationString(): String {
        return "$centerWorldName, $centerX, $centerY, $centerZ, $centerYaw, $centerPitch"
    }

    /**
     * 해당 위치가 맵 안인지 여부를 반환합니다.
     */
    fun isInMap(location: Location): Boolean {
        return centerLocation?.let { centerLocation ->
            location.world.equals(centerLocation.world)
                    && Math.abs(centerLocation.x - location.x) <= worldBorderSize / 2
                    && Math.abs(centerLocation.z - location.z) <= worldBorderSize / 2
        } == true
    }

    /**
     * 해당 위치가 경계선 안인지 확인합니다.
     */
    fun isInWorldBorder(location: Location, worldBorderSize: Int = currentWorldBorderSize): Boolean {
        return centerLocation?.let { centerLocation ->
            location.world.equals(centerLocation.world)
                    && Math.abs(centerLocation.x - location.x) <= worldBorderSize / 2
                    && Math.abs(centerLocation.z - location.z) <= worldBorderSize / 2
        } == true
    }

    /**
     * 맵 내의 랜덤 위치를 불러옵니다.
     * [useCurrentWorldBorder]: true일 경우 현재 경계선 크기를 기준으로 하고 false일 경우 맵 최초 경계선 크기를 기준으로 합니다.
     * Primary Thread에서만 실행할 수 있습니다.
     */
    fun getRandomLocation(useCurrentWorldBorder: Boolean = false): Location? {
        if (!Bukkit.isPrimaryThread()) error("getRandomLocation() can only be used in primary thread")

        val referenceWorldBorderSize = if (useCurrentWorldBorder) currentWorldBorderSize else worldBorderSize

        return centerLocation?.let { centerLocation ->
            // 랜덤 x, z
            val x = ((centerLocation.x - referenceWorldBorderSize / 2) + Random.nextInt(referenceWorldBorderSize)).toInt()
            val z = ((centerLocation.z - referenceWorldBorderSize / 2) + Random.nextInt(referenceWorldBorderSize)).toInt()

            // 랜덤 위치에서 제일 높은 블럭 찾기
            val highestY = centerLocation.world.getHighestBlockYAt(x, z) - 1
            val block = centerLocation.world.getBlockAt(x, highestY, z)

            // 블럭이 없을 경우 다시 찾음
            when (block.type) {
                Material.AIR,
                Material.WATER,
                Material.STATIONARY_WATER,
                Material.LAVA,
                Material.STATIONARY_LAVA,
                Material.BEDROCK,
                Material.BARRIER -> return getRandomLocation(useCurrentWorldBorder)
                else -> {}
            }

            // 블럭보다 한 칸 높게 반환
            block.location.add(0.0, 1.0, 0.0)
        } ?: null
    }

    /**
     * 경계선을 초기화합니다.
     */
    fun initWorldBorder() {
        GameCore.unsafe.mapService.initWorldBorder(this)
    }

    /**
     * 경계선을 [newSize]로 즉시 업데이트합니다.
     */
    fun updateWorldBorder(newSize: Int) {
        GameCore.unsafe.mapService.updateWorldBorder(this, newSize)
    }

    /**
     * 경계선을 [newSize] 크기로 초당 [updateSizePerSeconds] 칸만큼 변화시킵니다.
     * 줄어드는 시간은 정수여야 하기 때문에 [updateSizePerSeconds]는 정확하게 반영되지 않을 수 있습니다.
     * 줄어드는 데 걸리는 시간을 반환합니다.
     */
    fun updateWorldBorder(newSize: Int, updateSizePerSeconds: Int): Int {
        return GameCore.unsafe.mapService.updateWorldBorder(this, newSize, updateSizePerSeconds)
    }

    /**
     * 월드 로드 여부를 반환합니다.
     */
    fun isWorldLoaded(): Boolean {
        return GameCore.unsafe.mapService.isWorldLoaded(this)
    }

    /**
     * 월드를 불러옵니다.
     */
    suspend fun loadWorld() {
        GameCore.unsafe.mapService.loadWorld(this)
    }

    /**
     * 월드를 언로드합니다.
     */
    suspend fun unloadWorld(): Boolean {
        return GameCore.unsafe.mapService.unloadWorld(this)
    }

    /**
     * 맵 설정을 불러옵니다.
     */
    fun loadMapConfig() {
        GameCore.unsafe.mapService.loadMapConfig(name)
    }

    /**
     * 맵 설정을 저장합니다.
     */
    fun saveMapConfig() {
        GameCore.unsafe.mapService.saveMapConfig(this)
    }

    /**
     * 맵 설정을 삭제합니다.
     */
    fun deleteMapConfig() {
        GameCore.unsafe.mapService.deleteMapConfig(name)
    }

}