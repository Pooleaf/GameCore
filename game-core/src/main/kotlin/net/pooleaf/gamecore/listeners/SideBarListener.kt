package net.pooleaf.gamecore.listeners

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.player.GamePlayerJoinEvent
import net.pooleaf.gamecore.events.player.GamePlayerQuitEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class SideBarListener: Listener {

    @EventHandler
    fun onJoin(event: GamePlayerJoinEvent) {
        val gamePlayer = event.gamePlayer

        GameCore.unsafe.sideBarManager.sideBar?.let {
            it.update()
            it.setTo(gamePlayer)
        }
    }

    @EventHandler
    fun onQuit(event: GamePlayerQuitEvent) {
        val gamePlayer = event.gamePlayer

        GameCore.unsafe.sideBarManager.sideBar?.let {
            it.update()
            it.removeTo(gamePlayer)
        }
    }

}