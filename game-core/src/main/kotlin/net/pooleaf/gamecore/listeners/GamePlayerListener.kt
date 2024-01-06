package net.pooleaf.gamecore.listeners

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.player.GamePlayerJoinEvent
import net.pooleaf.gamecore.events.player.GamePlayerQuitEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class GamePlayerListener: Listener {

    /**
     * GamePlayer 등록 및 리셋
     * -> 게임 중이 아니라면? -> 게임 참여 대기 셋팅 -> 자동 시작
     * -> 게임 중이라면? -> 플레이 중이라면? -> 게임 중 셋팅
     *                    안했다면?  -> 관전 등록
     */
    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        var gamePlayer = GameCore.unsafe.playerManager.get(player.uniqueId)

        val isNewPlayer = (gamePlayer == null)

        BukkitSyncScope.launch {
            // 새로운 GamePlayer라면 등록 및 초기화
            if (isNewPlayer) {
                gamePlayer = GameCore.unsafe.playerManager.gamePlayerFactory.createGamePlayer(player.uniqueId)
                GameCore.unsafe.playerManager.set(player.uniqueId, gamePlayer)

                gamePlayer.init()
            }

            // 게임 중이 아니라면
            if (!GameCore.game.isGameStarted) {
                // 게임에 참여
                GameCore.unsafe.playerService.joinToGame(gamePlayer)
            }
            // 게임 중이라면
            else {
                // 게임에 참여했고 팀이 있으면 팀 이름표 접두사 보여줌
                if (gamePlayer.isJoined && gamePlayer.team?.players?.size?.let { it > 1 } == true) {
                    GameCore.unsafe.teamNameTagManager.setTeamNameTag(gamePlayer)
                }

                // 게임 플레이 중이라면
                if (!isNewPlayer && gamePlayer.isPlaying()) {
                    // 게임 중 셋팅
                    GameCore.unsafe.playerService.settingToPlaying(gamePlayer)

                    // 재접속 타이머 해제
                    gamePlayer.reconnectJob?.cancel()
                    gamePlayer.reconnectJob = null

                    // 관전 텔레포터 GUI 업데이트
                    GameCore.unsafe.quickBarManager.spectatorQuickBar.spectatorTeleporterGui.updateAsynchronously()
                }
                // 플레이 중이 아니라면
                else {
                    GameCore.unsafe.playerService.enableSpectatorMode(gamePlayer)
                }
            }

            // 이벤트
            val gamePlayerJoinEvent = GamePlayerJoinEvent(gamePlayer, event)
            try {
                Bukkit.getPluginManager().callEvent(gamePlayerJoinEvent)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            // 접속 메시지
            gamePlayerJoinEvent.playerJoinEvent.joinMessage?.let { BukkitBroadcaster.broadcast(it) }
        }

        event.joinMessage = null
    }

    /**
     * 게임 중이 아니라면? -> GamePlayer 삭제
     * 게임 중이라면? -> 참여했다면? -> 살아있다면? -> 재접속 타이머 시작
     *                              탈락했다면? ->
     *                 안했다면? -> GamePlayer 삭제
     */
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        val gamePlayer = GameCore.unsafe.playerManager.get(player.uniqueId)

        BukkitSyncScope.launch {
            // 게임 중이 아니라면
            if (!GameCore.game.isGameStarted) {
                GameCore.unsafe.playerManager.remove(player.uniqueId)
                GameCore.unsafe.playerService.quitFromGame(gamePlayer)
            }
            // 게임 중이라면
            else {
                // 게임에 참여했다면
                if (gamePlayer.isJoined) {
                    // 플레이 중이라면
                    if (gamePlayer.isPlaying()) {
                        // 재접속 타이머
                        gamePlayer.reconnectJob = BukkitAsyncScope.launch {
                            delay(GameCore.gameConfig.reconnectAllowSeconds * 1000L)
                            GameCore.unsafe.playerService.defeatPlayer(gamePlayer)
                            BukkitBroadcaster.broadcast("§c${gamePlayer.displayName} 님께서 재접속하지 않아 탈락했습니다.")
                            BukkitBroadcaster.broadcastSound(XSound.BLOCK_NOTE_BLOCK_BASS)
                        }
                    }

                    // 기록이나 재접속을 위해 GamePlayer 보존
                }
                // 참여하지 않았다면
                else {
                    GameCore.unsafe.playerManager.remove(player.uniqueId)
                }
            }

            // 이벤트
            val gamePlayerQuitEvent = GamePlayerQuitEvent(gamePlayer, event)
            try {
                Bukkit.getPluginManager().callEvent(gamePlayerQuitEvent)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            // 접속 메시지
            gamePlayerQuitEvent.playerQuitEvent.quitMessage?.let { BukkitBroadcaster.broadcast(it) }

            // 관전 해제
            if (gamePlayer.isSpectator) {
                GameCore.unsafe.playerService.disableSpectatorMode(gamePlayer)
            }

            // 게임 중단
            Bukkit.getScheduler().runTaskLater(GameCore.gamePlugin, {
                if (gamePlayer.isJoined) {
                    // 관전 텔레포터 GUI 업데이트
                    GameCore.unsafe.quickBarManager.spectatorQuickBar.spectatorTeleporterGui.updateAsynchronously()
                }

                BukkitSyncScope.launch {
                    if (GameCore.unsafe.gameManager.canStop()) {
                        GameCore.unsafe.gameManager.stopGame()
                    }
                }
            }, 1L)
        }

        event.quitMessage = null
    }

}