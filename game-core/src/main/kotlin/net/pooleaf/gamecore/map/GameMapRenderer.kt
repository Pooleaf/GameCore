package net.pooleaf.gamecore.map

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phases.WorldBorderUpdatePhase
import org.bukkit.entity.Player
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapCursor
import org.bukkit.map.MapCursorCollection
import org.bukkit.map.MapPalette
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import java.awt.Color

/**
 * 게임 지도 렌더러.
 *
 * - 베이스: [GameMapSnapshot] (게임 시작 시 1회 캡처된 정적 지형)
 * - 오버레이:
 *   1) 현재 자기장 사각형 (흰색)
 *   2) 다음 자기장 사각형 (빨강) — [WorldBorderUpdatePhase] 진행 중일 때만
 *   3) 본인 위치 화살표 (WHITE_POINTER)
 *   4) 같은 팀 플레이어 화살표 (GREEN_POINTER)
 *
 * 다른 팀의 위치/상태는 절대 그리지 않는다.
 */
class GameMapRenderer : MapRenderer(true) {

    companion object {
        @Suppress("DEPRECATION")
        private val COLOR_BORDER_CURRENT: Byte = MapPalette.matchColor(Color(0, 120, 255))

        @Suppress("DEPRECATION")
        private val COLOR_BORDER_NEXT: Byte = MapPalette.matchColor(Color(255, 255, 255))

        // 1.8.9 MapCursor.Type 인덱스 (Type enum 사용 시 호환 깨질 위험 있어 byte 직접 사용)
        private const val CURSOR_WHITE_POINTER: Byte = 0
        private const val CURSOR_GREEN_POINTER: Byte = 1
    }

    override fun render(map: MapView, canvas: MapCanvas, player: Player) {
        val snapshot = GameCore.unsafe.mapSnapshotService.currentSnapshot ?: run {
            clearCanvas(canvas)
            canvas.cursors = MapCursorCollection()
            return
        }

        // 1) 베이스 지형
        for (py in 0 until GameMapSnapshot.SIZE) {
            for (px in 0 until GameMapSnapshot.SIZE) {
                canvas.setPixel(px, py, snapshot.baseColors[py * GameMapSnapshot.SIZE + px])
            }
        }

        // 2) 현재 자기장 — vanilla worldBorder 의 실시간 크기 사용 (줄어드는 도중에도 부드럽게 반영)
        val currentMap = GameCore.currentMap
        if (currentMap != null) {
            val currentCenter = currentMap.currentWorldBorderCenterLocation
                ?: currentMap.centerLocation
            if (currentCenter != null) {
                val world = currentCenter.world
                val liveSize = world?.worldBorder?.size?.toInt() ?: currentMap.currentWorldBorderSize
                drawBorderRect(
                    canvas,
                    snapshot,
                    currentCenter.x,
                    currentCenter.z,
                    liveSize,
                    COLOR_BORDER_CURRENT
                )
            }

            // 3) 다음 자기장 (WorldBorderUpdatePhase 진행 중)
            // 다음 자기장도 실제 자기장 중심(currentWorldBorderCenterLocation)을 기준으로 그린다.
            // (현재 자기장과 동일한 중심을 사용해야 미리보기가 실제 줄어들 영역과 일치한다.)
            val currentPhase = GameCore.game.phasePipeline.currentPhase
            if (currentPhase is WorldBorderUpdatePhase) {
                val center = currentMap.currentWorldBorderCenterLocation ?: currentMap.centerLocation
                if (center != null) {
                    drawBorderRect(
                        canvas,
                        snapshot,
                        center.x,
                        center.z,
                        currentPhase.getNewWorldBorderSize(),
                        COLOR_BORDER_NEXT
                    )
                }
            }
        }

        // 4) 커서: 팀원 먼저, 본인 마지막 (본인 마커가 가려지지 않도록)
        val cursors = MapCursorCollection()

        // 팀원 화살표 (GREEN_POINTER) — 본인 제외
        val viewerGamePlayer = GameCore.unsafe.playerManager.get(player.uniqueId)
        val team = viewerGamePlayer?.team
        if (team != null) {
            for (teammate in team.players) {
                if (teammate.uuid == player.uniqueId) continue
                if (!teammate.isOnline) continue
                if (!teammate.isPlaying()) continue
                val teammatePlayer = teammate.player ?: continue
                addPlayerCursor(cursors, snapshot, teammatePlayer, CURSOR_GREEN_POINTER)
            }
        }

        // 본인 화살표 (WHITE_POINTER) — 마지막에 추가해서 항상 최상단
        addPlayerCursor(cursors, snapshot, player, CURSOR_WHITE_POINTER)

        canvas.cursors = cursors
    }

