package net.pooleaf.gamecore.listeners

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.player.GamePlayerJoinEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class StartItemListener: Listener {

    @EventHandler
    fun onJoin(event: GamePlayerJoinEvent) {
        val gamePlayer = event.gamePlayer

        // 시작 아이템 미지급 시 지급
        if (GameCore.game.isGameStarted && gamePlayer.isPlaying() && !gamePlayer.isReceiveStartItems) {
            gamePlayer.giveStartItem()
        }
    }

}