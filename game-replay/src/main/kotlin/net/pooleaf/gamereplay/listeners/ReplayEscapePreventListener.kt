package net.pooleaf.gamereplay.listeners

import net.pooleaf.gamereplay.GameReplayApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

class ReplayEscapePreventListener : Listener {

    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val player = event.player

        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(player.uniqueId)
        if (replayPlayer == null) return

        if (event.to.world.name != replayPlayer.replay.worldName) {
            if (event.from.world.name == replayPlayer.replay.worldName) {
                event.to = event.from
            } else {
                event.to = replayPlayer.replay.startLocation
            }
        }
    }

}