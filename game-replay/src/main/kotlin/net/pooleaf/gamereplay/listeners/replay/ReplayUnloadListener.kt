package net.pooleaf.gamereplay.listeners.replay

import net.pooleaf.gamereplay.GameReplayApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class ReplayUnloadListener : Listener {

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        GameReplayApi.unsafe.replayManager.removeNotWatchingReplays()
    }

}