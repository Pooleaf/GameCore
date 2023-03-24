package net.pooleaf.gamehistory.sql.daos

import net.pooleaf.core.modules.sqllib.common.AbstractSqlManager
import net.pooleaf.core.modules.sqllib.common.SqlDao
import net.pooleaf.core.modules.sqllib.common.SqlTable
import net.pooleaf.gamehistory.sql.dtos.*
import java.time.LocalDateTime
import java.util.*

class GameDao(sqlManager: AbstractSqlManager?) : SqlDao(sqlManager) {

    val gameTable = SqlTable(sqlManager, "games",
        "game_id VARCHAR(36) PRIMARY KEY",
        "game_type_id INT",
        "channel_name VARCHAR(50)",
        "started_at DATETIME",
        "ended_at DATETIME",
        "cancel_yn VARCHAR(1)"
    )

    val gameTypeTable = SqlTable(sqlManager, "game_types",
        "game_type_id INT PRIMARY KEY",
        "type_name VARCHAR(20)"
    )

    val gameParticipantTable = SqlTable(sqlManager, "game_participants",
        "game_id VARCHAR(36)",
        "team_id INT",
        "player_uuid VARCHAR(36)",
        "defeat_yn VARCHAR(1)",
        "PRIMARY KEY(game_id, player_uuid)"
    )

    val gameKillTable = SqlTable(sqlManager, "game_kills",
        "game_id VARCHAR(36)",
        "killer_player_uuid VARCHAR(36)",
        "dead_player_uuid VARCHAR(36)",
        "killed_at DATETIME"
    )

    val gameWinnerTable = SqlTable(sqlManager, "game_winners",
        "game_id VARCHAR(36)",
        "team_id INT",
        "winner_player_uuid VARCHAR(36)",
        "PRIMARY KEY(game_id, winner_player_uuid)"
    )

    val gamePlayerStats = SqlTable(sqlManager, "game_player_stats",
        "player_uuid VARCHAR(36)",
        "game_type_id INT",
        "kill_count INT",
        "death_count INT",
        "assist_count INT",
        "win_count INT",
        "PRIMARY KEY(player_uuid, game_type_id)"
    )

    override fun onConnected() {
        gameTable.create()
        gameTypeTable.create()
        gameParticipantTable.create()
        gameKillTable.create()
        gameWinnerTable.create()
        gamePlayerStats.create()
    }

    /**
     * Game
     */

    fun insertGame(gameDto: GameDto) {
        gameTable.insertInto()
            .valuesByObject(gameDto)
            .onDuplicateKeyUpdate()
            .execute()
    }

    fun selectGame(gameId: String): GameDto {
        return gameTable.select()
            .where("game_id = ?")
            .parameters(gameId)
            .execute(GameDto::class.java)
    }

    fun selectRecentGameIdByPlayerUuid(playerUuid: String, count: Int = 1): List<String> {
        return sqlManager.getResult("""
            SELECT game.game_id
            FROM ${gameTable.name} game, ${gameParticipantTable.name} part
            WHERE game.game_id = part.game_id
            AND part.player_uuid = ?
            ORDER BY game.started_at DESC
            LIMIT ${count}
        """.trimIndent(), playerUuid).rows.map { it.getString("game_id") }
    }

    /**
     * 서버가 강제 종료되어 종료 처리되지 않은 게임을 종료 처리합니다.
     */
    fun updateNotEndedGame(channelName: String) {
        gameTable.update()
            .set("cancel_yn = 'Y'")
            .where("channel_name = ? AND ended_at IS NULL")
            .parameters(channelName)
            .execute()
    }

    /**
     * 게임에 참여 중이고 탈락하지 않은 게임 ID를 반환합니다.
     */
    fun selectPlayingGameIdByPlayerUuid(playerUuid: String): String? {
        return sqlManager.getResult("""
            SELECT game.game_id
            FROM ${gameTable.name} game, ${gameParticipantTable.name} part
            WHERE game.game_id = part.game_id
            AND game.ended_at IS NULL
            AND game.cancel_yn != 'Y'
            AND part.player_uuid = ?
            AND part.defeat_yn != 'Y'
            ORDER BY game.started_at DESC
            LIMIT 1
        """.trimIndent(), playerUuid).rows.map { it.getString("game_id") }.firstOrNull()
    }

