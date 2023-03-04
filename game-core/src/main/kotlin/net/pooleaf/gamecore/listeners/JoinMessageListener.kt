package net.pooleaf.gamecore.listeners

import net.pooleaf.gamecore.GameCore
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class JoinMessageListener: Listener {

    /**
     * 대기 중일 경우 -> 게임 시작 전 -> 참여 메시지 (남은 인원)
     *                 게임 시작 후 -> 참여 메시지
     * 게임 중일 경우 -> 참여 중 -> 재접속 메시지
     *                 관전 중 -> 관전 메시지
     */
    @EventHandler
    fun onPlayerJoin(event: net.pooleaf.gamecore.events.player.GamePlayerJoinEvent) {
        val gamePlayer = event.gamePlayer
        val playerJoinEvent = event.playerJoinEvent

        if (!GameCore.game.isGameStarted) {
            if (!GameCore.game.isRunning) {
                playerJoinEvent.joinMessage = "§f${gamePlayer.displayName} §e님께서 접속했습니다. §f(${GameCore.unsafe.playerManager.getOnlinePlayingPlayers().size}/${GameCore.gameConfig.startPlayerCount})"
            } else {
                playerJoinEvent.joinMessage = "§f${gamePlayer.displayName} §e님께서 접속했습니다."
            }
        } else {
            if (gamePlayer.isPlaying()) {
                playerJoinEvent.joinMessage = "§f${gamePlayer.displayName} §e님께서 재접속했습니다."
            } else if (gamePlayer.isSpectator) {
                playerJoinEvent.joinMessage = "§f${gamePlayer.displayName} §b님께서 관전 모드로 접속했습니다."
            }
        }
    }

    /**
     * 대기 중일 경우 -> 게임 시작 전 -> 퇴장 메시지 (남은 인원)
     *                 게임 시작 후 -> 퇴장 메시지
     * 게임 중일 경우 -> 참여 중 -> 퇴장 메시지
     *                 관전 중 -> 관전 퇴장 메시지
     */
    @EventHandler
    fun onPlayerQuit(event: net.pooleaf.gamecore.events.player.GamePlayerQuitEvent) {
        val gamePlayer = event.gamePlayer
        val playerQuitEvent = event.playerQuitEvent

        if (!GameCore.game.isGameStarted) {
            if (!GameCore.game.isRunning) {
                playerQuitEvent.quitMessage = "§f${gamePlayer.displayName} §e님께서 퇴장했습니다. §f(${GameCore.unsafe.playerManager.getOnlinePlayingPlayers().size}/${GameCore.gameConfig.startPlayerCount})"
            } else {
                playerQuitEvent.quitMessage = "§f${gamePlayer.displayName} §e님께서 퇴장했습니다."
            }
        } else {
            if (gamePlayer.isPlaying()) {
                playerQuitEvent.quitMessage = "§f${gamePlayer.displayName} §e님께서 퇴장했습니다."
            } else if (gamePlayer.isSpectator) {
                playerQuitEvent.quitMessage = "§f${gamePlayer.displayName} §b님께서 관전을 종료했습니다."
            }
        }
    }

}