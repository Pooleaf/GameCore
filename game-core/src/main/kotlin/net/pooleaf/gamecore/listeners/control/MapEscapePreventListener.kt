package net.pooleaf.gamecore.listeners.control

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.gamecore.GameCore
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent

class MapEscapePreventListener: Listener {

    private fun isTeleportedToMap(): Boolean {
        return GameCore.game.isTeleportedToMap;
    }

    private fun isInMap(location: Location): Boolean {
        return GameCore.currentMap?.isInMap(location) == true
    }


    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        BukkitSyncScope.launch {
            delay(400L)

            if (isTeleportedToMap() && !isInMap(event.player.location)) {
                GameCore.currentMap?.centerLocation?.let { TeleportUtil.teleport(event.player, it) }
            }
        }
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (isTeleportedToMap() && !isInMap(event.to)) {
            // 원래 위치가 맵 안이라면 원래 위치로
            if (isInMap(event.from)) {
                event.to = event.from
            }
            // 원래 위치가 맵 밖이라면 맵 중앙으로
            else {
                GameCore.currentMap?.centerLocation?.let { TeleportUtil.teleport(event.player, it) }
            }
        }
    }

    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent) {
        if (event.from != null && isTeleportedToMap() && !isInMap(event.to)) {
            // 원래 위치가 맵 안이라면 원래 위치로
            if (isInMap(event.from)) {
                event.to = event.from
            }
            // 원래 위치가 맵 밖이라면 맵 중앙으로
            else {
                GameCore.currentMap?.centerLocation?.let { TeleportUtil.teleport(event.player, it) }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onRespawn(event: PlayerRespawnEvent) {
        if (!isInMap(event.respawnLocation)) {
            GameCore.currentMap?.centerLocation?.let { event.respawnLocation = it }
        }
    }

}