package net.pooleaf.gamecore.listeners

import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.player.GamePlayerJoinEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class JoinTeleportListener: Listener {

    @EventHandler
    fun onPlayerJoin(event: GamePlayerJoinEvent) {
        val gamePlayer = event.gamePlayer
        val player = gamePlayer.player

        // 대기 중
        if (!GameCore.game.isGameStarted) {
            GameCore.spawnConfig.spawnLocation?.let { TeleportUtil.teleport(player, it) }
        }
        // 게임 시작 후
        else if (GameCore.game.isTeleportedToMap) {
            // 관전자 맵 텔레포트
            if (!gamePlayer.isPlaying()) {
                GameCore.currentMap?.centerLocation.let { TeleportUtil.teleport(player, it) }
            }
            // 게임 중인 플레이어 텔레포트 안됐으면 팀 스폰으로
            else {
                GameCore.currentMap?.let { currentMap ->
                    if (!currentMap.isInMap(player.location)) {
                        currentMap.centerLocation.let { TeleportUtil.teleport(player, it) }
                    }
                }
            }
        }
    }

}