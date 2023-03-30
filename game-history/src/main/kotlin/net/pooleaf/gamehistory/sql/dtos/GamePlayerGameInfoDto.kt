package net.pooleaf.gamehistory.sql.dtos

import java.time.LocalDateTime

data class GamePlayerGameInfoDto(
    // Game
    var gameId: String? = null,
    var gameTypeId: Int? = -1,
    var startedAt: LocalDateTime? = null,
    var endedAt: LocalDateTime? = null,
    var cancelYn: String? = null,
    var channelName: String? = null,
    // Participant
    var teamId: Int = -1,
    var playerUuid: String? = null,
    var defeatYn: String? = null,
    var teamDefeatYn: String? = null,
    // Stats
    var killCount: Long = -1,
    var deathCount: Long = -1,
//    var assistCount: Int = -1,
    var winCount: Long = -1,
    // Replay
    var replayId: String? = null
) {
}