    /**
     * GameType
     */

    fun insertGameType(gameTypeDto: GameTypeDto) {
        gameTypeTable.insertInto()
            .valuesByObject(gameTypeDto)
            .onDuplicateKeyUpdate()
            .execute()
    }

    fun selectGameTypes(): List<GameTypeDto> {
        return gameTypeTable.select()
            .executeList(GameTypeDto::class.java)
    }

    /**
     * GameParticipant
     */

    fun insertGameParticipants(gameParticipantDtos: List<GameParticipantDto>) {
        val context = gameParticipantTable.insertInto()
        gameParticipantDtos.forEach { context.valuesByObject(it) }
        context.execute()
    }

    fun updateGameParticipantDefeat(gameParticipantDto: GameParticipantDto) {
        gameParticipantTable.update()
            .set("defeat_yn = ?")
            .where("player_uuid = ?")
            .parameters(gameParticipantDto.defeatYn, gameParticipantDto.playerUuid)
            .execute()
    }

    fun selectGameParticipantsByGameId(gameId: String, count: Int, offset: Int = 0): List<GameParticipantDto> {
        return gameParticipantTable.select()
            .where("game_id = ?")
            .parameters(gameId)
            .limit(offset, count)
            .executeList(GameParticipantDto::class.java)
    }

    fun selectGameParticipantsByPlayerUuid(playerUuid: String, count: Int, offset: Int = 0): List<GameParticipantDto> {
        return gameParticipantTable.select()
            .where("player_uuid = ?")
            .parameters(playerUuid)
            .limit(offset, count)
            .executeList(GameParticipantDto::class.java)
    }

    /**
     * GameKill
     */

    fun insertGameKill(gameKillDto: GameKillDto) {
        gameKillTable.insertInto()
            .valuesByObject(gameKillDto)
            .execute()
    }

    fun selectGameKillsByGameId(gameId: String, count: Int, offset: Int = 0): List<GameParticipantDto> {
        return gameKillTable.select()
            .where("game_id = ?")
            .parameters(gameId)
            .limit(offset, count)
            .executeList(GameParticipantDto::class.java)
    }

    fun selectGameKillsByKillerPlayerUuid(killerPlayerUuid: String, count: Int, offset: Int = 0): List<GameParticipantDto> {
        return gameKillTable.select()
            .where("killer_player_uuid = ?")
            .parameters(killerPlayerUuid)
            .limit(offset, count)
            .executeList(GameParticipantDto::class.java)
    }

    fun selectGameKillsByDeadPlayerUuid(deadPlayerUuid: String, count: Int, offset: Int = 0): List<GameParticipantDto> {
        return gameKillTable.select()
            .where("dead_player_uuid = ?")
            .parameters(deadPlayerUuid)
            .limit(offset, count)
            .executeList(GameParticipantDto::class.java)
    }

    fun selectGameKillsByPlayerUuid(playerUuid: String, count: Int, offset: Int = 0): List<GameParticipantDto> {
        return gameKillTable.select()
            .where("killer_player_uuid = ? OR dead_player_uuid = ?")
            .parameters(playerUuid, playerUuid)
            .limit(offset, count)
            .executeList(GameParticipantDto::class.java)
    }

    /**
     * GameWinner
     */

    fun insertGameWinner(gameWinnerDtos: List<GameWinnerDto>) {
        val context = gameWinnerTable.insertInto()
        gameWinnerDtos.forEach { context.valuesByObject(it) }
        context.execute()
    }

    fun selectGameWinnersByGameId(gameId: String): List<GameWinnerDto> {
        return gameWinnerTable.select()
            .where("game_id = ?")
            .parameters(gameId)
            .executeList(GameWinnerDto::class.java)
    }

    fun selectGameWinnersByPlayerUuid(playerUuid: String): List<GameWinnerDto> {
        return gameWinnerTable.select()
            .where("winner_player_uuid = ?")
            .parameters(playerUuid)
            .executeList(GameWinnerDto::class.java)
    }

    /**
     * GamePlayerStats
     */

    fun insertGamePlayerStats(gamePlayerStatsDto: GamePlayerStatsDto) {
        gamePlayerStats.insertInto()
            .valuesByObject(gamePlayerStatsDto)
            .execute()
    }

