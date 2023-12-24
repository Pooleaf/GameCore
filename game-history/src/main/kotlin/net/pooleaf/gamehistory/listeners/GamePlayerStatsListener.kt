package net.pooleaf.gamehistory.listeners

import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.coroutine.bukkit.BukkitNewAsyncScope
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.game.GameEndEvent
import net.pooleaf.gamecore.events.player.GamePlayerDeathEvent
import net.pooleaf.gamecore.events.player.GamePlayerDefeatEvent
import net.pooleaf.gamehistory.GameHistoryApi
import net.pooleaf.gamehistory.sql.dtos.GameKillDto
import net.pooleaf.gamehistory.sql.dtos.toDto
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.time.LocalDateTime

class GamePlayerStatsListener : Listener {

    /**
     * 게임 플레이어 킬 전적 저장
     */
    @EventHandler
    fun onPlayerDeath(event: GamePlayerDeathEvent) {
        if (!GameHistoryApi.historyConfig.isEnableHistory) return
        if (!GameCore.game.isGameStarted) return

        val deadPlayer = event.deadGamePlayer
        val killerPlayer = event.killerGamePlayer
        val assistPlayers = event.assistGamePlayers

        val gameTypeId = GameCore.game.gameTypeId

        // 킬 정보 DTO 생성
        val gameKillDto = GameKillDto(
            GameCore.game.gameId.toString(),
            killerPlayer?.uuid?.toString() ?: null,
            deadPlayer.uuid.toString(),
            LocalDateTime.now()
        )

        BukkitNewAsyncScope.launch {
            // 킬 정보 저장
            GameHistoryApi.unsafe.sqlManager.gameDao.insertGameKill(gameKillDto)

            // 전적 저장
            GameHistoryApi.unsafe.sqlManager.gameDao.addGamePlayerStatsDeathCount(deadPlayer.uuid, gameTypeId, 1)
            if (killerPlayer != null) {
                GameHistoryApi.unsafe.sqlManager.gameDao.addGamePlayerStatsKillCount(killerPlayer.uuid, gameTypeId, 1)
            }
            assistPlayers?.forEach { GameHistoryApi.unsafe.sqlManager.gameDao.addGamePlayerStatsAssistCount(it.uuid, gameTypeId, 1) }
        }
    }

    @EventHandler
    fun onPlayerDefeat(event: GamePlayerDefeatEvent) {
        val dto = event.gamePlayer.toDto()

        BukkitNewAsyncScope.launch {
            GameHistoryApi.unsafe.sqlManager.gameDao.updateGameParticipantDefeat(dto)
        }
    }

    /**
     * 게임 플레이어 우승 전적 저장
     */
    @EventHandler
    fun onPlayerWin(event: GameEndEvent) {
        if (!GameHistoryApi.historyConfig.isEnableHistory) return

        val winnerTeam = event.winnerTeam ?: return

        val gameTypeId = GameCore.game.gameTypeId

        BukkitNewAsyncScope.launch {
            winnerTeam.players.forEach { gamePlayer ->
                GameHistoryApi.unsafe.sqlManager.gameDao.addGamePlayerStatsWinCount(gamePlayer.uuid, gameTypeId, 1)
            }
        }
    }

}