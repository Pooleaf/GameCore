package net.pooleaf.gamecore.game

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.channel.ChannelModule
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.gui.GuiModule
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.core.modules.support.common.util.toMillis
import net.pooleaf.gamecore.GameBroadcaster
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.game.*
import net.pooleaf.gamecore.phases.EndPhase
import net.pooleaf.gamecore.team.Team
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class GameManager {

    lateinit var game: Game
        internal set


    /**
     * 게임 정보를 초기화 시킵니다.
     */
    fun initGame() {
        // 게임 정보 초기화
        game.gameId = null

        game.isRunning = false
        game.isCountingStarted = false
        game.isGameStarted = false
        game.isTeleportedToMap = false
        game.isGodMode = true
        game.isEnded = false
        game.isCancelled = false

        game.startedAt = null
        game.endedAt = null

        game.currentGameMode = game.waitingGameMode

        // 이벤트
        Bukkit.getPluginManager().callEvent(GameInitEvent())
    }

    /**
     * 게임을 리셋시킵니다.
     */
    suspend fun resetGame() {
        // 이벤트
        Bukkit.getPluginManager().callEvent(GameBeforeResetEvent())

        game.isInitialized = false

        // 초기화
        initGame()

        // 스폰으로 텔레포트
        BukkitSyncScope.launch {
            GameCore.spawnConfig.spawnLocation?.let { location ->
                Bukkit.getOnlinePlayers().forEach { player -> TeleportUtil.teleport(player, location) }
            }
        }.join()

        // 맵 언로드
        GameCore.currentMap?.let { map ->
            // 언로드 실패 시 서버 재부팅
            if (!map.unloadWorld()) {
                BukkitBroadcaster.broadcastTitle("§c오류", "§c게임 초기화에 실패하여 서버가 재부팅됩니다.", 10 * 20)

                // 10초 후 서버 종료
                BukkitAsyncScope.launch {
                    // 로비로 이동
                    delay(8000L)
                    Bukkit.getOnlinePlayers().forEach { ChannelModule.getLobbyChannelGroup().fastJoin(it.uniqueId) }

                    // 서버 종료
                    delay(2000L)
                    Bukkit.shutdown()
                }.join()
            }
        }
        GameCore.unsafe.mapManager.currentMap = null

        // Phase 초기화
        if (game.phasePipeline.isRunning()) {
            game.phasePipeline.cancelPhases()
        }
        game.phasePipeline.init()

        // 플레이어 초기화
        GameCore.unsafe.playerManager.values().forEach { gamePlayer ->
            gamePlayer.init()

            if (gamePlayer.isOnline) {
                gamePlayer.reset()
                GameCore.unsafe.playerService.joinToGame(gamePlayer)

                // 대기 퀵바
                GameCore.unsafe.quickBarManager.waitingQuickBar.setTo(gamePlayer.player)
            } else {
                GameCore.unsafe.playerManager.remove(gamePlayer.uuid)
            }
        }

        // 대기 액션바
        GameBroadcaster.broadcastWaitingActionBar(GameCore.unsafe.playerManager.getOnlineJoinedPlayers().size, GameCore.gameConfig.startPlayerCount)

        // 투표 초기화
        GameCore.unsafe.startVoteManager.initVote()
        GameCore.unsafe.mapVoteManager.initVote()

        // 사이드바
        if (GameCore.unsafe.sideBarManager.isSideBarTimerRunning()) {
            GameCore.unsafe.sideBarManager.stopSideBarTimer()
            GameCore.unsafe.sideBarManager.sideBar?.update()
        }

        // 보급품 데이터 삭제
        GameCore.unsafe.supplyManager.createdSupply.clear()

        // 보급품 타이머 중단
        if (GameCore.unsafe.supplyManager.isSupplyCreateTimerRunning()) {
            GameCore.unsafe.supplyManager.stopSupplyCreateTimer()
        }
        if (GameCore.unsafe.supplyManager.isSupplyParticleTimerRunning()) {
            GameCore.unsafe.supplyManager.stopSupplyParticleTimer()
        }

        // 이벤트
        Bukkit.getPluginManager().callEvent(GameResetEvent())

        game.isInitialized = true
    }


    /**
     * 게임을 시작시킵니다.
     * PrimaryThread에서 실행해야 합니다
     */
    suspend fun startGame(starterSender: CommandSender?) {
        if (game.isRunning) error("Game has already started")
        if (!Bukkit.isPrimaryThread()) error("Game start cannot start asynchronously")

        game.isRunning = true

        BukkitAsyncScope.launch {
            // 설정된 맵 없으면 랜덤 맵으로 설정
            if (GameCore.currentMap == null) {
                GameCore.unsafe.mapManager.currentMap = GameCore.unsafe.mapManager.getRandomMapCanUse()
            }
            // 맵 사용 가능 체크
            if (GameCore.currentMap?.canUse == false) {
                GameCore.unsafe.mapManager.currentMap = null
            }
            GameCore.currentMap?.let {
                // 월드 로드
                if (!it.isWorldLoaded()) {
                    it.loadWorld()
                }
            } ?: run {
                // 맵 없으면 중단
                BukkitBroadcaster.broadcastTitle(
                    "§c시작 실패",
                    "§c사용할 수 있는 맵이 없어 게임을 시작할 수 없습니다.",
                    5 * 20
                )
                BukkitBroadcaster.broadcastSound(XSound.ENTITY_ITEM_BREAK, 0.4F, 1.0F)
                return@launch
            }

            // 게임 정보 업데이트
            game.gameId = UUID.randomUUID()

            // 액션바 제거
            BukkitBroadcaster.removeActionBar()

            // 대기 퀵바 업데이트 (관전 슬롯 제거)
            GameCore.unsafe.quickBarManager.waitingQuickBar.updateAsynchronously()

            // 사이드바
            if (GameCore.unsafe.sideBarManager.sideBar != null && !GameCore.unsafe.sideBarManager.isSideBarTimerRunning()) {
                GameCore.unsafe.sideBarManager.startSideBarTimer()
            }

            // Phase 시작
            game.phasePipeline.runPhases()

            // 이벤트
            Bukkit.getPluginManager().callEvent(GameStartEvent(starterSender))
        }.join()
    }

    /**
     * 게임 시작 시 실행됩니다.
     */
    fun onGameStarted() {
        // 팀 매칭
        GameCore.unsafe.teamService.matchingTeams(GameCore.teamConfig.playerCountPerTeam, GameCore.teamConfig.maxTeamCount)

        // 게임 정보 업데이트
        game.isGameStarted = true
        game.startedAt = LocalDateTime.now()

        // 액션바 제거
        BukkitBroadcaster.removeActionBar()

        // 퀵바 제거
        Bukkit.getOnlinePlayers().forEach { GuiModule.getQuickBarManager().removeTo(it) }

        // 관전자 설정
        GameCore.unsafe.playerManager.getOnlineSpectators().forEach {
            // 관전 퀵바
            GameCore.unsafe.quickBarManager.spectatorQuickBar.setTo(it.player)

            // 관전자 날기 활성화
            it.player.isFlying = true
        }

        // 관전 텔레포터 GUI 업데이트
        GameCore.unsafe.quickBarManager.spectatorQuickBar.spectatorTeleporterGui.updateAsynchronously()

        // 시작 아이템 지급
        GameCore.unsafe.playerManager.getOnlinePlayingPlayers().forEach { it.giveStartItem() }

        // 이벤트
        Bukkit.getPluginManager().callEvent(GameStartedEvent())
    }

    /**
     * 게임 자동 시작 가능 여부를 반환합니다.
     */
    fun canAutoStart(): Boolean {
        return game.isInitialized && !GameCore.game.isRunning
                && GameCore.unsafe.playerManager.getOnlineJoinedPlayers().size >= GameCore.gameConfig.startPlayerCount
    }

    /**
     * 게임 종료 가능 여부를 반환합니다.
     */
    fun canEnd(): Boolean {
        return game.isInitialized && game.isGameStarted && !game.isEnded
                && GameCore.unsafe.teamManager.getNotDefeatedOnlineTeams().size < 2
    }

    /**
     * 게임 중단 또는 종료 가능 여부를 반환합니다.
     */
    fun canStop(): Boolean {
        return game.isRunning
                && (
                !GameCore.game.isGameStarted && GameCore.unsafe.playerManager.getOnlinePlayingPlayers().size <= GameCore.teamConfig.playerCountPerTeam
                || GameCore.game.isGameStarted && !GameCore.game.isEnded && GameCore.unsafe.teamManager.getNotDefeatedOnlineTeams().size < 2
                        )
    }

    /**
     * 게임 중단 또는 종료
     */
    suspend fun stopGame() {
        if (!canStop()) error("game cannot stop")

        // 게임 시작 전 중단
        if (!GameCore.game.isGameStarted && GameCore.unsafe.playerManager.getOnlinePlayingPlayers().size <= GameCore.teamConfig.playerCountPerTeam) {
            cancelGame(null, "인원이 적어 게임이 중단됩니다.")
            BukkitBroadcaster.broadcast("§c인원이 적어 게임이 중단되었습니다.")
        }

        // 플레이 중인 팀이 한팀 남았으면 게임 종료
        else if (GameCore.game.isGameStarted && GameCore.unsafe.teamManager.getNotDefeatedOnlineTeams().size < 2) {
            // 우승 시간 안됐을 때 중단
            if (System.currentTimeMillis() - GameCore.game.startedAt!!.toMillis() < GameCore.gameConfig.winAllowSeconds) {
                cancelGame(null, "게임 진행 시간이 적어 우승할 수 없습니다.")
                BukkitBroadcaster.broadcast("§c게임 진행 시간이 적어 승자가 결정되지 않았습니다.")
                BukkitBroadcaster.broadcast("§c더 많은 시간을 플레이해야 게임이 정상적으로 종료됩니다.")
            }
            // 아무도 없을 경우
            else if (GameCore.unsafe.teamManager.getNotDefeatedOnlineTeams().isEmpty()) {
                cancelGame(null)
            }
            // 우승
            else {
                skipToEnd()
            }
        }
    }

    /**
     * 우승 가능 여부를 반환합니다.
     */
    fun isWinAllowTime(): Boolean {
        return game.startedAt?.let { System.currentTimeMillis() - it.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() > GameCore.gameConfig.winAllowSeconds * 1000 } == true
    }

    /**
     * EndPhase까지 Phase를 스킵합니다.
     */
    fun skipToEnd() {
        if (!game.phasePipeline.isRunning()) error("game is not running")

        val endPhase = game.phasePipeline.phases.filter { it is EndPhase }.firstOrNull()
        endPhase?.let {
            game.phasePipeline.cancelPhases()
            game.phasePipeline.currentPhase = endPhase
            game.phasePipeline.job = BukkitAsyncScope.launch {
                endPhase.start()
            }
        }
    }

    /**
     * 게임 종료 시 실행됩니다.
     * 우승 팀을 반환합니다.
     */
    suspend fun onGameEnd(): Team? {
        if (!canEnd()) error("End of game condition not met")

        // 게임 정보 업데이트
        game.isEnded = true
        game.endedAt = LocalDateTime.now()

        // 재접속 중인 플레이어 탈락처리
        GameCore.unsafe.playerManager.getJoinedPlayers().filter { it.reconnectJob?.isActive == true }
            .forEach { GameCore.unsafe.playerService.defeatPlayer(it) }

        // 우승자 계산
        val winnerTeam = GameCore.unsafe.teamManager.getNotDefeatedOnlineTeams().firstOrNull()

        // 게임 종료 이벤트
        Bukkit.getPluginManager().callEvent(GameEndEvent(winnerTeam))

        return winnerTeam
    }

    /**
     * 게임을 중단시킵니다.
     */
    suspend fun cancelGame(cancelSender: CommandSender?, cancelCause: String = "게임이 중단되었습니다.") {
        if (!GameCore.game.isRunning) error("Game is not started")

        // 게임 정보 업데이트
        game.isEnded = true
        game.isCancelled = true
        game.endedAt = LocalDateTime.now()

        // 이벤트
        Bukkit.getPluginManager().callEvent(GameCancelEvent(cancelSender, cancelCause))

        // 리셋
        resetGame()

        // 타이틀
        BukkitBroadcaster.broadcastTitle("§c게임 중단", "§c${cancelCause}", 5 * 20)
        BukkitBroadcaster.broadcastSound(XSound.ENTITY_ITEM_BREAK, 1.0F, 1.0F)

        // 이벤트
        Bukkit.getPluginManager().callEvent(GameCancelledEvent(cancelSender, cancelCause))
    }

    /**
     * 팀끼리 맵으로 텔레포트시킵니다.
     */
    suspend fun teleportToMap() {
        val map = GameCore.currentMap

        map?.let {
            BukkitSyncScope.launch {
                // 관전자를 맵으로 텔레포트
                map.centerLocation?.let { centerLocation ->
                    GameCore.unsafe.playerManager.getOnlineSpectators().forEach { TeleportUtil.teleport(it.player, centerLocation) }
                }

                // 팀끼리 맵으로 텔레포트
                GameCore.unsafe.teamManager.teams.forEach { team ->
                    val location = map.getRandomLocation()
                    location?.let {
                        team.spawnLocation = location
                        team.teleport(location)
                    } ?: error("random location cannot be null")
                }
            }.join()

            // 액션바
            BukkitBroadcaster.broadcastActionBar("${map.displayName} §e맵으로 이동되었습니다.")

            // 게임 정보 업데이트
            game.isTeleportedToMap = true

            // 이벤트
            Bukkit.getPluginManager().callEvent(GameMapTeleportedEvent())
        } ?: error("currentMap cannot be null")
    }

    /**
     * 현재 게임 모드를 변경합니다.
     */
    suspend fun changeCurrentGameMode(gameMode: GameMode) {
        BukkitSyncScope.launch {
            GameCore.game.currentGameMode = gameMode
            GameCore.unsafe.playerManager.getPlayingPlayers().forEach {
                it.player?.gameMode = gameMode

                if (gameMode != GameMode.CREATIVE) {
                    it.player?.allowFlight = false
                    it.player?.isFlying = false
                }
            }
        }.join()
    }

}