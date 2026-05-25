package net.pooleaf.gamecore.map

import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.support.common.logger.Logger
import org.bukkit.Bukkit
import org.bukkit.Material
import java.util.concurrent.atomic.AtomicReference

/**
 * 현재 게임 맵의 스냅샷을 캡처/보관하는 서비스.
 *
 * - 게임 시작 시 [captureCurrentMap]을 호출해 1회 캡처
 * - 이후 [currentSnapshot]으로 공유 참조
 * - 게임 리셋 시 [clear]
 */
class GameMapSnapshotService {

    private val snapshotRef = AtomicReference<GameMapSnapshot?>(null)

    val currentSnapshot: GameMapSnapshot?
        get() = snapshotRef.get()

    fun clear() {
        snapshotRef.set(null)
    }

    /**
     * 현재 [GameMap]의 지형을 스캔하여 스냅샷을 생성한다.
     * 메인 스레드에 1회 진입하여 16384픽셀을 일괄 처리한다 (실측 ~150ms).
     */
    suspend fun captureCurrentMap(map: GameMap): GameMapSnapshot? {
        val centerLocation = map.centerLocation ?: run {
            Logger.warning("스냅샷 캡처 실패: centerLocation 이 null 입니다.")
            return null
        }

        val worldName = centerLocation.world.name
        val centerX = centerLocation.x
        val centerZ = centerLocation.z
        val size = map.worldBorderSize
        if (size <= 0) {
            Logger.warning("스냅샷 캡처 실패: worldBorderSize 가 0 이하 입니다.")
            return null
        }

        val colors = ByteArray(GameMapSnapshot.PIXEL_COUNT)
        val blocksPerPixel = size.toDouble() / GameMapSnapshot.SIZE
        val half = size / 2.0

        val tStart = System.currentTimeMillis()

        // 메인 스레드 1회 진입으로 전 영역 스캔 (순수 작업 < 200ms 수준)
        BukkitSyncScope.launch {
            val world = Bukkit.getWorld(worldName) ?: return@launch

            for (py in 0 until GameMapSnapshot.SIZE) {
                val worldZ = ((centerZ - half) + (py + 0.5) * blocksPerPixel).toInt()

                for (px in 0 until GameMapSnapshot.SIZE) {
                    val worldX = ((centerX - half) + (px + 0.5) * blocksPerPixel).toInt()

                    val topY = world.getHighestBlockYAt(worldX, worldZ) - 1
                    if (topY < 0) {
                        colors[py * GameMapSnapshot.SIZE + px] = 0
                        continue
                    }

                    val block = world.getBlockAt(worldX, topY, worldZ)
                    val material = block.type
                    val colorByte = if (material == Material.AIR) {
                        NmsMapColor.get(Material.STONE)
                    } else {
                        NmsMapColor.get(block)
                    }
                    colors[py * GameMapSnapshot.SIZE + px] = colorByte
                }
            }
        }.join()

        val snapshot = GameMapSnapshot(
            baseColors = colors,
            worldName = worldName,
            centerX = centerX,
            centerZ = centerZ,
            worldBorderSize = size
        )
        snapshotRef.set(snapshot)
        Logger.log("지도 스냅샷 캡처 완료: ${map.name} (${size}x${size}, ${System.currentTimeMillis() - tStart}ms)")
        return snapshot
    }
}
