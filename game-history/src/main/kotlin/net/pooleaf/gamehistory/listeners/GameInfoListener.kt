package net.pooleaf.gamehistory.listeners

import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitNewAsyncScope
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.game.GameCancelEvent
import net.pooleaf.gamecore.events.game.GameEndEvent
import net.pooleaf.gamecore.events.game.GameStartedEvent
import net.pooleaf.gamehistory.GameHistoryApi
import net.pooleaf.gamehistory.sql.dtos.GameWinnerDto
import net.pooleaf.gamehistory.sql.dtos.toDto
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class GameInfoListener : Listener {

    /**
     * 게임 시작 정보 저장
     */
    @EventHandler
    fun onGameStarted(event: GameStartedEvent) {
        if (!GameHistoryApi.historyConfig.isEnableHistory) return

        val game = GameCore.game

        BukkitNewAsyncScope.launch {
            GameHistoryApi.unsafe.sqlManager.gameDao.insertGame(game.toDto())

            val participantDtos = GameCore.unsafe.playerManager.getJoinedPlayers().map { it.toDto() }
            GameHistoryApi.unsafe.sqlManager.gameDao.insertGameParticipants(participantDtos)
        }
    }

    /**
     * 게임 종료 정보 저장
     */
    @EventHandler
    fun onGameEnd(event: GameEndEvent) {
        if (!GameHistoryApi.historyConfig.isEnableHistory) return

        val game = GameCore.game

        val gameDto = game.toDto()
        val gameId = game.gameId

        val winnerTeam = event.winnerTeam

        BukkitNewAsyncScope.launch {
            GameHistoryApi.unsafe.sqlManager.gameDao.insertGame(gameDto)

            if (winnerTeam != null) {
                val winnerDtos = winnerTeam.players.map { GameWinnerDto(gameId.toString(), winnerTeam.id, it.uuid.toString()) }
                GameHistoryApi.unsafe.sqlManager.gameDao.insertGameWinner(winnerDtos)
            }
        }
    }

    /**
     * 게임 중단 정보 저장
     */
    @EventHandler
    fun onGameCancel(event: GameCancelEvent) {
        if (!GameHistoryApi.historyConfig.isEnableHistory) return

        val game = GameCore.game
        val gameDto = game.toDto()

        BukkitNewAsyncScope.launch {
            GameHistoryApi.unsafe.sqlManager.gameDao.insertGame(gameDto)
        }
    }

}