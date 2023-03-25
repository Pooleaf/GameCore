package net.pooleaf.gamereplay.listeners.replay

import net.pooleaf.gamereplay.GameReplayApi
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent

class ReplayViewerMapEscapePreventListener : Listener {

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val toLocation = getToLocation(event.player, event.from, event.to)
        toLocation?.let { event.to = it }
    }

    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val toLocation = getToLocation(event.player, event.from, event.to)
        toLocation?.let { event.to = it }
    }


    /**
     * 맵 밖으로 이동할 경우 올바른 위치를 반환합니다.
     * 맵 안에서 이동할 경우 null을 반환합니다.
     */
    private fun getToLocation(
        player: Player,
        fromLocation: Location,
        toLocation: Location
    ): Location? {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(player.uniqueId) ?: return null

        val centerLocation = replayPlayer.replay.startLocation
        val worldBorderSize = replayPlayer.replay.worldBorderSize

        // 크기 지정 안돼있으면 무한
        if (worldBorderSize == 0) return null

        if (!isInWorldBorder(centerLocation, toLocation, worldBorderSize)) {
            if (isInWorldBorder(centerLocation, fromLocation, worldBorderSize)) {
                return fromLocation
            } else {
                return centerLocation
            }
        }

        return null
    }

    /**
     * 해당 위치가 경계선 안인지 여부를 반환합니다.
     */
    private fun isInWorldBorder(centerLocation: Location, location: Location, worldBorderSize: Int): Boolean {
        return location.world.equals(centerLocation.world)
                && Math.abs(centerLocation.x - location.x) <= worldBorderSize / 2
                && Math.abs(centerLocation.z - location.z) <= worldBorderSize / 2
                && 0 <= location.y  && location.y <= 260
    }

}