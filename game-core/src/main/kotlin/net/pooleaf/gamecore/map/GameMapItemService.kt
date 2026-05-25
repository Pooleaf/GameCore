package net.pooleaf.gamecore.map

import net.pooleaf.gamecore.GameCore
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import java.time.LocalDateTime
import java.util.HashMap

/**
 * 게임 지도 아이템 ItemStack 생성/지급 서비스.
 *
 * 게임당 1개의 [MapView]를 공유한다 (모든 플레이어가 같은 mapId).
 * 렌더러는 contextual=true이므로 본인 위치 등 플레이어별 차이는 [GameMapRenderer]가 처리한다.
 */
class GameMapItemService {

    companion object {
        private const val MIN_MAP_ID = 10000
        private const val MAX_MAP_ID = 30000
        private const val MAP_ID_INTERVAL_SECONDS = 10
        private const val MAP_ID_SLOTS_PER_DAY = 24 * 60 * 60 / MAP_ID_INTERVAL_SECONDS
        private const val MAP_ID_DAY_BUCKETS = (MAX_MAP_ID - MIN_MAP_ID + 1) / MAP_ID_SLOTS_PER_DAY
    }

    private var sharedView: MapView? = null
    private var sharedRenderer: GameMapRenderer? = null

    val displayName: String = "§f지도"

    /**
     * 공유 [MapView]를 준비한다. 이미 준비된 경우 그대로 반환한다.
     * 메인 스레드에서 호출해야 한다.
     */
    fun ensureSharedMapView(): MapView {
        sharedView?.let { return it }

        val centerWorld = GameCore.currentMap?.centerLocation?.world
            ?: Bukkit.getWorlds().firstOrNull()
            ?: error("사용 가능한 월드가 없습니다.")

        val view = createTimeBasedMapView(centerWorld)
        view.scale = MapView.Scale.NORMAL

        // 기존 vanilla 렌더러 제거 (지형 자동 그리기 + 다른 플레이어 커서 노출 차단)
        view.renderers.toList().forEach { existing: MapRenderer -> view.removeRenderer(existing) }

        val renderer = GameMapRenderer()
        view.addRenderer(renderer)

        sharedView = view
        sharedRenderer = renderer
        return view
    }

    /**
     * 공유 mapView를 폐기한다. 게임 리셋 시 호출.
     */
    fun disposeSharedMapView() {
        sharedView = null
        sharedRenderer = null
    }

    private fun createTimeBasedMapView(world: World): MapView {
        val primaryWorld = Bukkit.getWorlds().first() as CraftWorld
        var id = createUniqueMapId()
        while (Bukkit.getMap(id.toShort()) != null) {
            id++
            if (id > MAX_MAP_ID) {
                id = MIN_MAP_ID
            }
        }

        setMapIdCounter(primaryWorld, id - 1)
        return Bukkit.createMap(world)
    }

    @Suppress("UNCHECKED_CAST")
    private fun setMapIdCounter(world: CraftWorld, id: Int) {
        val countersField = world.handle.worldMaps.javaClass.getDeclaredField("d")
        countersField.isAccessible = true

        val counters = countersField.get(world.handle.worldMaps) as? MutableMap<String, Short>
            ?: HashMap<String, Short>().also { countersField.set(world.handle.worldMaps, it) }
        counters["map"] = id.toShort()
    }

    private fun createUniqueMapId(): Int {
        val now = LocalDateTime.now()
        val dayBucket = (now.dayOfYear - 1) % MAP_ID_DAY_BUCKETS
        val timeSlot = now.toLocalTime().toSecondOfDay() / MAP_ID_INTERVAL_SECONDS
        return MIN_MAP_ID + dayBucket * MAP_ID_SLOTS_PER_DAY + timeSlot
    }

    /**
     * 게임 지도 ItemStack을 생성한다.
     */
    fun createMapItem(): ItemStack {
        val view = ensureSharedMapView()
        val item = ItemStack(Material.MAP, 1, view.id)
        val meta = item.itemMeta
        if (meta != null) {
            meta.displayName = displayName
            meta.lore = buildLore()
            item.itemMeta = meta
        }
        return item
    }

    private fun buildLore(): List<String> {
        val isTeamGame = GameCore.teamConfig.playerCountPerTeam > 1
        val lore = mutableListOf(
            "§7현재 맵을 보여줍니다.",
            "",
            "§f흰색 화살표 §7- 내 위치"
        )
        if (isTeamGame) {
            lore += "§a초록 화살표 §7- 팀원 위치"
        }
        return lore
    }

    /**
     * 플레이어에게 지도 아이템을 지급한다.
     * 인벤토리가 꽉 차면 발 밑에 드랍한다.
     */
    fun give(player: Player) {
        val item = createMapItem()
        val leftover = player.inventory.addItem(item)
        if (leftover.isNotEmpty()) {
            leftover.values.forEach { remaining ->
                player.world.dropItem(player.location, remaining)
            }
        }
        player.updateInventory()
    }
}
