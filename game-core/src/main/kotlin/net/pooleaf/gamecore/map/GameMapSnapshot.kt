package net.pooleaf.gamecore.map

import org.bukkit.Location

/**
 * 게임 시작 시점에 1회 캡처된 정적 지도 스냅샷.
 *
 * 보안상 실시간 렌더링은 금지된다 (다른 플레이어 행동 노출 위험).
 * 모든 플레이어가 동일 인스턴스를 공유한다.
 */
class GameMapSnapshot(
    val baseColors: ByteArray,
    val worldName: String,
    val centerX: Double,
    val centerZ: Double,
    val worldBorderSize: Int
) {
    companion object {
        const val SIZE = 128
        const val PIXEL_COUNT = SIZE * SIZE
    }

    val blocksPerPixel: Double = worldBorderSize.toDouble() / SIZE

    /**
     * 월드 좌표가 스냅샷 영역 안에 있는지 확인한다.
     */
    fun contains(location: Location): Boolean {
        if (location.world?.name != worldName) return false
        val half = worldBorderSize / 2.0
        return location.x in (centerX - half)..(centerX + half) &&
                location.z in (centerZ - half)..(centerZ + half)
    }

    /**
     * 월드 좌표 -> 픽셀 좌표 (0..127, 0..127). 영역 밖이면 null.
     */
    fun toPixel(worldX: Double, worldZ: Double): IntArray? {
        val half = worldBorderSize / 2.0
        val relX = worldX - (centerX - half)
        val relZ = worldZ - (centerZ - half)
        val px = (relX / blocksPerPixel).toInt()
        val py = (relZ / blocksPerPixel).toInt()
        if (px !in 0 until SIZE) return null
        if (py !in 0 until SIZE) return null
        return intArrayOf(px, py)
    }

    fun toPixel(location: Location): IntArray? {
        if (location.world?.name != worldName) return null
        return toPixel(location.x, location.z)
    }

    /**
     * 픽셀 좌표 -> 월드 좌표 (픽셀 중심).
     */
    fun toWorldX(px: Int): Double {
        val half = worldBorderSize / 2.0
        return (centerX - half) + (px + 0.5) * blocksPerPixel
    }

    fun toWorldZ(py: Int): Double {
        val half = worldBorderSize / 2.0
        return (centerZ - half) + (py + 0.5) * blocksPerPixel
    }
}
