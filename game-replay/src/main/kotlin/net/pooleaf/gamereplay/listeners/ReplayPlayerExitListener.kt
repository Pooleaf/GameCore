package net.pooleaf.gamereplay.listeners

import net.pooleaf.gamereplay.GameReplayApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class ReplayPlayerExitListener : Listener {

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        if (!GameReplayApi.replayConfig.isReplayPlayServer) return

        val player = event.player
        if (!GameReplayApi.unsafe.replayService.isPlayingReplay(player)) return

        GameReplayApi.unsafe.replayService.exitReplay(player, false)
    }

}