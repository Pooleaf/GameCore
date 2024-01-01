package net.pooleaf.gamecore.player

import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.gui.GuiModule
import net.pooleaf.gamecore.GameBroadcaster
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.player.*
import net.pooleaf.gamecore.events.team.TeamDefeatEvent
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class GamePlayerService {

    /**
     * 플레이어 정보를 초기화합니다.
     */
    fun initPlayer(gamePlayer: GamePlayer) {
        // 정보 초기화
        gamePlayer.isJoined = false
        gamePlayer.isDefeated = false
        gamePlayer.isSpectator = false
        gamePlayer.isReceiveStartItems = false

        gamePlayer.lastDamagers.clear()

        gamePlayer.killStreak = null
        gamePlayer.lastKillTime = null

        gamePlayer.team?.removePlayer(gamePlayer)
        gamePlayer.team = null

        gamePlayer.reconnectJob?.cancel()
        gamePlayer.reconnectJob = null

        // 이벤트
        Bukkit.getPluginManager().callEvent(GamePlayerInitEvent(gamePlayer))
    }

    /**
     * 플레이어 게임 상태를 리셋합니다.
     * 온라인 플레이어만 사용 가능합니다.
     */
    suspend fun resetPlayer(gamePlayer: GamePlayer) {
        // 리셋
        BukkitSyncScope.async {
            if (!gamePlayer.isOnline) error("Player is not online")

            val player = gamePlayer.player

            // 스텟 초기화
            player.health = player.maxHealth
            player.level = 0
            player.exp = 0.0F

            // 인벤토리 초기화
            player.inventory!!.clear()
            player.inventory.helmet = null
            player.inventory.chestplate = null
            player.inventory.leggings = null
            player.inventory.boots = null
            player.updateInventory()

            // 게임모드 초기화
            player.gameMode = GameCore.game.currentGameMode

            // 비행 비활성화
            player.allowFlight = false
            player.isFlying = false

            // 충돌 활성화
            player.spigot().collidesWithEntities = true

            // 포션 효과 제거
            player.activePotionEffects.forEach { player.removePotionEffect(it.type) }

            // 투명 해제
            Bukkit.getOnlinePlayers().forEach { it.showPlayer(player) }

            try {
                // 팀 이름표 접두사 제거
                if (GameCore.unsafe.teamNameTagManager.exists(gamePlayer.uuid)) {
                    GameCore.unsafe.teamNameTagManager.removeTeamNameTag(gamePlayer)
                }

                // 퀵바 제거
                GuiModule.getQuickBarManager().removeTo(player)

                // 투표 삭제
                GameCore.unsafe.startVoteManager.unvote(gamePlayer)
                GameCore.unsafe.mapVoteManager.unvote(gamePlayer)
                GameCore.unsafe.godModeSkipVoteManager.unvote(gamePlayer)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }


            // 이벤트
            Bukkit.getPluginManager().callEvent(GamePlayerResetEvent(gamePlayer))
        }.await()
    }

    /**
     * 플레이어를 게임에 참여시킵니다.
     * 온라인 플레이어에게만 사용할 수 있습니다.
     */
    suspend fun joinToGame(gamePlayer: GamePlayer) {
        if (!gamePlayer.isOnline) error("gamePlayer is not online")

        // 게임 참여
        gamePlayer.isJoined = true

        // 셋팅
        settingToCurrentProgress(gamePlayer)

        // 대기 중이라면
        if (!GameCore.game.isRunning) {
            // 대기 액션바
            GameBroadcaster.broadcastWaitingActionBar(GameCore.unsafe.playerManager.getOnlineJoinedPlayers().size, GameCore.gameConfig.startPlayerCount)

            // 게임 시작
            BukkitSyncScope.launch {
                if (GameCore.unsafe.gameManager.canAutoStart()) {
                    GameCore.unsafe.gameManager.startGame(null)
                }
            }
        }

        // 이벤트
        Bukkit.getPluginManager().callEvent(GamePlayerJoinToGameEvent(gamePlayer))
    }

    /**
     * 플레이어를 게임 참여에서 제외시킵니다.
     * 온라인 플레이어에게만 사용할 수 있습니다.
     */
    suspend fun quitFromGame(gamePlayer: GamePlayer) {
        if (!gamePlayer.isOnline) error("gamePlayer is not online")

        // 게임 참여
        gamePlayer.isJoined = false

        // 리셋
        resetPlayer(gamePlayer)

        // 대기 중이라면
        if (!GameCore.game.isRunning) {
            // 대기 액션바
            GameBroadcaster.broadcastWaitingActionBar(GameCore.unsafe.playerManager.getOnlineJoinedPlayers().size, GameCore.gameConfig.startPlayerCount)

            // 게임 시작
            if (GameCore.unsafe.gameManager.canAutoStart()) {
                GameCore.unsafe.gameManager.startGame(null)
            }
        }
        // 게임 중이라면
        else {
            if (GameCore.unsafe.gameManager.canStop()) {
                GameCore.unsafe.gameManager.stopGame()
            } else {
                // 관전 텔레포터 GUI 업데이트
                GameCore.unsafe.quickBarManager.spectatorQuickBar.spectatorTeleporterGui.updateAsynchronously()
            }
        }

        // 이벤트
        Bukkit.getPluginManager().callEvent(GamePlayerQuitFromGameEvent(gamePlayer))
    }

    /**
     * 플레이어를 대기 중 필요한 상태로 셋팅합니다.
     * 온라인 플레이어에게만 사용할 수 있습니다.
     */
    suspend fun settingToWaiting(gamePlayer: GamePlayer) {
        if (!gamePlayer.isOnline) error("gamePlayer is not online")

        resetPlayer(gamePlayer)

        val player = gamePlayer.player

        // 퀵바
        GameCore.unsafe.quickBarManager.waitingQuickBar.setTo(player)
    }

    /**
     * 플레이어를 게임 중 필요한 상태로 셋팅합니다.
     * 온라인 플레이어에게만 사용할 수 있습니다.
     */
    suspend fun settingToPlaying(gamePlayer: GamePlayer) {
        if (!gamePlayer.isOnline) error("gamePlayer is not online")

    }

    /**
     * 현재 게임 진행상황에 맞게 플레이어를 셋팅합니다.
     */
    suspend fun settingToCurrentProgress(gamePlayer: GamePlayer) {
        if (GameCore.game.isGameStarted) {
            settingToPlaying(gamePlayer)
        } else {
            settingToWaiting(gamePlayer)
        }
    }

    /**
     * 플레이어의 관전 모드를 활성화합니다.
     */
    suspend fun enableSpectatorMode(gamePlayer: GamePlayer) {
        if (gamePlayer.isSpectator) error("gamePlayer already spectator")

        BukkitSyncScope.launch {
            val isJoined = GameCore.game.isGameStarted && gamePlayer.isJoined

            // 정보 업데이트
            gamePlayer.isSpectator = true

            // 오프라인일 경우 실행 안함
            if (!gamePlayer.isOnline) return@launch

            // 참여 해제
            quitFromGame(gamePlayer)

            // 리셋
            resetPlayer(gamePlayer)

            // 게임 중 참여 해제하고 리셋하면 정보가 사라지므로 다시 설정
            gamePlayer.isJoined = isJoined
            if (isJoined && GameCore.unsafe.teamNameTagManager.exists(gamePlayer.uuid)) {
                GameCore.unsafe.teamNameTagManager.setTeamNameTag(gamePlayer)
            }

            // 관전 셋팅
            val player = gamePlayer.player

            // 게임모드
            player.gameMode = GameMode.ADVENTURE

            // 날기
            player.allowFlight = true
            // 게임 시작했으면 날기 활성화
            if (GameCore.game.isGameStarted) {
                player.isFlying = true
            }

            // 투명
            player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 100000, 0, true))
            Bukkit.getOnlinePlayers().forEach { it.hidePlayer(player) }

            // 충돌 비활성화
            player.spigot().collidesWithEntities = false

            // 퀵바
            if (GameCore.game.isGameStarted) {
                GameCore.unsafe.quickBarManager.spectatorQuickBar.setTo(player)
            } else {
                GameCore.unsafe.quickBarManager.waitingQuickBar.setTo(player)
            }

            // 이벤트
            Bukkit.getPluginManager().callEvent(GamePlayerEnableSpectatorModeEvent(gamePlayer))
        }.join()
    }

    /**
     * 플레이어의 관전 모드를 비활성화합니다.
     */
    suspend fun disableSpectatorMode(gamePlayer: GamePlayer) {
        if (!gamePlayer.isSpectator) error("gamePlayer already not spectator")

        BukkitSyncScope.async {
            gamePlayer.isSpectator = false

            // 오프라인일 경우 실행 안함
            if (!gamePlayer.isOnline) return@async

            // 리셋
            resetPlayer(gamePlayer)

            // 게임 참여
            joinToGame(gamePlayer)

            // 이벤트
            Bukkit.getPluginManager().callEvent(
                GamePlayerDisableSpectatorModeEvent(gamePlayer)
            )
        }.await()
    }

    /**
     * 플레이어를 탈락시킵니다.
     */
    suspend fun defeatPlayer(gamePlayer: GamePlayer) {
        gamePlayer.isDefeated = true

        // 이벤트
        Bukkit.getPluginManager().callEvent(GamePlayerDefeatEvent(gamePlayer, gamePlayer.getKillerGamePlayer(), gamePlayer.getKillerAssistGamePlayer()))

        // 팀 탈락 이벤트
        if (gamePlayer.team?.let { it.isDefeated() } == true) {
            Bukkit.getPluginManager().callEvent(TeamDefeatEvent(gamePlayer.team!!, gamePlayer.getKillerGamePlayer()))
        }

        // 재접속 타이머 취소
        if (gamePlayer.reconnectJob?.isActive == true) {
            gamePlayer.reconnectJob?.cancel()
            gamePlayer.reconnectJob = null
        }

        // 관전 전환
        if (gamePlayer.isOnline) {
            enableSpectatorMode(gamePlayer)
        }

        // 관전 텔레포터 GUI 업데이트
        GameCore.unsafe.quickBarManager.spectatorQuickBar.spectatorTeleporterGui.updateAsynchronously()
    }

}