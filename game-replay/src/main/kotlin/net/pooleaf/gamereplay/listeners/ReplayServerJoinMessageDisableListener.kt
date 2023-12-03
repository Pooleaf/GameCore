package net.pooleaf.gamereplay.listeners

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class ReplayServerJoinMessageDisableListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (Bukkit.getPluginManager().getPlugin("GameCore") != null) return

        event.joinMessage = null
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        if (Bukkit.getPluginManager().getPlugin("GameCore") != null) return

        event.quitMessage = null
    }

}