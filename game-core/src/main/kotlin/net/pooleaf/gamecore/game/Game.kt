package net.pooleaf.gamecore.game

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phase.PhasePipeline
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import java.time.LocalDateTime
import java.util.UUID

abstract class Game {

    val gameTypeId: Int

    var gameId: UUID? = null
        internal set

    // 초기화 여부
    var isInitialized: Boolean = false
        internal set

    // 게임 실행 중 여부
    var isRunning: Boolean = false
        internal set

    // 시작 카운팅 시작 여부
    var isCountingStarted: Boolean = false
        internal set

    // 시작 여부
    var isGameStarted: Boolean = false
        internal set

    // 맵 텔레포트 여부
    var isTeleportedToMap: Boolean = false
        internal set

    // 무적 모드 여부
    var isGodMode: Boolean = false
        internal set

    // 게임 종료 여부
    var isEnded: Boolean = false
        internal set

    // 게임 취소 여부
    var isCancelled: Boolean = false
        internal set

    // 시작 시간
    var startedAt: LocalDateTime? = null
        internal set

    // 종료 시간
    var endedAt: LocalDateTime? = null
        internal set

    // 대기 중 게임 모드
    val waitingGameMode: GameMode

    // 현재 게임 모드
    var currentGameMode: GameMode = GameMode.ADVENTURE
        internal set

    // 노인챈전
    var isNoEnchantMode: Boolean = false

    // 게임 Phase
    val phasePipeline: PhasePipeline


    constructor(gameTypeId: Int, phasePipeline: PhasePipeline, waitingGameMode: GameMode = GameMode.ADVENTURE) {
        this.gameTypeId = gameTypeId
        this.phasePipeline = phasePipeline
        this.waitingGameMode = waitingGameMode
    }

    /**
     * 게임 정보를 초기화 시킵니다.
     */
    fun init() {
        GameCore.unsafe.gameManager.initGame()
    }

    /**
    * 게임을 시작시킵니다.
    * PrimaryThread에서 실행해야 합니다
    */
    suspend fun start(starterSender: CommandSender?) {
        GameCore.unsafe.gameManager.startGame(starterSender)
    }

    /**
     * 게임을 리셋시킵니다.
     */
    suspend fun reset() {
        GameCore.unsafe.gameManager.resetGame()
    }

    /**
     * 게임을 중단시킵니다.
     */
    suspend fun cancel(cancelSender: CommandSender?, cancelCause: String = "게임이 중단되었습니다.") {
        GameCore.unsafe.gameManager.cancelGame(cancelSender, cancelCause)
    }

    /**
     * 현재 게임 모드를 변경합니다.
     */
    suspend fun changeCurrentGameMode(gameMode: GameMode) {
        GameCore.unsafe.gameManager.changeCurrentGameMode(gameMode)
    }

    /**
     * EndPhase까지 Phase를 스킵합니다.
     */
    fun skipToEnd() {
        GameCore.unsafe.gameManager.skipToEnd()
    }

}