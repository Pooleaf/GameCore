package net.pooleaf.gamecore.listeners

import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.gamecore.GameCore
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class QuitDeathListener : Listener {

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        if (!GameCore.game.isGameStarted || GameCore.game.isEnded) return

        val player = event.player
        val gamePlayer = GameCore.unsafe.playerManager.get(player.uniqueId) ?: return
        if (!gamePlayer.isPlaying()) return

        // 체력이 낮은 상태로 퇴장 시 사망
        if (player.health <= GameCore.gameConfig.quitDeathHealth) {
            player.damage(1000.0)
            BukkitBroadcaster.broadcast("${gamePlayer.displayName} §c님께서 낮은 체력으로 퇴장하여 사망 처리되었습니다.")
        }
    }

}