    fun addGamePlayerStatsKillCount(playerUuid: UUID, gameTypeId: Int, addCount: Int) {
        if (selectGamePlayerStatsByPlayerUuidAndGameTypeId(playerUuid.toString(), gameTypeId, 1).isEmpty()) {
            insertGamePlayerStats(
                GamePlayerStatsDto(
                    playerUuid.toString(),
                    gameTypeId,
                    addCount,
                    0,
                    0,
                    0
                )
            )
        } else {
            gamePlayerStats.update()
                .set("kill_count = kill_count + ?")
                .where("player_uuid = ? AND game_type_id = ?")
                .parameters(addCount, playerUuid, gameTypeId)
                .execute()
        }
    }

    fun addGamePlayerStatsDeathCount(playerUuid: UUID, gameTypeId: Int, addCount: Int) {
        if (selectGamePlayerStatsByPlayerUuidAndGameTypeId(playerUuid.toString(), gameTypeId, 1).isEmpty()) {
            insertGamePlayerStats(
                GamePlayerStatsDto(
                    playerUuid.toString(),
                    gameTypeId,
                    0,
                    addCount,
                    0,
                    0
                )
            )
        } else {
            gamePlayerStats.update()
                .set("death_count = death_count + ?")
                .where("player_uuid = ? AND game_type_id = ?")
                .parameters(addCount, playerUuid, gameTypeId)
                .execute()
        }
    }

    fun addGamePlayerStatsAssistCount(playerUuid: UUID, gameTypeId: Int, addCount: Int) {
        if (selectGamePlayerStatsByPlayerUuidAndGameTypeId(playerUuid.toString(), gameTypeId, 1).isEmpty()) {
            insertGamePlayerStats(
                GamePlayerStatsDto(
                    playerUuid.toString(),
                    gameTypeId,
                    0,
                    0,
                    addCount,
                    0
                )
            )
        } else {
            gamePlayerStats.update()
                .set("assist_count = assist_count + ?")
                .where("player_uuid = ? AND game_type_id = ?")
                .parameters(addCount, playerUuid, gameTypeId)
                .execute()
        }
    }

    fun addGamePlayerStatsWinCount(playerUuid: UUID, gameTypeId: Int, addCount: Int) {
        if (selectGamePlayerStatsByPlayerUuidAndGameTypeId(playerUuid.toString(), gameTypeId, 1).isEmpty()) {
            insertGamePlayerStats(
                GamePlayerStatsDto(
                    playerUuid.toString(),
                    gameTypeId,
                    0,
                    0,
                    0,
                    addCount
                )
            )
        } else {
            gamePlayerStats.update()
                .set("win_count = win_count + ?")
                .where("player_uuid = ? AND game_type_id = ?")
                .parameters(addCount, playerUuid, gameTypeId)
                .execute()
        }
    }

    fun selectGamePlayerStats(count: Int, offset: Int = 0): List<GamePlayerStatsDto> {
        return gamePlayerStats.select()
            .limit(offset, count)
            .executeList(GamePlayerStatsDto::class.java)
    }

    fun selectGamePlayerStatsByPlayerUuid(playerUuid: String, count: Int, offset: Int = 0): List<GamePlayerStatsDto> {
        return gamePlayerStats.select()
            .where("player_uuid = ?")
            .parameters(playerUuid)
            .limit(offset, count)
            .executeList(GamePlayerStatsDto::class.java)
    }

    fun selectGamePlayerStatsByGameTypeId(gameTypeId: Int, count: Int, offset: Int = 0): List<GamePlayerStatsDto> {
        return gamePlayerStats.select()
            .where("game_type_id = ?")
            .parameters(gameTypeId)
            .limit(offset, count)
            .executeList(GamePlayerStatsDto::class.java)
    }

    fun selectGamePlayerStatsByPlayerUuidAndGameTypeId(playerUuid: String, gameTypeId: Int, count: Int, offset: Int = 0): List<GamePlayerStatsDto> {
        return gamePlayerStats.select()
            .where("player_uuid = ? AND game_type_id = ?")
            .parameters(playerUuid, gameTypeId)
            .limit(offset, count)
            .executeList(GamePlayerStatsDto::class.java)
    }

}