    private fun addPlayerCursor(
        cursors: MapCursorCollection,
        snapshot: GameMapSnapshot,
        target: Player,
        type: Byte
    ) {
        val location = target.location
        if (!snapshot.contains(location)) return

        // 픽셀(0..127) -> MapCursor 좌표(-128..126)
        val pixel = snapshot.toPixel(location) ?: return
        val cursorX = (pixel[0] * 2 - 128).toByte()
        val cursorY = (pixel[1] * 2 - 128).toByte()

        // yaw(-180..180) -> 16방향 (0=북, 4=동, 8=남, 12=서)
        val direction = (((location.yaw + 360f) % 360f) / 22.5f).toInt() and 0x0F

        @Suppress("DEPRECATION")
        cursors.addCursor(MapCursor(cursorX, cursorY, direction.toByte(), type, true))
    }

    private fun clearCanvas(canvas: MapCanvas) {
        for (py in 0 until GameMapSnapshot.SIZE) {
            for (px in 0 until GameMapSnapshot.SIZE) {
                canvas.setPixel(px, py, 0)
            }
        }
    }

    private fun drawBorderRect(
        canvas: MapCanvas,
        snapshot: GameMapSnapshot,
        centerWorldX: Double,
        centerWorldZ: Double,
        worldSize: Int,
        color: Byte
    ) {
        if (worldSize <= 0) return
        val half = worldSize / 2.0
        val minWorldX = centerWorldX - half
        val maxWorldX = centerWorldX + half
        val minWorldZ = centerWorldZ - half
        val maxWorldZ = centerWorldZ + half

        val p1 = snapshot.toPixelClamped(minWorldX, minWorldZ)
        val p2 = snapshot.toPixelClamped(maxWorldX, maxWorldZ)

        val xMin = minOf(p1[0], p2[0])
        val xMax = maxOf(p1[0], p2[0])
        val yMin = minOf(p1[1], p2[1])
        val yMax = maxOf(p1[1], p2[1])

        for (px in xMin..xMax) {
            if (yMin in 0 until GameMapSnapshot.SIZE) canvas.setPixel(px.clampPx(), yMin, color)
            if (yMax in 0 until GameMapSnapshot.SIZE) canvas.setPixel(px.clampPx(), yMax, color)
        }
        for (py in yMin..yMax) {
            if (xMin in 0 until GameMapSnapshot.SIZE) canvas.setPixel(xMin, py.clampPx(), color)
            if (xMax in 0 until GameMapSnapshot.SIZE) canvas.setPixel(xMax, py.clampPx(), color)
        }
    }

    private fun Int.clampPx(): Int =
        if (this < 0) 0 else if (this >= GameMapSnapshot.SIZE) GameMapSnapshot.SIZE - 1 else this
}

private fun GameMapSnapshot.toPixelClamped(worldX: Double, worldZ: Double): IntArray {
    val half = worldBorderSize / 2.0
    val relX = worldX - (centerX - half)
    val relZ = worldZ - (centerZ - half)
    var px = (relX / blocksPerPixel).toInt()
    var py = (relZ / blocksPerPixel).toInt()
    if (px < 0) px = 0
    if (px >= GameMapSnapshot.SIZE) px = GameMapSnapshot.SIZE - 1
    if (py < 0) py = 0
    if (py >= GameMapSnapshot.SIZE) py = GameMapSnapshot.SIZE - 1
    return intArrayOf(px, py)
}
